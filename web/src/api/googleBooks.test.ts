import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'

/**
 * The Google Books key is optional and, when configured, may be rejected by the
 * API (expired, revoked, or referrer restriction mismatch). A rejected key must
 * degrade to the keyless quota instead of breaking book search.
 *
 * `firebase.ts` reads the key at module load, so each case re-imports the module
 * with a stubbed `googleBooksKey`.
 */
const KEY = 'test-key'

async function loadModule(key: string) {
  vi.resetModules()
  vi.doMock('../firebase', () => ({ googleBooksKey: key }))
  return import('./googleBooks')
}

function jsonResponse(body: unknown, status = 200) {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}

describe('searchVolumes key handling', () => {
  let fetchMock: ReturnType<typeof vi.fn>

  beforeEach(() => {
    fetchMock = vi.fn()
    vi.stubGlobal('fetch', fetchMock)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    vi.doUnmock('../firebase')
  })

  it('appends the key when one is configured', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockResolvedValueOnce(jsonResponse({ totalItems: 0, items: [] }))

    await searchVolumes('dune')

    expect(fetchMock).toHaveBeenCalledTimes(1)
    expect(fetchMock.mock.calls[0][0]).toContain(`key=${KEY}`)
  })

  it('does not call the API with an empty key parameter when unconfigured', async () => {
    const { searchVolumes } = await loadModule('')
    fetchMock.mockResolvedValueOnce(jsonResponse({ totalItems: 0, items: [] }))

    await searchVolumes('dune')

    const url = String(fetchMock.mock.calls[0][0])
    expect(url).not.toContain('key=')
  })

  it('retries without the key when the key is rejected', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock
      .mockResolvedValueOnce(
        jsonResponse(
          { error: { code: 400, message: 'API key expired. Please renew the API key.' } },
          400,
        ),
      )
      .mockResolvedValueOnce(jsonResponse({ totalItems: 1, items: [{ id: 'v1' }] }))

    const result = await searchVolumes('dune')

    expect(result.totalItems).toBe(1)
    expect(fetchMock).toHaveBeenCalledTimes(2)
    expect(String(fetchMock.mock.calls[0][0])).toContain('key=')
    expect(String(fetchMock.mock.calls[1][0])).not.toContain('key=')
  })

  it('reports a rate-limit message when keyless quota is exhausted', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock
      .mockResolvedValueOnce(jsonResponse({ error: {} }, 400))
      .mockResolvedValueOnce(jsonResponse({ error: {} }, 429))

    await expect(searchVolumes('dune')).rejects.toThrow(/rate limit/i)
  })

  it('surfaces a clear error when the key is rejected and keyless also fails', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock
      .mockResolvedValueOnce(jsonResponse({ error: {} }, 403))
      .mockResolvedValueOnce(jsonResponse({ error: {} }, 403))

    await expect(searchVolumes('dune')).rejects.toThrow(/rejected the API key/i)
  })

  it('does not retry keyless when the failure is not key-related', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockResolvedValueOnce(jsonResponse({ error: {} }, 500))

    await expect(searchVolumes('dune')).rejects.toThrow(/Google Books error 500/)
    expect(fetchMock).toHaveBeenCalledTimes(1)
  })
})

describe('getVolume', () => {
  let fetchMock: ReturnType<typeof vi.fn>

  beforeEach(() => {
    fetchMock = vi.fn()
    vi.stubGlobal('fetch', fetchMock)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    vi.doUnmock('../firebase')
  })

  it('fetches a single volume with the configured key', async () => {
    const { getVolume } = await loadModule(KEY)
    fetchMock.mockResolvedValueOnce(jsonResponse({ id: 'v1', volumeInfo: { title: 'Dune' } }))

    const v = await getVolume('v1')

    expect(v.id).toBe('v1')
    expect(String(fetchMock.mock.calls[0][0])).toContain('/volumes/v1')
    expect(String(fetchMock.mock.calls[0][0])).toContain(`key=${KEY}`)
  })
})
