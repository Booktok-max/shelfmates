import { describe, expect, it } from 'vitest'
import {
  normalizeHandoffIsbn,
  parsePendingCatalogueSave,
  pendingCatalogueSaveKey,
  safeHandoffCoverUrl,
} from './catalogueHandoff'

describe('catalogue handoff validation', () => {
  it('accepts a valid catalogue UUID and keeps the stable identity', () => {
    const result = parsePendingCatalogueSave(JSON.stringify({
      id: '11111111-1111-1111-1111-111111111111',
      isbn: '978-0-14-143951-8',
      description: 'A classic novel about society and relationships.',
    }))
    expect(result.ok).toBe(true)
    if (result.ok) {
      expect(result.value.id).toBe('11111111-1111-1111-1111-111111111111')
      expect(result.value.isbn).toBe('9780141439518')
      expect(result.value.description).toBe('A classic novel about society and relationships.')
    }
  })

  it('trims and bounds optional descriptions without requiring them', () => {
    const result = parsePendingCatalogueSave({ title: 'A Book', author: 'An Author', description: '  A useful blurb.  ' })
    expect(result.ok).toBe(true)
    if (result.ok) expect(result.value.description).toBe('A useful blurb.')
  })

  it('rejects malformed catalogue identifiers instead of treating them as titles', () => {
    const result = parsePendingCatalogueSave({ id: 'catalogue:not-a-uuid', title: 'A Book', author: 'An Author' })
    expect(result).toEqual({ ok: false, reason: 'The catalogue book identifier is invalid.' })
  })

  it('requires both title and author for a metadata handoff', () => {
    expect(parsePendingCatalogueSave({ title: 'A Book' }).ok).toBe(false)
    expect(parsePendingCatalogueSave({ author: 'An Author' }).ok).toBe(false)
  })

  it('drops blank ISBN and unsafe or malformed cover URLs', () => {
    const result = parsePendingCatalogueSave({
      title: 'A Book',
      author: 'An Author',
      isbn: ' --- ',
      coverUrl: 'http://example.com/cover.jpg',
    })
    expect(result.ok).toBe(true)
    if (result.ok) {
      expect(result.value.isbn).toBeUndefined()
      expect(result.value.coverUrl).toBeUndefined()
    }
  })

  it('accepts only credential-free HTTPS cover URLs', () => {
    expect(safeHandoffCoverUrl('https://covers.example/book.jpg')).toBe('https://covers.example/book.jpg')
    expect(safeHandoffCoverUrl('https://user:pass@covers.example/book.jpg')).toBeUndefined()
    expect(safeHandoffCoverUrl('javascript:alert(1)')).toBeUndefined()
  })

  it('normalizes ISBN X and produces a stable retry key', () => {
    expect(normalizeHandoffIsbn('0-8044-2957-x')).toBe('080442957X')
    const first = parsePendingCatalogueSave({ title: 'The Secret Garden', author: 'Frances Hodgson Burnett' })
    const second = parsePendingCatalogueSave({ title: ' the secret garden ', author: 'FRANCES HODGSON BURNETT' })
    expect(first.ok && second.ok && pendingCatalogueSaveKey(first.value)).toBe(
      second.ok ? pendingCatalogueSaveKey(second.value) : '',
    )
  })
})
