import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'
import { getVolume, coverUrl, authorsStr } from '../api/googleBooks'
import {
  catalogueIsConfigured,
  getCatalogueBook,
  availabilityLabel,
  readingDestination,
  attributionFor,
  googleBooksIdFor,
  type CatalogueBook,
} from '../api/catalogue'
import { useCustomShelves } from '../data/shelves'
import { doc, setDoc } from 'firebase/firestore'
import { db } from '../firebase'
import { buildShelfBookDoc, buildCatalogueShelfBookDoc } from '../data/serialize'

const UUID_RE = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i

/**
 * Strip markup from externally supplied text.
 *
 * The Google Books description is HTML, and it was rendered with
 * `dangerouslySetInnerHTML`. That renders third-party markup in the page,
 * which is a scripting risk for content this app does not control. The
 * description is read, not formatted, so it is rendered as text.
 */
function toPlainText(value: string | undefined | null): string {
  if (!value) return ''
  return value
    .replace(/<script[\s\S]*?<\/script>/gi, ' ')
    .replace(/<style[\s\S]*?<\/style>/gi, ' ')
    .replace(/<br\s*\/?>/gi, '\n')
    .replace(/<\/(p|div|li)>/gi, '\n')
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/gi, ' ')
    .replace(/&amp;/gi, '&')
    .replace(/&quot;/gi, '"')
    .replace(/&#39;/gi, "'")
    .replace(/&lt;/gi, '<')
    .replace(/&gt;/gi, '>')
    .replace(/[ \t]+/g, ' ')
    .replace(/\n{3,}/g, '\n\n')
    .trim()
}

export default function BookDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const { shelves } = useCustomShelves(user?.uid ?? null)
  const [volume, setVolume] = useState<any>(null)
  const [catalogueBook, setCatalogueBook] = useState<CatalogueBook | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [category, setCategory] = useState('To Read')
  const [saving, setSaving] = useState(false)
  const [saved, setSaved] = useState(false)

  const isCatalogueId = Boolean(id && UUID_RE.test(id))

  useEffect(() => {
    if (!id) return
    setLoading(true)
    setError(null)
    setSaved(false)
    if (isCatalogueId && catalogueIsConfigured()) {
      getCatalogueBook(id)
        .then((book) => {
          if (book) {
            setCatalogueBook(book)
            setLoading(false)
            return
          }
          // A catalogue id that the catalogue does not know: fall through to
          // the Google Books lookup rather than showing a dead end.
          return getVolume(id).then((v) => { setVolume(v); setLoading(false) })
            .catch((e) => { setError(e?.message); setLoading(false) })
        })
        .catch((e) => { setError(e?.message); setLoading(false) })
      return
    }
    getVolume(id)
      .then((v) => { setVolume(v); setLoading(false) })
      .catch((e) => { setError(e?.message); setLoading(false) })
  }, [id, isCatalogueId])

  async function save() {
    if (!user) return
    setSaving(true)
    try {
      if (catalogueBook) {
        const payload = buildCatalogueShelfBookDoc(user.uid, catalogueBook, category)
        await setDoc(doc(db, 'users', user.uid, 'bookshelf', payload.id), payload, { merge: true })
      } else if (volume) {
        const docId = `saved_${user.uid}_${volume.id}`
        await setDoc(doc(db, 'users', user.uid, 'bookshelf', docId), buildShelfBookDoc(user.uid, volume, category), { merge: true })
      }
      setSaved(true)
    } catch (e: any) { alert(e?.message ?? 'Save failed') }
    finally { setSaving(false) }
  }

  if (loading) return <div className="sm-container sm-empty">Loading…</div>
  if (error || (!volume && !catalogueBook)) {
    return (
      <div className="sm-container sm-empty">
        <h3>Book not found</h3>
        <p>{error}</p>
        <Link to="/discover" className="sm-btn sm-btn-ghost">Back</Link>
      </div>
    )
  }

  const cats = ['Currently Reading', 'To Read', 'Finished', ...shelves.map(s => s.name)]

  // ---- Catalogue record -------------------------------------------------
  if (catalogueBook) {
    const b = catalogueBook
    const destination = readingDestination(b)
    const attribution = attributionFor(b)
    const googleId = googleBooksIdFor(b)
    const isbn = b.editions.find(e => e.isbn13)?.isbn13 ?? b.editions.find(e => e.isbn10)?.isbn10
    return (
      <div className="sm-container">
        <Link to="/discover" style={{ color: 'var(--sm-text-muted)', fontSize: 14 }}>← Discover</Link>
        <div style={{ display: 'flex', gap: 20, marginTop: 16, flexWrap: 'wrap' }}>
          <div style={{ flex: '0 0 180px' }}>
            {b.coverUrl
              ? <img src={b.coverUrl} alt={b.title} style={{ width: '100%', borderRadius: 12, boxShadow: 'var(--sm-shadow-lg)' }} />
              : <div style={{ width: '100%', aspectRatio: '2/3', background: 'var(--sm-surface-alt)', borderRadius: 12 }} />}
          </div>
          <div style={{ flex: '1 1 280px' }}>
            <h1 style={{ margin: 0, fontSize: 24 }}>{b.title}</h1>
            {b.subtitle && <p style={{ color: 'var(--sm-text-muted)', margin: '2px 0 0', fontSize: 15 }}>{b.subtitle}</p>}
            <p style={{ color: 'var(--sm-text-muted)', margin: '4px 0 12px' }}>{b.authors.join(', ')}</p>
            <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 12 }}>
              {b.genres.slice(0, 6).map((g) => <span key={g} className="sm-chip">{g}</span>)}
              {b.language && <span className="sm-chip">{b.language}</span>}
              {b.publicationYear && <span className="sm-chip">{b.publicationYear}</span>}
              {b.pageCount ? <span className="sm-chip">{b.pageCount} pages</span> : null}
              {isbn && <span className="sm-chip">ISBN {isbn}</span>}
            </div>

            <p style={{ fontSize: 13, margin: '0 0 10px' }}>
              <strong>{availabilityLabel(b)}</strong>
              {b.sources.length > 0 && <> · from {b.sources.map(s => s.provider).join(', ')}</>}
            </p>

            {b.description && (
              <p style={{ fontSize: 14, lineHeight: 1.6, color: 'var(--sm-text)', whiteSpace: 'pre-wrap' }}>
                {toPlainText(b.description)}
              </p>
            )}

            <div style={{ marginTop: 16, display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center' }}>
              <select className="sm-input" style={{ width: 'auto', minWidth: 180 }} value={category} onChange={e => setCategory(e.target.value)}>
                {cats.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
              <button className="sm-btn sm-btn-primary" onClick={save} disabled={saving || saved}>
                {saved ? 'Saved ✓' : saving ? 'Saving…' : 'Save to shelf'}
              </button>
              {destination && (
                <a className="sm-btn sm-btn-ghost" href={destination.href} target="_blank" rel="noopener noreferrer">
                  {destination.label}
                </a>
              )}
            </div>

            <p style={{ fontSize: 12, color: 'var(--sm-text-subtle)', marginTop: 12 }}>
              Saved books sync to your Android Shelfmates app via Firestore.
            </p>

            {attribution && (
              <p style={{ fontSize: 11, color: 'var(--sm-text-subtle)', marginTop: 8 }}>{attribution}</p>
            )}
            {b.rights.publicDomain === true && (
              <p style={{ fontSize: 11, color: 'var(--sm-text-subtle)', marginTop: 4 }}>
                Public domain in {b.rights.jurisdiction ?? 'the United States'}. Copyright status may differ elsewhere.
              </p>
            )}
            {googleId && (
              <p style={{ fontSize: 11, color: 'var(--sm-text-subtle)', marginTop: 4 }}>
                Also available on Google Books as {googleId}.
              </p>
            )}
          </div>
        </div>
      </div>
    )
  }

  // ---- Google Books record ----------------------------------------------
  const v = volume.volumeInfo
  return (
    <div className="sm-container">
      <Link to="/discover" style={{ color: 'var(--sm-text-muted)', fontSize: 14 }}>← Discover</Link>
      <div style={{ display: 'flex', gap: 20, marginTop: 16, flexWrap: 'wrap' }}>
        <div style={{ flex: '0 0 180px' }}>
          {coverUrl(volume) ? <img src={coverUrl(volume)} alt={v.title} style={{ width: '100%', borderRadius: 12, boxShadow: 'var(--sm-shadow-lg)' }} /> : <div style={{ width: '100%', aspectRatio: '2/3', background: 'var(--sm-surface-alt)', borderRadius: 12 }} />}
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
          {v.description && (
            <p style={{ fontSize: 14, lineHeight: 1.6, color: 'var(--sm-text)', whiteSpace: 'pre-wrap' }}>
              {toPlainText(v.description)}
            </p>
          )}
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
