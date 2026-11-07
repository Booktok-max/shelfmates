/**
 * Shared catalogue client for Shelfmates.
 *
 * The catalogue lives in StoryPal's PostgreSQL and is served by its public
 * read API. This client is the only place in the web app that knows about
 * it, so switching the catalogue off (or pointing it somewhere else) is a
 * one-line configuration change rather than an edit to every page.
 *
 * TWO THINGS THIS CLIENT WILL NOT DO
 *
 *   1. It never sends a credential. The catalogue API is a public read API;
 *      the provider credentials all live server-side in the importer. There
 *      is nothing secret to put in the bundle.
 *
 *   2. It never pretends a book is readable. The API reports availability
 *      honestly ("metadata_only", "preview", "external", "full_text") and
 *      this client passes that through unchanged, so the UI can say exactly
 *      what a reader will get.
 *
 * If the catalogue is not configured, `catalogueIsConfigured()` returns
 * false and the app keeps using Google Books exactly as it did before.
 */

export interface CatalogueEdition {
  isbn10: string | null
  isbn13: string | null
  editionTitle: string | null
  publisher: string | null
  publicationDate: string | null
  language: string | null
  pageCount: number | null
}

export interface CatalogueSource {
  provider: string
  externalId: string | null
  sourceUrl: string | null
  license: string | null
  rightsStatus: string | null
  publicDomain: boolean | null
  attributionRequired: boolean
  attributionText: string | null
  redistributionAllowed: boolean
  metadataAvailable: boolean
  coverAvailable: boolean
  previewAvailable: boolean
  fullTextAvailable: boolean
  downloadAvailable: boolean
}

export interface CatalogueReadingOption {
  provider: string
  kind: 'full_text_stored' | 'full_text_external' | 'preview' | 'borrow' | 'purchase'
  format: string | null
  url: string | null
  license: string | null
  attribution: string | null
  territory: string | null
  downloadUrl?: string
}

export interface CatalogueIdentifier {
  provider: string
  scheme: string
  externalId: string
}

export interface CatalogueBook {
  id: string
  title: string
  subtitle: string | null
  description: string | null
  authors: string[]
  language: string | null
  audience: string
  publicationYear: number | null
  pageCount: number | null
  coverUrl: string | null
  coverAttribution: string | null
  availability: 'metadata_only' | 'preview' | 'external' | 'full_text'
  rights: {
    license: string | null
    publicDomain: boolean | null
    attributionRequired: boolean
    jurisdiction: string | null
  }
  subjects: string[]
  genres: string[]
  editions: CatalogueEdition[]
  sources: CatalogueSource[]
  readingOptions: CatalogueReadingOption[]
  identifiers: CatalogueIdentifier[]
  completeness: number
  sourceCount: number
  lastVerifiedAt: string | null
}

export interface CatalogueSearchResponse {
  items: CatalogueBook[]
  total: number
  limit: number
  offset: number
}

export interface CatalogueFacets {
  total: number
  byLanguage: Array<{ value: string; count: number }>
  byAudience: Array<{ value: string; count: number }>
  byAvailability: Array<{ value: string; count: number }>
  bySource: Array<{ value: string; count: number }>
  byGenre: Array<{ value: string; label: string; count: number }>
}

const RAW_BASE = (import.meta.env.VITE_CATALOGUE_API_URL ?? '').trim()

/** The catalogue origin, without a trailing slash. Empty when not configured. */
export const catalogueBase = RAW_BASE.replace(/\/+$/, '')

/** True when the app should query the shared catalogue. */
export function catalogueIsConfigured(): boolean {
  return catalogueBase.length > 0
}

function api(path: string): string {
  if (!catalogueIsConfigured()) throw new Error('The book catalogue is not configured.')
  return `${catalogueBase}${path}`
}

export interface CatalogueQuery {
  q?: string
  language?: string
  audience?: string
  genre?: string
  availability?: string
  source?: string
  readable?: boolean
  limit?: number
  offset?: number
  sort?: 'relevance' | 'title' | 'newest' | 'recently_added'
}

async function readJson<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const detail = await res.text().catch(() => '')
    throw new Error(
      detail
        ? `The book catalogue returned an error (${res.status}).`
        : `The book catalogue is temporarily unavailable (${res.status}).`
    )
  }
  return (await res.json()) as T
}

export async function searchCatalogue(query: CatalogueQuery): Promise<CatalogueSearchResponse> {
  const params = new URLSearchParams()
  if (query.q) params.set('q', query.q)
  if (query.language) params.set('language', query.language)
  if (query.audience) params.set('audience', query.audience)
  if (query.genre) params.set('genre', query.genre)
  if (query.availability) params.set('availability', query.availability)
  if (query.source) params.set('source', query.source)
  if (query.readable) params.set('readable', 'true')
  if (query.limit) params.set('limit', String(query.limit))
  if (query.offset) params.set('offset', String(query.offset))
  if (query.sort) params.set('sort', query.sort)

  const res = await fetch(api(`/api/catalogue/books?${params.toString()}`), {
    headers: { Accept: 'application/json' },
  })
  return readJson<CatalogueSearchResponse>(res)
}

export async function getCatalogueBook(id: string): Promise<CatalogueBook | null> {
  const res = await fetch(api(`/api/catalogue/books/${encodeURIComponent(id)}`), {
    headers: { Accept: 'application/json' },
  })
  if (res.status === 404) return null
  const data = await readJson<{ book: CatalogueBook }>(res)
  return data.book
}

export async function getCatalogueFacets(): Promise<CatalogueFacets | null> {
  try {
    const res = await fetch(api('/api/catalogue/facets'), { headers: { Accept: 'application/json' } })
    return await readJson<CatalogueFacets>(res)
  } catch {
    return null
  }
}

/**
 * The Google Books volume id for a catalogue record, when it has one.
 *
 * The shared catalogue stores every provider identifier a record was seen
 * under, so a book that also exists on Google Books can be saved to a
 * shelf under its Google Books id. That keeps the existing shelf document
 * shape and the Android app's lookup working exactly as they did before --
 * the catalogue enriches discovery without changing how a saved book is
 * identified.
 */
export function googleBooksIdFor(book: CatalogueBook): string | null {
  const match = book.identifiers.find(
    (i) => i.provider === 'google-books' && i.scheme === 'provider'
  )
  return match ? match.externalId : null
}

/** A human label for the availability state. */
export function availabilityLabel(book: CatalogueBook): string {
  switch (book.availability) {
    case 'full_text':
      return 'Read now'
    case 'preview':
      return 'Preview'
    case 'external':
      return 'Read at source'
    default:
      return 'Details only'
  }
}

/**
 * The best legitimate reading destination for a book.
 *
 * A locally-held full text wins, then a full text at the provider, then a
 * preview, then a borrow. A book with no reading option returns null, which
 * the UI renders as "no reading option yet" -- never as a dead link.
 */
export function readingDestination(
  book: CatalogueBook
): { label: string; href: string; kind: CatalogueReadingOption['kind'] } | null {
  const options = book.readingOptions ?? []
  const stored = options.find((o) => o.kind === 'full_text_stored' && o.downloadUrl)
  if (stored?.downloadUrl) {
    return {
      label: 'Read now',
      href: `${catalogueBase}${stored.downloadUrl}`,
      kind: 'full_text_stored',
    }
  }
  const external = options.find((o) => o.kind === 'full_text_external' && o.url)
  if (external?.url) {
    return { label: 'Read at source', href: external.url, kind: 'full_text_external' }
  }
  const preview = options.find((o) => o.kind === 'preview' && o.url)
  if (preview?.url) {
    return { label: 'Preview', href: preview.url, kind: 'preview' }
  }
  const borrow = options.find((o) => o.kind === 'borrow' && o.url)
  if (borrow?.url) {
    return { label: 'Borrow at source', href: borrow.url, kind: 'borrow' }
  }
  return null
}

/** The attribution line a reader should see for a catalogue record. */
export function attributionFor(book: CatalogueBook): string | null {
  const withText = (book.sources ?? []).find((s) => s.attributionText)
  if (withText) return withText.attributionText
  if (book.coverAttribution) return book.coverAttribution
  return null
}
