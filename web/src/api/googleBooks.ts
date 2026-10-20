import { googleBooksKey } from '../firebase'
import type { GBSearchResult, GBVolume } from './types'

const BASE = 'https://www.googleapis.com/books/v1'

/**
 * Google Books accepts keyless requests but applies a small per-IP daily quota.
 * A configured key raises that quota. The key is a *client* credential and is
 * shipped in the bundle on purpose (it must be restricted by HTTP referrer in
 * the Google Cloud console). It is never a privileged secret.
 */
export function withKey(url: string) {
  if (!googleBooksKey) return url
  const sep = url.includes('?') ? '&' : '?'
  return `${url}${sep}key=${encodeURIComponent(googleBooksKey)}`
}

/**
 * Fetch a Google Books URL, retrying without the API key when the key itself is
 * rejected (expired / revoked / wrong restrictions). A broken key must degrade
 * to the keyless quota rather than break search entirely.
 */
async function gbFetch(path: string): Promise<Response> {
  if (googleBooksKey) {
    const keyed = await fetch(withKey(`${BASE}${path}`))
    if (keyed.ok) return keyed
    // Only fall back when the key is the problem, not when the query is bad.
    if (keyed.status === 400 || keyed.status === 401 || keyed.status === 403) {
      const plain = await fetch(`${BASE}${path}`)
      if (plain.ok) return plain
      return plain
    }
    return keyed
  }
  return fetch(`${BASE}${path}`)
}

function describeFailure(status: number): string {
  if (status === 429) {
    return 'Google Books rate limit reached. Please try again later.'
  }
  if (status === 400 || status === 403) {
    return 'Google Books rejected the API key. Book search is unavailable until it is renewed.'
  }
  return `Google Books error ${status}`
}

async function readJson<T>(res: Response): Promise<T> {
  if (!res.ok) throw new Error(describeFailure(res.status))
  return res.json() as Promise<T>
}

export async function searchVolumes(q: string, startIndex = 0, maxResults = 20): Promise<GBSearchResult> {
  const res = await gbFetch(
    `/volumes?q=${encodeURIComponent(q)}&startIndex=${startIndex}&maxResults=${maxResults}&printType=books`,
  )
  return readJson<GBSearchResult>(res)
}

export async function getVolume(id: string): Promise<GBVolume> {
  const res = await gbFetch(`/volumes/${encodeURIComponent(id)}`)
  return readJson<GBVolume>(res)
}

export { coverUrl, authorsStr, isbn13 } from './format'
export type { GBSearchResult, GBVolume } from './types'
