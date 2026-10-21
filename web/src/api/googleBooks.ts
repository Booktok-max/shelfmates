import { googleBooksKey } from '../firebase'
import type { GBSearchResult, GBVolume } from './types'

const BASE = 'https://www.googleapis.com/books/v1'

/**
 * Google Books accepts keyless requests but applies a small per-IP daily quota.
 * A configured key raises that quota. The key is a *client* credential and is
 * shipped in the bundle on purpose (it must be restricted by HTTP referrer in
 * the Google Cloud console). It is never a privileged secret.
 *
 * The key is sent as `X-Goog-Api-Key` rather than `?key=` so it never appears in
 * a request URL, a browser history entry, or a service-worker cache key. Google
 * allows this header in its CORS preflight response.
 */
export function keyHeaders(): Record<string, string> | undefined {
  return googleBooksKey ? { 'X-Goog-Api-Key': googleBooksKey } : undefined
}

/**
 * Google Books treats an unqualified query as broad full-text matching. For
 * known topic shortcuts, scope the request to the subject field so a chip such
 * as "Romance" finds romance-category books instead of any book that mentions
 * the word. Leave arbitrary user queries and explicit API operators untouched.
 */
const SUBJECT_QUERIES: Readonly<Record<string, string>> = {
  'sci-fi': 'subject:"science fiction"',
  'science fiction': 'subject:"science fiction"',
  fantasy: 'subject:fantasy',
  mystery: 'subject:mystery',
  romance: 'subject:romance',
  'non-fiction': 'subject:nonfiction',
  nonfiction: 'subject:nonfiction',
  biography: 'subject:biography',
  history: 'subject:history',
  poetry: 'subject:poetry',
  "children's": 'subject:"juvenile fiction"',
  'children’s': 'subject:"juvenile fiction"',
  children: 'subject:"juvenile fiction"',
};

function queryForSearch(input: string): string {
  const query = input.trim();
  if (!query || /^(intitle|inauthor|inpublisher|subject|isbn|lccn|oclc):/i.test(query)) {
    return query;
  }
  return SUBJECT_QUERIES[query.toLowerCase()] ?? query;
}

/**
 * The Books `volumes?q=` endpoint has returned intermittent 503 `backendFailed`
 * errors for every caller since April 2026 (observed ~17% success on cold
 * requests). It is a provider-side defect, so a short exponential backoff is
 * the only client-side mitigation. Only transient failures are retried: 5xx and
 * network errors. A 429 is a daily quota and retrying it would only add load.
 */
const RETRY_DELAYS_MS = [400, 900, 1800]

function isTransient(status: number) {
  return status >= 500 && status <= 599
}

function sleep(ms: number) {
  return new Promise<void>((resolve) => setTimeout(resolve, ms))
}

/** Fetch once, retrying transient failures with exponential backoff + jitter. */
async function fetchWithRetry(url: string, headers?: Record<string, string>): Promise<Response> {
  let lastFailure: Response | Error | undefined

  for (let attempt = 0; attempt <= RETRY_DELAYS_MS.length; attempt++) {
    try {
      const res = await fetch(url, headers ? { headers } : undefined)
      if (!isTransient(res.status)) return res
      lastFailure = res
    } catch (err) {
      lastFailure = err instanceof Error ? err : new Error(String(err))
    }

    const delay = RETRY_DELAYS_MS[attempt]
    if (delay !== undefined) {
      // Jitter keeps a burst of clients from synchronising their retries.
      await sleep(delay + Math.floor(Math.random() * (delay / 4)))
    }
  }

  if (lastFailure instanceof Response) return lastFailure
  // Never surface "Failed to fetch" to the reader; the cause is always the
  // same from their point of view as a transient upstream failure.
  throw new Error('Google Books is temporarily unavailable. Please try again in a moment.')
}

/**
 * Fetch a Google Books URL, retrying without the API key when the key itself is
 * rejected (expired / revoked / wrong restrictions). A broken key must degrade
 * to the keyless quota rather than break search entirely.
 */
async function gbFetch(path: string): Promise<Response> {
  const url = `${BASE}${path}`
  if (googleBooksKey) {
    const keyed = await fetchWithRetry(url, keyHeaders())
    if (keyed.ok) return keyed
    // Only fall back when the key is the problem, not when the query is bad.
    if (keyed.status === 400 || keyed.status === 401 || keyed.status === 403) {
      return fetchWithRetry(url)
    }
    return keyed
  }
  return fetchWithRetry(url)
}

function describeFailure(status: number): string {
  if (status === 429) {
    return 'Google Books rate limit reached. Please try again later.'
  }
  if (status === 400 || status === 401 || status === 403) {
    return 'Google Books rejected the API key. Book search is unavailable until it is renewed.'
  }
  if (status >= 500) {
    return 'Google Books is temporarily unavailable. Please try again in a moment.'
  }
  return `Google Books error ${status}`
}

async function readJson<T>(res: Response): Promise<T> {
  if (!res.ok) throw new Error(describeFailure(res.status))
  return res.json() as Promise<T>
}

export async function searchVolumes(q: string, startIndex = 0, maxResults = 20): Promise<GBSearchResult> {
  const res = await gbFetch(
    `/volumes?q=${encodeURIComponent(queryForSearch(q))}&startIndex=${startIndex}&maxResults=${maxResults}&printType=books`,
  )
  return readJson<GBSearchResult>(res)
}

export async function getVolume(id: string): Promise<GBVolume> {
  const res = await gbFetch(`/volumes/${encodeURIComponent(id)}`)
  return readJson<GBVolume>(res)
}

export { coverUrl, authorsStr, isbn13 } from './format'
export type { GBSearchResult, GBVolume } from './types'
