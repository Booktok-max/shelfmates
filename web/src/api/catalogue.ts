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
