export interface SharedCatalogueBook {
  id: string
  title: string
  authors: string[]
  coverUrl: string | null
  description: string | null
  genres: string[]
  language: string | null
  availability: string
}

const CATALOGUE_API = (import.meta.env.VITE_CATALOGUE_API_URL as string | undefined)?.replace(/\\/$/, '')
  || 'https://storypal.atomic-shelf.com/api/catalogue'

export async function getSharedCatalogueBook(id: string): Promise<SharedCatalogueBook> {
  if (!/^[0-9a-f-]{36}$/i.test(id)) throw new Error('Invalid catalogue book identifier.')
  const response = await fetch(`${CATALOGUE_API}/books/${encodeURIComponent(id)}`, {
    headers: { Accept: 'application/json' },
  })
  if (!response.ok) throw new Error(response.status === 404 ? 'Catalogue book not found.' : 'Catalogue is temporarily unavailable.')
  const data = await response.json()
  if (!data?.book?.id || !data.book.title) throw new Error('Catalogue returned an invalid book record.')
  return data.book as SharedCatalogueBook
}

export async function findSharedCatalogueBook(input: { title: string; author: string; isbn?: string }): Promise<SharedCatalogueBook> {
  const query = input.isbn || input.title
  const response = await fetch(`${CATALOGUE_API}/books?q=${encodeURIComponent(query)}&limit=100`, {
    headers: { Accept: 'application/json' },
  })
  if (!response.ok) throw new Error('Catalogue search is temporarily unavailable.')
  const data = await response.json()
  const normalize = (value: string) => value.toLocaleLowerCase().normalize('NFKC').replace(/[^\\p{L}\\p{N}]+/gu, ' ').trim()
  const title = normalize(input.title)
  const author = normalize(input.author)
  const matches = (data.items ?? []).filter((item: any) =>
    normalize(item.title ?? '') === title && (item.authors ?? []).some((name: string) => normalize(name) === author)
  )
  if (matches.length !== 1) throw new Error('No unique exact match was found in the shared catalogue.')
  return matches[0] as SharedCatalogueBook
}
