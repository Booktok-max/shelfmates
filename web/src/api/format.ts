import type { GBVolume } from './types'

export function coverUrl(v: GBVolume) {
  const links = v.volumeInfo.imageLinks
  const u = links?.thumbnail || links?.smallThumbnail || ''
  return u.replace('http://', 'https://')
}

export function authorsStr(v: GBVolume) {
  return (v.volumeInfo.authors ?? []).join(', ')
}

export function isbn13(v: GBVolume) {
  return v.volumeInfo.industryIdentifiers?.find(i => i.type === 'ISBN_13')?.identifier ?? ''
}
