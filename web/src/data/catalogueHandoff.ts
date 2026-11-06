export interface PendingCatalogueSave {
  id?: string
  title?: string
  author?: string
  isbn?: string
  coverUrl?: string
}

export type PendingCatalogueSaveResult =
  | { ok: true; value: PendingCatalogueSave }
  | { ok: false; reason: string }

const UUID = /^[0-9a-f]{8}-[0-9a-f]{4}-[1-8][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i

export function normalizeHandoffIsbn(value: unknown): string | undefined {
  const normalized = String(value ?? '').replace(/[^0-9Xx]/g, '').toUpperCase()
  return normalized || undefined
}

export function safeHandoffCoverUrl(value: unknown): string | undefined {
  const raw = String(value ?? '').trim()
  if (!raw) return undefined
  try {
    const url = new URL(raw)
    if (url.protocol !== 'https:' || url.username || url.password) return undefined
    return url.toString()
  } catch {
    return undefined
  }
}

/** Parse and validate the versioned local handoff before any network/write action. */
export function parsePendingCatalogueSave(raw: unknown): PendingCatalogueSaveResult {
  let value: unknown = raw
  if (typeof raw === 'string') {
    try { value = JSON.parse(raw) } catch {
      // Legacy handoffs stored a bare catalogue UUID.
      value = { id: raw }
    }
  }
  if (!value || typeof value !== 'object' || Array.isArray(value)) {
    return { ok: false, reason: 'The saved book details were invalid.' }
  }

  const input = value as Record<string, unknown>
  const id = String(input.id ?? '').trim()
  if (id) {
    if (!UUID.test(id)) return { ok: false, reason: 'The catalogue book identifier is invalid.' }
    return { ok: true, value: { id, isbn: normalizeHandoffIsbn(input.isbn), coverUrl: safeHandoffCoverUrl(input.coverUrl) } }
  }

  const title = String(input.title ?? '').trim()
  const author = String(input.author ?? '').trim()
  if (!title || !author) {
    return { ok: false, reason: 'The book title and author are both required. Nothing has been saved.' }
  }

  return {
    ok: true,
    value: {
      title,
      author,
      isbn: normalizeHandoffIsbn(input.isbn),
      coverUrl: safeHandoffCoverUrl(input.coverUrl),
    },
  }
}

/** Stable key used to prevent duplicate processing while an effect re-runs. */
export function pendingCatalogueSaveKey(pending: PendingCatalogueSave): string {
  if (pending.id) return `id:${pending.id.toLowerCase()}`
  const normalize = (part: string) => part.normalize('NFKC').toLocaleLowerCase().replace(/[^\\p{L}\\p{N}]+/gu, ' ').trim()
  return `book:${normalize(pending.title ?? '')}|${normalize(pending.author ?? '')}|${pending.isbn ?? ''}`
}
