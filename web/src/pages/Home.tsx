import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { useCustomShelves, useShelfBooks } from '../data/shelves'

export default function Home() {
  const { user } = useAuth()
  const nav = useNavigate()
  const [q, setQ] = useState('')
  const { shelves } = useCustomShelves(user?.uid ?? null)
  const { books } = useShelfBooks(user?.uid ?? null)

  const firstName = (user?.displayName ?? 'Reader').split(' ')[0]
  const grouped: Record<string, number> = {}
  for (const b of books) grouped[b.category] = (grouped[b.category] ?? 0) + 1

  function search(e: React.FormEvent) {
    e.preventDefault()
    if (q.trim()) nav(`/discover?q=${encodeURIComponent(q.trim())}`)
  }

  return (
    <div className="sm-container">
      <div style={{ marginBottom: 16 }}>
        <h1 style={{ fontSize: 26, margin: 0 }}>Welcome back, {firstName}</h1>
        <p style={{ color: 'var(--sm-text-muted)', margin: '4px 0 0' }}>
          {books.length} saved · {shelves.length} shelves
        </p>
      </div>

      <form onSubmit={search} className="sm-card" style={{ display: 'flex', gap: 8, padding: 12 }}>
        <input className="sm-input" placeholder="Search books, authors, topics…" value={q} onChange={e => setQ(e.target.value)} />
        <button className="sm-btn sm-btn-primary" type="submit">Search</button>
      </form>

      <div className="sm-section-title">Your shelves</div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))', gap: 12 }}>
        {['Currently Reading', 'To Read', 'Finished'].map(name => (
          <Link key={name} to="/shelves" className="sm-card" style={{ textDecoration: 'none', color: 'inherit' }}>
            <div style={{ fontSize: 22 }}>📚</div>
            <div style={{ fontWeight: 700, marginTop: 6 }}>{name}</div>
            <div style={{ fontSize: 13, color: 'var(--sm-text-muted)' }}>{grouped[name] ?? 0} books</div>
          </Link>
        ))}
        {shelves.filter(s => !['Currently Reading','To Read','Finished'].includes(s.name)).map(s => (
          <Link key={s.id} to="/shelves" className="sm-card" style={{ textDecoration: 'none', color: 'inherit' }}>
            <div style={{ fontSize: 22 }}>{s.iconEmoji || '📁'}</div>
            <div style={{ fontWeight: 700, marginTop: 6 }}>{s.name}</div>
            <div style={{ fontSize: 13, color: 'var(--sm-text-muted)' }}>{books.filter(b => b.category === s.name).length} books</div>
          </Link>
        ))}
      </div>

      <div className="sm-section-title">Recently saved</div>
      {books.length === 0 && (
        <div className="sm-empty">
          <h3>No books yet</h3>
          <p>Search to discover and save your first book.</p>
          <Link to="/discover" className="sm-btn sm-btn-primary">Discover books</Link>
        </div>
      )}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(120px, 1fr))', gap: 12 }}>
        {books.slice(0, 8).map(b => (
          <Link key={b.id} to={`/book/${encodeURIComponent(b.googleBooksId || b.id)}`} style={{ textDecoration: 'none', color: 'inherit' }}>
            <div className="sm-card" style={{ padding: 8 }}>
              {b.coverUrl ? <img src={b.coverUrl} alt={b.title} style={{ width: '100%', aspectRatio: '2/3', objectFit: 'cover', borderRadius: 8 }} /> : <div style={{ width:'100%', aspectRatio:'2/3', background:'var(--sm-surface-alt)', borderRadius:8 }} />}
              <div style={{ fontSize: 12, fontWeight: 700, marginTop: 6, lineHeight: 1.2 }}>{b.title}</div>
            </div>
          </Link>
        ))}
      </div>

      <div className="sm-section-title">On the web</div>
      <div className="sm-card">
        <p style={{ margin: 0, fontSize: 14, color: 'var(--sm-text-muted)' }}>
          Club content, ARC campaigns, rewards, and AI companions are device-local on Android and are not yet available on the web.
          Shared data (profile, shelves, saved books, reading progress, club memberships) syncs with your Android app.
        </p>
        <div style={{ marginTop: 10, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
          <Link to="/clubs" className="sm-btn sm-btn-ghost">My club memberships</Link>
          <Link to="/shelves" className="sm-btn sm-btn-ghost">Manage shelves</Link>
        </div>
      </div>
    </div>
  )
}
