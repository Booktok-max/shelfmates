import { afterEach, describe, expect, it, vi } from 'vitest'
import { findSharedCatalogueBook } from './catalogue'

const book = (id: string, title: string, authors: string[], editions: Array<{ isbn10?: string; isbn13?: string }> = []) => ({
  id, title, authors, editions, coverUrl: null, description: null, genres: [], language: 'en', availability: 'metadata_only',
})

afterEach(() => {
  vi.unstubAllGlobals()
})

describe('shared catalogue matching', () => {
  it('resolves one exact title-and-author match', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ items: [book('one', 'Pride and Prejudice', ['Jane Austen'])] }),
    }))
    const result = await findSharedCatalogueBook({ title: 'Pride and Prejudice', author: 'Jane Austen' })
    expect(result.id).toBe('one')
  })

  it('queries by ISBN first and returns the matching catalogue description', async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce({
        ok: true,
        json: async () => ({ items: [book('isbn-match', 'Pride and Prejudice', ['Jane Austen'], [{ isbn13: '9780141439518' }]).description = 'A proper catalogue blurb'] }),
      })
    vi.stubGlobal('fetch', fetchMock)
    const result = await findSharedCatalogueBook({
      title: 'Pride and Prejudice',
      author: 'Jane Austen',
      isbn: '978-0-14-143951-8',
    })
    expect(result.id).toBe('isbn-match')
    expect(new URL(String(fetchMock.mock.calls[0][0])).searchParams.get('q')).toBe('isbn:9780141439518')
    vi.unstubAllGlobals()
  })

  it('returns an explicit ambiguous result instead of silently choosing an edition', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ items: [
        book('one', 'The Secret Garden', ['Frances Hodgson Burnett'], [{ isbn13: '9780000000001' }]),
        book('two', 'The Secret Garden', ['Frances Hodgson Burnett'], [{ isbn13: '9780000000001' }]),
      ] }),
    }))
    await expect(findSharedCatalogueBook({
      title: 'The Secret Garden', author: 'Frances Hodgson Burnett', isbn: '9780000000001',
    })).rejects.toMatchObject({ kind: 'ambiguous', matches: expect.arrayContaining([expect.objectContaining({ id: 'one' })]) })
  })

  it('classifies a true no-match as not-found so the caller can save a metadata recommendation', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
      ok: true,
      json: async () => ({ items: [] }),
    }))
    await expect(findSharedCatalogueBook({ title: 'A Modern Novel', author: 'An Author' }))
      .rejects.toMatchObject({ kind: 'not-found' })
  })

  it('treats an HTTP 404 as a permanent endpoint or record failure', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: false, status: 404 }))
    await expect(findSharedCatalogueBook({ title: 'A Book', author: 'An Author' }))
      .rejects.toMatchObject({ kind: 'invalid' })
  })

  it('keeps transient server failures retryable instead of treating them as a catalogue miss', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: false, status: 503 }))
    await expect(findSharedCatalogueBook({ title: 'A Book', author: 'An Author' }))
      .rejects.toMatchObject({ kind: 'retryable' })
  })

  it('keeps network failures retryable', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new Error('offline')))
    await expect(findSharedCatalogueBook({ title: 'A Book', author: 'An Author' }))
      .rejects.toMatchObject({ kind: 'retryable' })
  })
})
