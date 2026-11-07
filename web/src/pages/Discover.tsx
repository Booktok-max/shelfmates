import { useEffect, useState } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { searchVolumes, coverUrl, authorsStr } from '../api/googleBooks'
import type { GBVolume } from '../api/googleBooks'
import {
  catalogueIsConfigured,
  searchCatalogue,
  availabilityLabel,
  readingDestination,
  type CatalogueBook,
  type CatalogueFacets,
} from '../api/catalogue'

type Result =
  | { kind: 'catalogue'; book: CatalogueBook }
  | { kind: 'google'; volume: GBVolume }

export default function Discover() {
  const [params, setParams] = useSearchParams()
  const initial = params.get('q') ?? ''
  const [q, setQ] = useState(initial)
  const [results, setResults] = useState<Result[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [notice, setNotice] = useState<string | null>(null)
  const [total, setTotal] = useState(0)
  const [facets, setFacets] = useState<CatalogueFacets | null>(null)
  const [language, setLanguage] = useState('')
  const [readableOnly, setReadableOnly] = useState(false)
  const [genre, setGenre] = useState('')

  const catalogueOn = catalogueIsConfigured()

  useEffect(() => {
    if (!catalogueOn) return
    // The filter counts are a convenience, not a requirement: a failure here
    // must not take the search page down with it.
    let cancelled = false
    import('../api/catalogue')
      .then((m) => m.getCatalogueFacets())
      .then((f) => { if (!cancelled) setFacets(f) })
      .catch(() => { if (!cancelled) setFacets(null) })
    return () => { cancelled = true }
  }, [catalogueOn])

  async function run(query: string) {
    if (!query.trim()) return
    setLoading(true); setError(null); setNotice(null)
    const collected: Result[] = []

    if (catalogueOn) {
      try {
        const r = await searchCatalogue({
          q: query.trim(),
          language: language || undefined,
          genre: genre || undefined,
          readable: readableOnly || undefined,
          limit: 40,
        })
        for (const book of r.items) collected.push({ kind: 'catalogue', book })
        setTotal(r.total)
        if (r.items.length === 0) {
          setNotice('No matches in the shared catalogue. Showing Google Books results instead.')
        }
      } catch {
        // The catalogue being unreachable must not make the page unusable:
        // Google Books still works, so the reader still gets results.
        setNotice('The shared catalogue is not reachable right now. Showing Google Books results instead.')
      }
    }

    if (collected.length === 0) {
      try {
        const r = await searchVolumes(query.trim())
        for (const volume of r.items ?? []) collected.push({ kind: 'google', volume })
        if (!catalogueOn) setTotal(r.totalItems ?? 0)
      } catch (e: any) {
        // The Google Books fallback failed too. The error state below is
        // the honest outcome: no results and a reason.
        if (collected.length === 0) {
          setError(e?.message ?? 'Search failed')
          setResults([])
          setLoading(false)
          return
        }
      }
    }

    setResults(collected)
    setLoading(false)
  }

  useEffect(() => { if (initial) run(initial) }, []) // eslint-disable-line

  function submit(e: React.FormEvent) {
    e.preventDefault()
    setParams({ q })
    run(q)
  }

  const chips = ['Sci-Fi', 'Fantasy', 'Mystery', 'Romance', 'Non-Fiction', 'Biography', 'History', 'Poetry', "Children's"]
  const genreChips = facets?.byGenre?.slice(0, 12) ?? []
  const languageChips = facets?.byLanguage?.filter((l) => l.value !== 'unknown').slice(0, 12) ?? []

  return (
    <div className="sm-container">
      <h1>Discover</h1>
      <form onSubmit={submit} style={{ display: 'flex', gap: 8, margin: '12px 0' }}>
        <input className="sm-input" placeholder="Search books…" value={q} onChange={e => setQ(e.target.value)} />
        <button className="sm-btn sm-btn-primary" type="submit">Search</button>
      </form>
      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 8 }}>
        {chips.map(c => (
          <button key={c} className="sm-chip" onClick={() => { setQ(c); setParams({ q: c }); run(c) }}>{c}</button>
        ))}
      </div>

      {catalogueOn && (
        <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 8, alignItems: 'center' }}>
          <span style={{ fontSize: 12, color: 'var(--sm-text-subtle)' }}>Filter:</span>
          {genreChips.length > 0 && (
            <select
              className="sm-input"
              style={{ width: 'auto', minWidth: 150 }}
              value={genre}
              onChange={e => { setGenre(e.target.value); run(q) }}
            >
              <option value="">All genres</option>
              {genreChips.map(g => (
                <option key={g.value} value={g.value}>{g.label} ({g.count})</option>
              ))}
            </select>
          )}
          {languageChips.length > 0 && (
            <select
              className="sm-input"
              style={{ width: 'auto', minWidth: 130 }}
              value={language}
              onChange={e => { setLanguage(e.target.value); run(q) }}
            >
              <option value="">All languages</option>
              {languageChips.map(l => (
                <option key={l.value} value={l.value}>{l.value} ({l.count})</option>
              ))}
            </select>
          )}
          <button
            className="sm-chip"
            onClick={() => { const next = !readableOnly; setReadableOnly(next); run(q) }}
            style={readableOnly ? { background: 'var(--sm-crimson)', color: '#fff' } : undefined}
          >
            Readable only
          </button>
        </div>
      )}

      {loading && <div className="sm-empty">Searching…</div>}
      {error && <div className="sm-empty"><h3>Search failed</h3><p>{error}</p></div>}
      {notice && !error && (
        <div className="sm-empty" style={{ padding: '8px 12px' }}><p style={{ fontSize: 13 }}>{notice}</p></div>
      )}
      {!loading && !error && results.length === 0 && initial && <div className="sm-empty"><h3>No results</h3></div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: 14 }}>
        {results.map((result) => {
          if (result.kind === 'catalogue') {
            const book = result.book
            const destination = readingDestination(book)
            return (
              <Link key={book.id} to={`/book/${encodeURIComponent(book.id)}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                <div className="sm-card" style={{ padding: 8 }}>
                  {book.coverUrl
                    ? <img src={book.coverUrl} alt={book.title} style={{ width: '100%', aspectRatio: '2/3', objectFit: 'cover', borderRadius: 8 }} />
                    : <div style={{ width: '100%', aspectRatio: '2/3', background: 'var(--sm-surface-alt)', borderRadius: 8 }} />}
                  <div style={{ fontSize: 13, fontWeight: 700, marginTop: 6, lineHeight: 1.2 }}>{book.title}</div>
                  <div style={{ fontSize: 11, color: 'var(--sm-text-muted)', marginTop: 2 }}>{book.authors.join(', ')}</div>
                  <div style={{ fontSize: 10, color: 'var(--sm-text-subtle)', marginTop: 4 }}>
                    {availabilityLabel(book)}{book.language ? ` · ${book.language}` : ''}
                  </div>
                  {destination && (
                    <div style={{ fontSize: 10, color: 'var(--sm-crimson)', marginTop: 2 }}>{destination.label}</div>
                  )}
                </div>
              </Link>
            )
          }
          const v = result.volume
          return (
            <Link key={v.id} to={`/book/${encodeURIComponent(v.id)}`} style={{ textDecoration: 'none', color: 'inherit' }}>
              <div className="sm-card" style={{ padding: 8 }}>
                {coverUrl(v) ? <img src={coverUrl(v)} alt={v.volumeInfo.title} style={{ width: '100%', aspectRatio: '2/3', objectFit: 'cover', borderRadius: 8 }} /> : <div style={{ width:'100%', aspectRatio:'2/3', background:'var(--sm-surface-alt)', borderRadius:8 }} />}
                <div style={{ fontSize: 13, fontWeight: 700, marginTop: 6, lineHeight: 1.2 }}>{v.volumeInfo.title}</div>
                <div style={{ fontSize: 11, color: 'var(--sm-text-muted)', marginTop: 2 }}>{authorsStr(v)}</div>
                <div style={{ fontSize: 10, color: 'var(--sm-text-subtle)', marginTop: 4 }}>Google Books</div>
              </div>
            </Link>
          )
        })}
      </div>
      {total > 0 && <p style={{ fontSize: 12, color: 'var(--sm-text-subtle)', marginTop: 12 }}>Showing {results.length} of {total}</p>}
    </div>
  )
}
