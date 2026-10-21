import { useEffect, useState } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { searchVolumes, coverUrl, authorsStr } from '../api/googleBooks'
import type { GBVolume } from '../api/googleBooks'

export default function Discover() {
  const [params, setParams] = useSearchParams()
  const initial = params.get('q') ?? ''
  const [q, setQ] = useState(initial)
  const [results, setResults] = useState<GBVolume[]>([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [total, setTotal] = useState(0)

  async function run(query: string) {
    if (!query.trim()) return
    setLoading(true); setError(null)
    try {
      const r = await searchVolumes(query.trim())
      setResults(r.items ?? [])
      setTotal(r.totalItems ?? 0)
    } catch (e: any) {
      setError(e?.message ?? 'Search failed')
      setResults([])
    } finally { setLoading(false) }
  }

  useEffect(() => { if (initial) run(initial) }, []) // eslint-disable-line

  function submit(e: React.FormEvent) {
    e.preventDefault()
    setParams({ q })
    run(q)
  }

  const chips = ['Sci-Fi', 'Fantasy', 'Mystery', 'Romance', 'Non-Fiction', 'Biography', 'History', 'Poetry', "Children's"]

  return (
    <div className="sm-container">
      <h1>Discover</h1>
      <form onSubmit={submit} style={{ display: 'flex', gap: 8, margin: '12px 0' }}>
        <input className="sm-input" placeholder="Search books…" value={q} onChange={e => setQ(e.target.value)} />
        <button className="sm-btn sm-btn-primary" type="submit">Search</button>
      </form>
      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 16 }}>
        {chips.map(c => (
          <button key={c} className="sm-chip" onClick={() => { setQ(c); setParams({ q: c }); run(c) }}>{c}</button>
        ))}
      </div>
      {loading && <div className="sm-empty">Searching…</div>}
      {error && <div className="sm-empty"><h3>Search failed</h3><p>{error}</p></div>}
      {!loading && !error && results.length === 0 && initial && <div className="sm-empty"><h3>No results</h3></div>}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(150px, 1fr))', gap: 14 }}>
        {results.map(v => (
          <Link key={v.id} to={`/book/${encodeURIComponent(v.id)}`} style={{ textDecoration: 'none', color: 'inherit' }}>
            <div className="sm-card" style={{ padding: 8 }}>
              {coverUrl(v) ? <img src={coverUrl(v)} alt={v.volumeInfo.title} style={{ width: '100%', aspectRatio: '2/3', objectFit: 'cover', borderRadius: 8 }} /> : <div style={{ width:'100%', aspectRatio:'2/3', background:'var(--sm-surface-alt)', borderRadius:8 }} />}
              <div style={{ fontSize: 13, fontWeight: 700, marginTop: 6, lineHeight: 1.2 }}>{v.volumeInfo.title}</div>
              <div style={{ fontSize: 11, color: 'var(--sm-text-muted)', marginTop: 2 }}>{authorsStr(v)}</div>
            </div>
          </Link>
        ))}
      </div>
      {total > 0 && <p style={{ fontSize: 12, color: 'var(--sm-text-subtle)', marginTop: 12 }}>Showing {results.length} of {total}</p>}
    </div>
  )
}
