import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { getVolume, coverUrl, authorsStr } from '../api/googleBooks'
import { useCustomShelves } from '../data/shelves'
import { doc, getDoc, setDoc } from 'firebase/firestore'
import { db } from '../firebase'
import { buildShelfBookDoc } from '../data/serialize'
import type { ShelfBook } from '../data/shelves'

export default function BookDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const { shelves } = useCustomShelves(user?.uid ?? null)
  const [volume, setVolume] = useState<any>(null)
  const [savedBook, setSavedBook] = useState<ShelfBook | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [category, setCategory] = useState('To Read')
  const [saving, setSaving] = useState(false)
  const [saved, setSaved] = useState(false)

  useEffect(() => {
    if (!id || !user) return
    let active = true
    setLoading(true)
    setError(null)
    setVolume(null)
    setSavedBook(null)

    async function loadBook() {
      try {
        // Shelf links use the Firestore document id. Recommendations are not
        // Google Books volumes, so resolve them from the signed-in reader's
        // own shelf before considering the Google Books API.
        if (id!.startsWith('saved_')) {
          const snapshot = await getDoc(doc(db, 'users', user!.uid, 'bookshelf', id!))
          if (!snapshot.exists()) throw new Error('This saved book could not be found on your shelf.')
          const book = { id: snapshot.id, ...(snapshot.data() as ShelfBook) }
          if (book.savedAsRecommendation || !book.googleBooksId) {
            if (active) setSavedBook(book)
            return
          }
          const result = await getVolume(book.googleBooksId)
          if (active) setVolume(result)
          return
        }

        const result = await getVolume(id!)
        if (active) setVolume(result)
      } catch (e: any) {
        if (active) setError(e?.message ?? 'The book could not be loaded.')
      } finally {
        if (active) setLoading(false)
      }
    }

    void loadBook()
    return () => { active = false }
  }, [id, user?.uid])

  async function save() {
    if (!user || !volume) return
    setSaving(true)
    try {
      const docId = `saved_${user.uid}_${volume.id}`
      await setDoc(doc(db, 'users', user.uid, 'bookshelf', docId), buildShelfBookDoc(user.uid, volume, category), { merge: true })
      setSaved(true)
    } catch (e: any) { alert(e?.message ?? 'Save failed') }
    finally { setSaving(false) }
  }

  if (loading) return <div className="sm-container sm-empty">Loading…</div>
  if (error || (!volume && !savedBook)) return <div className="sm-container sm-empty"><h3>Book not found</h3><p>{error ?? 'This book could not be loaded.'}</p><Link to="/shelves" className="sm-btn sm-btn-ghost">Back to shelves</Link></div>

  if (savedBook) {
    const cover = savedBook.coverUrl
    return (
      <div className="sm-container">
        <Link to="/shelves" style={{ color: 'var(--sm-text-muted)', fontSize: 14 }}>← Your shelves</Link>
        <div style={{ display: 'flex', gap: 20, marginTop: 16, flexWrap: 'wrap' }}>
          <div style={{ flex: '0 0 180px' }}>
            {cover
              ? <img src={cover} alt={savedBook.title} style={{ width: '100%', borderRadius: 12, boxShadow: 'var(--sm-shadow-lg)' }} />
              : <div role="img" aria-label="Cover not available" style={{ width: '100%', aspectRatio: '2/3', background: 'var(--sm-surface-alt)', borderRadius: 12, display: 'grid', placeItems: 'center', color: 'var(--sm-text-muted)', padding: 12, boxSizing: 'border-box', textAlign: 'center' }}>Cover not available</div>}
          </div>
          <div style={{ flex: '1 1 280px' }}>
            <span className="sm-chip">Milky catalogue recommendation</span>
            <h1 style={{ margin: '10px 0 0', fontSize: 24 }}>{savedBook.title}</h1>
            <p style={{ color: 'var(--sm-text-muted)', margin: '4px 0 12px' }}>{savedBook.authors}</p>
            {savedBook.genre && <span className="sm-chip">{savedBook.genre}</span>}
            {(savedBook.isbn13 || savedBook.sourceIsbn) && (
              <p style={{ color: 'var(--sm-text-muted)', fontSize: 13 }}>ISBN: {savedBook.isbn13 || savedBook.sourceIsbn}</p>
            )}
            {savedBook.description && <p style={{ fontSize: 14, lineHeight: 1.6 }}>{savedBook.description}</p>}
            <p style={{ fontSize: 13, lineHeight: 1.6, color: 'var(--sm-text-muted)', marginTop: 16 }}>
              This is a saved book recommendation. A digital reading edition is not attached to this record.
            </p>
          </div>
        </div>
      </div>
    )
  }

  const v = volume.volumeInfo
  const cats = ['Currently Reading', 'To Read', 'Finished', ...shelves.map(s => s.name)]

  return (
    <div className="sm-container">
      <Link to="/discover" style={{ color: 'var(--sm-text-muted)', fontSize: 14 }}>← Discover</Link>
      <div style={{ display: 'flex', gap: 20, marginTop: 16, flexWrap: 'wrap' }}>
        <div style={{ flex: '0 0 180px' }}>
          {coverUrl(volume) ? <img src={coverUrl(volume)} alt={v.title} style={{ width: '100%', borderRadius: 12, boxShadow: 'var(--sm-shadow-lg)' }} /> : <div style={{ width:'100%', aspectRatio:'2/3', background:'var(--sm-surface-alt)', borderRadius:12 }} />}
        </div>
        <div style={{ flex: '1 1 280px' }}>
          <h1 style={{ margin: 0, fontSize: 24 }}>{v.title}</h1>
          <p style={{ color: 'var(--sm-text-muted)', margin: '4px 0 12px' }}>{authorsStr(volume)}</p>
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 12 }}>
            {v.categories?.map((c: string) => <span key={c} className="sm-chip">{c}</span>)}
            {v.pageCount ? <span className="sm-chip">{v.pageCount} pages</span> : null}
            {v.averageRating ? <span className="sm-chip">★ {v.averageRating}</span> : null}
            {v.publishedDate ? <span className="sm-chip">{v.publishedDate}</span> : null}
          </div>
          {v.description && <p style={{ fontSize: 14, lineHeight: 1.6, color: 'var(--sm-text)' }} dangerouslySetInnerHTML={{ __html: v.description }} />}
          <div style={{ marginTop: 16, display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center' }}>
            <select className="sm-input" style={{ width: 'auto', minWidth: 180 }} value={category} onChange={e => setCategory(e.target.value)}>
              {cats.map(c => <option key={c} value={c}>{c}</option>)}
            </select>
            <button className="sm-btn sm-btn-primary" onClick={save} disabled={saving || saved}>
              {saved ? 'Saved ✓' : saving ? 'Saving…' : 'Save to shelf'}
            </button>
          </div>
          <p style={{ fontSize: 12, color: 'var(--sm-text-subtle)', marginTop: 12 }}>
            Saved books sync to your Android Shelfmates app via Firestore.
          </p>
        </div>
      </div>
    </div>
  )
}
