export interface SharedCatalogueBook {
  id: string
  title: string
  authors: string[]
  coverUrl: string | null
  description: string | null
  genres: string[]
  language: string | null
  availability: string
  editions?: Array<{ isbn10?: string | null; isbn13?: string | null; publisher?: string | null; publicationDate?: string | null; format?: string | null }>
  readingOptions?: Array<{ provider: string; kind: string; format?: string | null; url?: string | null; attribution?: string | null; territory?: string | null }>
  sources?: Array<{ provider: string; sourceUrl?: string | null; attributionText?: string | null }>
}

export interface CatalogueLink {
  label: string
  kind: string
  provider: string
  url: string
}

function safeCatalogueUrl(value: unknown): string | null {
  try {
    const url = new URL(String(value ?? '').trim())
    return url.protocol === 'https:' && !url.username && !url.password ? url.toString() : null
  } catch { return null }
}

/** Keep every validated reading/purchase/source destination from the shared catalogue. */
export function catalogueLinks(book: SharedCatalogueBook): CatalogueLink[] {
  const links = new Map<string, CatalogueLink>()
  for (const option of book.readingOptions ?? []) {
    const url = safeCatalogueUrl(option.url)
    if (!url) continue
    const provider = String(option.provider || 'Catalogue').replace(/[-_]/g, ' ')
    const kind = String(option.kind || 'information')
    const label = option.attribution?.trim() || ({ purchase: 'Buy this book', borrow: 'Borrow this book', preview: 'Read a preview', full_text_external: 'Read online', full_text_stored: 'Read online' } as Record<string, string>)[kind] || provider
    links.set(url, { label, kind, provider, url })
  }
  for (const source of book.sources ?? []) {
    const url = safeCatalogueUrl(source.sourceUrl)
    if (!url || links.has(url)) continue
    const provider = String(source.provider || 'Catalogue').replace(/[-_]/g, ' ')
    links.set(url, { label: `View on ${provider}`, kind: 'source', provider, url })
  }
  return [...links.values()]
}

export type CatalogueLookupFailure = 'not-found' | 'ambiguous' | 'retryable' | 'invalid'

export class CatalogueLookupError extends Error {
  readonly kind: CatalogueLookupFailure
  readonly matches: SharedCatalogueBook[]

  constructor(kind: CatalogueLookupFailure, message: string, matches: SharedCatalogueBook[] = []) {
    super(message)
    this.name = 'CatalogueLookupError'
    this.kind = kind
    this.matches = matches
  }
}

const CATALOGUE_API = (import.meta.env.VITE_CATALOGUE_API_URL as string | undefined)?.replace(/\/$/, '')
  || 'https://storypal.atomic-shelf.com/api/catalogue'

async function catalogueJson(url: string): Promise<any> {
  let response: Response
  try {
    response = await fetch(url, { headers: { Accept: 'application/json' } })
  } catch {
    throw new CatalogueLookupError('retryable', 'The catalogue could not be reached. Your save is still pending.')
  }

  if (!response.ok) {
    if (response.status === 404) throw new CatalogueLookupError('invalid', 'The catalogue endpoint or record was not found.')
    if (response.status === 429 || response.status >= 500) {
      throw new CatalogueLookupError('retryable', 'The catalogue is temporarily unavailable. Your save is still pending.')
    }
    throw new CatalogueLookupError('invalid', `The catalogue request was refused (${response.status}).`)
  }

  try {
    return await response.json()
  } catch {
    throw new CatalogueLookupError('retryable', 'The catalogue returned an unreadable response. Your save is still pending.')
  }
}

export async function getSharedCatalogueBook(id: string): Promise<SharedCatalogueBook> {
  if (!/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(id)) {
    throw new CatalogueLookupError('invalid', 'Invalid catalogue book identifier.')
  }
  const data = await catalogueJson(`${CATALOGUE_API}/books/${encodeURIComponent(id)}?audience=adult`)
  if (!data?.book?.id || !data.book.title) {
    throw new CatalogueLookupError('invalid', 'Catalogue returned an invalid book record.')
  }
  return data.book as SharedCatalogueBook
}

function normalize(value: string): string {
  return value.toLocaleLowerCase().normalize('NFKC').replace(/[^\p{L}\p{N}]+/gu, ' ').trim()
}

function normalizeIsbn(value: unknown): string {
  return String(value ?? '').replace(/[^0-9Xx]/g, '').toUpperCase()
}

export async function findSharedCatalogueBook(input: {
  title: string
  author: string
  isbn?: string
}): Promise<SharedCatalogueBook> {
  const isbn = normalizeIsbn(input.isbn)

  // ISBN is the strongest edition identity. Query it directly first so a
  // title variation cannot hide an existing shared-catalogue record.
  if (isbn) {
    const byIsbn = await catalogueJson(`${CATALOGUE_API}/books?q=${encodeURIComponent(`isbn:${isbn}`)}&limit=100&audience=adult`)
    const isbnMatches = (Array.isArray(byIsbn.items) ? byIsbn.items as SharedCatalogueBook[] : []).filter((item) =>
      (item.editions ?? []).some((edition) =>
        [edition.isbn10, edition.isbn13].some((value) => normalizeIsbn(value) === isbn)
      )
    )
    if (isbnMatches.length === 1) return isbnMatches[0]
    if (isbnMatches.length > 1) {
      throw new CatalogueLookupError('ambiguous', 'The ISBN matches more than one catalogue record. Choose the correct edition.', isbnMatches)
    }
  }

  const data = await catalogueJson(`${CATALOGUE_API}/books?q=${encodeURIComponent(input.title)}&limit=100&audience=adult`)
  const items = Array.isArray(data.items) ? data.items as SharedCatalogueBook[] : []
  const title = normalize(input.title)
  const author = normalize(input.author)
  const matches = items.filter((item) =>
    normalize(item.title ?? '') === title && (item.authors ?? []).some((name: string) => normalize(name) === author)
  )
  if (matches.length === 1) return matches[0]
  if (matches.length > 1) {
    throw new CatalogueLookupError('ambiguous', 'More than one exact catalogue match was found. Choose the correct edition.', matches)
  }
  throw new CatalogueLookupError('not-found', 'No exact catalogue match was found.')
}
