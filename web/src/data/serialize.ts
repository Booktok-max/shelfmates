import type { GBVolume } from '../api/types'
import { coverUrl, isbn13 } from '../api/format'

/**
 * Firestore document shapes must match the Android app exactly, otherwise the
 * two clients will overwrite each other's data in the shared Firestore project.
 *
 * Android reference (app/src/main/java/com/shelfmates/data/model/FirestoreShelfModels.kt
 * and app/src/main/java/com/shelfmates/data/remote/FirebaseService.kt):
 *   users/{uid}/bookshelf/{bookId}      -> FirestoreShelfBook.toMap()
 *   users/{uid}/custom_shelves/{id}     -> FirestoreCustomShelf.toMap()
 *   users/{uid}/reading_progress/{id}   -> FirebaseService.syncReadingProgressToFirestore
 */
export function buildShelfBookDoc(uid: string, volume: GBVolume, category: string) {
  const v = volume.volumeInfo
  const id = `saved_${uid}_${volume.id}`
  return {
    id,
    userId: uid,
    googleBooksId: volume.id,
    title: v.title ?? '',
    authors: (v.authors ?? []).join(', '),
    coverUrl: coverUrl(volume),
    category,
    description: v.description ?? '',
    genre: (v.categories ?? []).join(', '),
    pageCount: v.pageCount ?? 0,
    rating: v.averageRating ?? 0,
    isbn13: isbn13(volume),
    notes: '',
    personalRating: 0,
    savedAt: Date.now(),
    lastUpdated: Date.now(),
  }
}


/** Shared Atomic Shelf catalogue records are recommendations, not Google Books volumes.
 * Keep googleBooksId empty so Android never tries to resolve a catalogue UUID as a GB id.
 */
export function buildCatalogueShelfBookDoc(uid: string, book: {
  id: string; title: string; authors?: string[]; coverUrl?: string | null;
  description?: string | null; genres?: string[]; language?: string | null;
}, category: string) {
  const now = Date.now()
  return {
    id: `saved_${uid}_catalogue_${book.id}`,
    userId: uid,
    googleBooksId: '',
    catalogueBookId: book.id,
    catalogueProvider: 'atomic-shelf',
    savedAsRecommendation: true,
    title: book.title,
    authors: (book.authors ?? []).join(', '),
    coverUrl: book.coverUrl ?? '',
    category,
    description: book.description ?? '',
    genre: (book.genres ?? []).join(', '),
    language: book.language ?? '',
    pageCount: 0,
    rating: 0,
    isbn13: '',
    notes: '',
    personalRating: 0,
    savedAt: now,
    lastUpdated: now,
  }
}


/**
 * Milky may hand off books that are not represented in StoryPal's shared
 * catalogue. Save those as metadata-only recommendations instead of blocking
 * the reader. The deterministic key makes retries idempotent per ISBN, or
 * per normalized title/author when no ISBN is available.
 */
export function buildMilkyRecommendationShelfBookDoc(uid: string, book: {
  title: string
  author: string
  isbn?: string
  coverUrl?: string
  description?: string
  genres?: string[]
}, category: string) {
  const normalizedIsbn = String(book.isbn ?? '').replace(/[^0-9Xx]/g, '').toUpperCase()
  const normalize = (value: string) => value
    .normalize('NFKC')
    .toLocaleLowerCase()
    .replace(/[^\p{L}\p{N}]+/gu, '-')
    .replace(/^-+|-+$/g, '')
  const identity = normalizedIsbn
    ? `isbn-${normalizedIsbn}`
    : `title-${normalize(book.title)}-${normalize(book.author)}`
  const now = Date.now()
  let safeCoverUrl = ''
  try {
    const parsedCoverUrl = new URL(String(book.coverUrl ?? '').trim())
    if (parsedCoverUrl.protocol === 'https:' && !parsedCoverUrl.username && !parsedCoverUrl.password) {
      safeCoverUrl = parsedCoverUrl.toString()
    }
  } catch { /* Missing or invalid cover: the detail page shows a placeholder. */ }
  return {
    id: `saved_${uid}_milky_${identity}`,
    userId: uid,
    googleBooksId: '',
    catalogueBookId: `milky:${identity}`,
    catalogueProvider: 'milky',
    savedAsRecommendation: true,
    title: book.title.trim(),
    authors: book.author.trim(),
    coverUrl: safeCoverUrl,
    category,
    description: String(book.description ?? '').trim(),
    genre: (book.genres ?? []).join(', '),
    pageCount: 0,
    rating: 0,
    isbn13: /^\d{13}$/.test(normalizedIsbn) ? normalizedIsbn : '',
    sourceIsbn: normalizedIsbn,
    notes: '',
    personalRating: 0,
    savedAt: now,
    lastUpdated: now,
  }
}

export function buildCustomShelfDoc(uid: string, name: string, iconEmoji = '📁') {
  const id = `shelf_${uid}_${name.toLowerCase().replace(/\s+/g, '_')}`
  return {
    id,
    userId: uid,
    name,
    iconEmoji,
    isDefault: false,
    createdAt: Date.now(),
    lastUpdated: Date.now(),
  }
}

export function buildReadingProgressDoc(
  uid: string,
  bookId: string,
  lastPage: number,
  lastChapterIndex: number,
  totalPagesOrChapters: number,
  progressPercent: number,
  format: string,
) {
  return {
    userId: uid,
    bookId,
    lastPage,
    lastChapterIndex,
    totalPagesOrChapters,
    progressPercent,
    format,
    lastReadTimestamp: Date.now(),
  }
}

export function buildProfileDoc(uid: string, opts: {
  email?: string | null
  displayName?: string | null
  photoUrl?: string | null
  isAnonymous?: boolean
}) {
  return {
    uid,
    email: opts.email ?? '',
    displayName: opts.displayName ?? 'Reader',
    photoUrl: opts.photoUrl ?? '',
    lastActive: Date.now(),
    isAnonymous: opts.isAnonymous ?? false,
  }
}

export function buildClubMembershipDoc(uid: string, clubId: string) {
  return { userId: uid, clubId, joinedAt: Date.now() }
}

export const DEFAULT_SHELF_CATEGORIES = ['Currently Reading', 'To Read', 'Finished']
