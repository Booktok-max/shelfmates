export interface GBVolume {
  id: string
  volumeInfo: {
    title?: string
    authors?: string[]
    description?: string
    categories?: string[]
    pageCount?: number
    averageRating?: number
    imageLinks?: { thumbnail?: string; smallThumbnail?: string }
    industryIdentifiers?: { type: string; identifier: string }[]
    publishedDate?: string
    publisher?: string
  }
}

export interface GBSearchResult {
  items?: GBVolume[]
  totalItems?: number
}
