import { describe, it, expect } from 'vitest'
import {
  catalogueIsConfigured,
  googleBooksIdFor,
  availabilityLabel,
  readingDestination,
  attributionFor,
  type CatalogueBook,
} from '../api/catalogue'

/**
 * The catalogue client is the only place the web app knows about the
 * shared catalogue, so the rules it enforces are worth pinning:
 *
 *   - it reports availability exactly as the catalogue states it, and never
 *     dresses a metadata-only record up as a readable book;
 *   - it never invents a reading destination;
 *   - it prefers the Google Books id when the catalogue knows one, so a book
 *     saved to a shelf still resolves in the Android app.
 */

function book(overrides: Partial<CatalogueBook> = {}): CatalogueBook {
  return {
    id: '11111111-1111-1111-1111-111111111111',
    title: 'A Book',
    subtitle: null,
    description: null,
    authors: ['An Author'],
    language: 'en',
    audience: 'adult',
    publicationYear: 1900,
    pageCount: 100,
    coverUrl: null,
    coverAttribution: null,
    availability: 'metadata_only',
    rights: { license: null, publicDomain: null, attributionRequired: false, jurisdiction: null },
    subjects: [],
    genres: [],
    editions: [],
    sources: [],
    readingOptions: [],
    identifiers: [],
    completeness: 50,
    sourceCount: 1,
    lastVerifiedAt: null,
    ...overrides,
  }
}

describe('catalogue client: availability', () => {
  it('is not configured when no catalogue origin is set', () => {
    // The test environment has no VITE_CATALOGUE_API_URL, which is the
    // "leave the app on Google Books" configuration.
    expect(catalogueIsConfigured()).toBe(false)
  })

  it('labels a metadata-only record as details only', () => {
    expect(availabilityLabel(book())).toBe('Details only')
  })

  it('labels each availability state distinctly', () => {
    expect(availabilityLabel(book({ availability: 'preview' }))).toBe('Preview')
    expect(availabilityLabel(book({ availability: 'external' }))).toBe('Read at source')
    expect(availabilityLabel(book({ availability: 'full_text' }))).toBe('Read now')
  })
})

describe('catalogue client: reading destination', () => {
  it('offers no destination for a metadata-only record', () => {
    expect(readingDestination(book())).toBeNull()
  })

  it('prefers a locally stored full text', () => {
    const destination = readingDestination(
      book({
        availability: 'full_text',
        readingOptions: [
          {
            provider: 'standardebooks',
            kind: 'full_text_external',
            format: 'EPUB',
            url: 'https://standardebooks.org/ebooks/x/y',
            license: null,
            attribution: null,
            territory: null,
          },
          {
            provider: 'standardebooks',
            kind: 'full_text_stored',
            format: 'EPUB',
            url: 'https://standardebooks.org/ebooks/x/y',
            license: null,
            attribution: null,
            territory: 'US',
            downloadUrl: '/api/free-library/books/standardebooks%3Ax%2Fy/download',
          },
        ],
      })
    )
    expect(destination?.kind).toBe('full_text_stored')
    expect(destination?.href).toContain('/api/free-library/books/')
  })

  it('falls back to a full text at the provider', () => {
    const destination = readingDestination(
      book({
        availability: 'full_text',
        readingOptions: [
          {
            provider: 'gutenberg',
            kind: 'full_text_external',
            format: 'EPUB',
            url: 'https://www.gutenberg.org/ebooks/1',
            license: null,
            attribution: null,
            territory: 'US',
          },
        ],
      })
    )
    expect(destination?.kind).toBe('full_text_external')
    expect(destination?.href).toBe('https://www.gutenberg.org/ebooks/1')
  })

  it('offers a borrow destination when that is all there is', () => {
    const destination = readingDestination(
      book({
        availability: 'external',
        readingOptions: [
          {
            provider: 'openlibrary',
            kind: 'borrow',
            format: null,
            url: 'https://openlibrary.org/works/OL1W',
            license: null,
            attribution: null,
            territory: null,
          },
        ],
      })
    )
    expect(destination?.kind).toBe('borrow')
  })

  it('ignores a reading option with no URL rather than linking to nothing', () => {
    const destination = readingDestination(
      book({
        readingOptions: [
          {
            provider: 'x',
            kind: 'preview',
            format: null,
            url: null,
            license: null,
            attribution: null,
            territory: null,
          },
        ],
      })
    )
    expect(destination).toBeNull()
  })
})

describe('catalogue client: shelf identity', () => {
  it('uses the Google Books id when the catalogue knows one', () => {
    expect(
      googleBooksIdFor(
        book({
          identifiers: [
            { provider: 'openlibrary', scheme: 'provider', externalId: 'OL1W' },
            { provider: 'google-books', scheme: 'provider', externalId: 'gbook123' },
          ],
        })
      )
    ).toBe('gbook123')
  })

  it('returns null when the catalogue has no Google Books id', () => {
    expect(
      googleBooksIdFor(book({ identifiers: [{ provider: 'openlibrary', scheme: 'provider', externalId: 'OL1W' }] }))
    ).toBeNull()
  })
})

describe('catalogue client: attribution', () => {
  it('prefers the source attribution text', () => {
    expect(
      attributionFor(
        book({
          coverAttribution: 'Cover credit',
          sources: [
            {
              provider: 'gutenberg',
              externalId: '1',
              sourceUrl: null,
              license: null,
              rightsStatus: 'PUBLIC_DOMAIN',
              publicDomain: true,
              attributionRequired: true,
              attributionText: 'Source: Project Gutenberg',
              redistributionAllowed: false,
              metadataAvailable: true,
              coverAvailable: false,
              previewAvailable: false,
              fullTextAvailable: true,
              downloadAvailable: false,
            },
          ],
        })
      )
    ).toBe('Source: Project Gutenberg')
  })

  it('falls back to the cover attribution', () => {
    expect(attributionFor(book({ coverAttribution: 'Cover credit' }))).toBe('Cover credit')
  })

  it('returns null when there is nothing to attribute', () => {
    expect(attributionFor(book())).toBeNull()
  })
})
