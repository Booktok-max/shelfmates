import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'

/**
 * The Google Books key is optional and, when configured, may be rejected by the
 * API (expired, revoked, or referrer restriction mismatch). A rejected key must
 * degrade to the keyless quota instead of breaking book search.
 *
 * The key is sent as the `X-Goog-Api-Key` header so it never appears in a URL or
 * a service-worker cache key.
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

type Call = [string, RequestInit | undefined]

function headersOf(call: Call): Record<string, string> {
  return (call[1]?.headers ?? {}) as Record<string, string>
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

  it('sends the key as a header and never in the URL', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockResolvedValueOnce(jsonResponse({ totalItems: 0, items: [] }))

    await searchVolumes('dune')

    const call = fetchMock.mock.calls[0] as Call
    expect(fetchMock).toHaveBeenCalledTimes(1)
    expect(headersOf(call)['X-Goog-Api-Key']).toBe(KEY)
    expect(String(call[0])).not.toContain('key=')
    expect(String(call[0])).not.toContain(KEY)
  })

  it('makes a keyless, credential-free request when unconfigured', async () => {
    const { searchVolumes } = await loadModule('')
    fetchMock.mockResolvedValueOnce(jsonResponse({ totalItems: 0, items: [] }))

    await searchVolumes('dune')

    const call = fetchMock.mock.calls[0] as Call
    expect(String(call[0])).not.toContain('key=')
    expect(headersOf(call)['X-Goog-Api-Key']).toBeUndefined()
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
    expect(headersOf(fetchMock.mock.calls[0] as Call)['X-Goog-Api-Key']).toBe(KEY)
    expect(headersOf(fetchMock.mock.calls[1] as Call)['X-Goog-Api-Key']).toBeUndefined()
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
    fetchMock.mockResolvedValueOnce(jsonResponse({ error: {} }, 418))

    await expect(searchVolumes('dune')).rejects.toThrow(/Google Books error 418/)
    expect(fetchMock).toHaveBeenCalledTimes(1)
  })
})

/**
 * The Books `volumes?q=` endpoint intermittently returns 503 `backendFailed`
 * for all callers (provider-side, observed since April 2026), so transient
 * failures must be retried before the UI shows an error.
 */
describe('searchVolumes transient failure handling', () => {
  let fetchMock: ReturnType<typeof vi.fn>

  beforeEach(() => {
    fetchMock = vi.fn()
    vi.stubGlobal('fetch', fetchMock)
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
    vi.unstubAllGlobals()
    vi.doUnmock('../firebase')
  })

  /**
   * Run the request, let all backoff timers elapse, then settle. The outcome is
   * captured immediately so a rejection raised during the timer advance is
   * never left unhandled.
   */
  async function settle<T>(run: () => Promise<T>): Promise<T> {
    const outcome = run().then(
      (value) => ({ ok: true as const, value }),
      (error) => ({ ok: false as const, error }),
    )
    await vi.advanceTimersByTimeAsync(30_000)
    const result = await outcome
    if (!result.ok) throw result.error
    return result.value
  }

  it('retries a transient 503 and returns the results once the backend recovers', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock
      .mockResolvedValueOnce(jsonResponse({ error: { code: 503 } }, 503))
      .mockResolvedValueOnce(jsonResponse({ error: { code: 503 } }, 503))
      .mockResolvedValueOnce(jsonResponse({ totalItems: 1, items: [{ id: 'v1' }] }))

    const result = await settle(() => searchVolumes('dune'))

    expect(result.totalItems).toBe(1)
    expect(fetchMock).toHaveBeenCalledTimes(3)
    expect(headersOf(fetchMock.mock.calls[2] as Call)['X-Goog-Api-Key']).toBe(KEY)
  })

  it('gives up after the retry budget and reports a temporary outage', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockResolvedValue(jsonResponse({ error: { code: 503 } }, 503))

    await expect(settle(() => searchVolumes('dune'))).rejects.toThrow(/temporarily unavailable/i)
    // 1 initial attempt + 3 backoff retries.
    expect(fetchMock).toHaveBeenCalledTimes(4)
  })

  it('retries network errors instead of failing on the first throw', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock
      .mockRejectedValueOnce(new TypeError('Failed to fetch'))
      .mockResolvedValueOnce(jsonResponse({ totalItems: 2, items: [{ id: 'v1' }] }))

    const result = await settle(() => searchVolumes('dune'))

    expect(result.totalItems).toBe(2)
    expect(fetchMock).toHaveBeenCalledTimes(2)
  })

  it('reports a temporary outage when every attempt fails at the network layer', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockRejectedValue(new TypeError('Failed to fetch'))

    await expect(settle(() => searchVolumes('dune'))).rejects.toThrow(/temporarily unavailable/i)
    expect(fetchMock).toHaveBeenCalledTimes(4)
  })

  it('does not retry an exhausted daily quota', async () => {
    const { searchVolumes } = await loadModule('')
    fetchMock.mockResolvedValueOnce(jsonResponse({ error: { code: 429 } }, 429))

    await expect(settle(() => searchVolumes('dune'))).rejects.toThrow(/rate limit/i)
    expect(fetchMock).toHaveBeenCalledTimes(1)
  })
})


describe('searchVolumes topic queries', () => {
  let fetchMock: ReturnType<typeof vi.fn>

  beforeEach(() => {
    fetchMock = vi.fn()
    vi.stubGlobal('fetch', fetchMock)
  })

  afterEach(() => {
    vi.unstubAllGlobals()
    vi.doUnmock('../firebase')
  })

  it('searches the Romance topic by subject instead of broad full text', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockResolvedValueOnce(jsonResponse({ totalItems: 1, items: [] }))

    await searchVolumes('Romance')

    expect(new URL(String(fetchMock.mock.calls[0][0])).searchParams.get('q')).toBe('subject:romance')
  })

  it('maps Sci-Fi to the Science Fiction subject phrase', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockResolvedValueOnce(jsonResponse({ totalItems: 1, items: [] }))

    await searchVolumes('Sci-Fi')

    expect(new URL(String(fetchMock.mock.calls[0][0])).searchParams.get('q')).toBe('subject:"science fiction"')
  })

  it('keeps ordinary free-text searches unchanged', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockResolvedValueOnce(jsonResponse({ totalItems: 1, items: [] }))

    await searchVolumes('the secret garden')

    expect(new URL(String(fetchMock.mock.calls[0][0])).searchParams.get('q')).toBe('the secret garden')
  })

  it('preserves explicit Google Books query operators', async () => {
    const { searchVolumes } = await loadModule(KEY)
    fetchMock.mockResolvedValueOnce(jsonResponse({ totalItems: 1, items: [] }))

    await searchVolumes('inauthor:Austen')

    expect(new URL(String(fetchMock.mock.calls[0][0])).searchParams.get('q')).toBe('inauthor:Austen')
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

    const call = fetchMock.mock.calls[0] as Call
    expect(v.id).toBe('v1')
    expect(String(call[0])).toContain('/volumes/v1')
    expect(headersOf(call)['X-Goog-Api-Key']).toBe(KEY)
  })
})
