import { describe, it, expect } from 'vitest'
import {
  buildShelfBookDoc,
  buildCustomShelfDoc,
  buildReadingProgressDoc,
  buildProfileDoc,
  buildClubMembershipDoc,
} from '../data/serialize'
import type { GBVolume } from '../api/types'

const volume: GBVolume = {
  id: 'abc123',
  volumeInfo: {
    title: 'The Pragmatic Programmer',
    authors: ['Andrew Hunt', 'David Thomas'],
    description: 'A classic.',
    categories: ['Computers'],
    pageCount: 352,
    averageRating: 4.4,
    imageLinks: { thumbnail: 'http://books.google.com/t.jpg' },
    industryIdentifiers: [{ type: 'ISBN_13', identifier: '9780135957059' }],
  },
}

describe('buildShelfBookDoc', () => {
  it('matches the Android FirestoreShelfBook field names', () => {
    const doc = buildShelfBookDoc('uid1', volume, 'To Read')
    expect(Object.keys(doc).sort()).toEqual([
      'authors', 'category', 'coverUrl', 'description', 'genre', 'id', 'isbn13',
      'lastUpdated', 'notes', 'pageCount', 'personalRating', 'rating', 'savedAt',
      'title', 'userId', 'googleBooksId',
    ].sort())
    expect(doc.userId).toBe('uid1')
    expect(doc.googleBooksId).toBe('abc123')
    expect(doc.id).toBe('saved_uid1_abc123')
    expect(doc.authors).toBe('Andrew Hunt, David Thomas')
    expect(doc.category).toBe('To Read')
    expect(doc.pageCount).toBe(352)
    expect(doc.isbn13).toBe('9780135957059')
  })

  it('upgrades the cover url to https', () => {
    expect(buildShelfBookDoc('u', volume, 'To Read').coverUrl).toBe('https://books.google.com/t.jpg')
  })
})

describe('buildCustomShelfDoc', () => {
  it('uses the Android shelf id convention', () => {
    const doc = buildCustomShelfDoc('uid1', 'Sci-Fi Favorites')
    expect(doc.id).toBe('shelf_uid1_sci-fi_favorites')
    expect(doc.userId).toBe('uid1')
    expect(doc.name).toBe('Sci-Fi Favorites')
    expect(doc.isDefault).toBe(false)
  })
})

describe('buildReadingProgressDoc', () => {
  it('matches the Android reading_progress fields', () => {
    const doc = buildReadingProgressDoc('uid1', 'book-9', 3, 3, 12, 25, 'EPUB')
    expect(doc).toMatchObject({
      userId: 'uid1',
      bookId: 'book-9',
      lastPage: 3,
      lastChapterIndex: 3,
      totalPagesOrChapters: 12,
      progressPercent: 25,
      format: 'EPUB',
    })
    expect(typeof doc.lastReadTimestamp).toBe('number')
  })
})

describe('buildProfileDoc', () => {
  it('keeps the uid equal to the path owner', () => {
    const doc = buildProfileDoc('uid1', { email: 'a@b.c', displayName: 'Ada', photoUrl: 'p', isAnonymous: false })
    expect(doc.uid).toBe('uid1')
    expect(doc.email).toBe('a@b.c')
    expect(doc.displayName).toBe('Ada')
  })

  it('never writes a role field (role is privileged in the rules)', () => {
    const doc = buildProfileDoc('uid1', {})
    expect('role' in doc).toBe(false)
  })
})

describe('buildClubMembershipDoc', () => {
  it('binds membership to the owner', () => {
    const doc = buildClubMembershipDoc('uid1', 'club_42')
    expect(doc.userId).toBe('uid1')
    expect(doc.clubId).toBe('club_42')
  })
})
