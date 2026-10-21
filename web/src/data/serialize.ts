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
