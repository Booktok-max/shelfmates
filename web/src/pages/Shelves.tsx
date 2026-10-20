import { useState } from 'react'
import { useAuth } from '../auth/AuthContext'
import { useCustomShelves, useShelfBooks } from '../data/shelves'
import type { CustomShelf } from '../data/shelves'
import { doc, deleteDoc, setDoc, updateDoc } from 'firebase/firestore'
import { db } from '../firebase'
import { buildCustomShelfDoc } from '../data/serialize'

export default function Shelves() {
  const { user } = useAuth()
  const { shelves, loading: sl } = useCustomShelves(user?.uid ?? null)
  const { books, loading: bl } = useShelfBooks(user?.uid ?? null)
  const [newShelf, setNewShelf] = useState('')
  const [creating, setCreating] = useState(false)

  async function createShelf() {
    if (!user || !newShelf.trim()) return
    setCreating(true)
    try {
      const id = `shelf_${user.uid}_${newShelf.trim().toLowerCase().replace(/\s+/g, '_')}`
      await setDoc(doc(db, 'users', user.uid, 'custom_shelves', id), buildCustomShelfDoc(user.uid, newShelf.trim()), { merge: true })
      setNewShelf('')
    } catch (e: any) { alert(e?.message) }
    finally { setCreating(false) }
  }

  async function removeBook(bookId: string) {
    if (!user) return
    if (!confirm('Remove this book from your shelves?')) return
    await deleteDoc(doc(db, 'users', user.uid, 'bookshelf', bookId))
  }

  async function moveBook(bookId: string, category: string) {
    if (!user) return
    await updateDoc(doc(db, 'users', user.uid, 'bookshelf', bookId), { category, lastUpdated: Date.now() })
  }

  async function deleteShelf(s: CustomShelf) {
    if (!user) return
    if (!confirm(`Delete shelf "${s.name}"? Books are not deleted.`)) return
    await deleteDoc(doc(db, 'users', user.uid, 'custom_shelves', s.id))
  }

  if (sl || bl) return <div className="sm-container sm-empty">Loading…</div>

  const groups = new Map<string, typeof books>()
  for (const b of books) {
    if (!groups.has(b.category)) groups.set(b.category, [])
    groups.get(b.category)!.push(b)
  }

  return (
    <div className="sm-container">
      <h1>Your Shelves</h1>
      <div className="sm-card" style={{ display: 'flex', gap: 8, margin: '12px 0' }}>
        <input className="sm-input" placeholder="New shelf name…" value={newShelf} onChange={e => setNewShelf(e.target.value)} />
        <button className="sm-btn sm-btn-primary" onClick={createShelf} disabled={creating || !newShelf.trim()}>{creating ? '…' : 'Create'}</button>
      </div>

      {shelves.length > 0 && (
        <>
          <div className="sm-section-title">Custom shelves</div>
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginBottom: 12 }}>
            {shelves.map(s => (
              <span key={s.id} className="sm-chip" title="Click × to delete">
                {s.iconEmoji} {s.name}
                <button style={{ background: 'none', border: 'none', cursor: 'pointer', marginLeft: 4, color: 'inherit' }} onClick={() => deleteShelf(s)}>×</button>
              </span>
            ))}
          </div>
        </>
      )}

      {books.length === 0 && (
        <div className="sm-empty"><h3>No saved books</h3><p>Discover books to fill your shelves.</p></div>
      )}

      {[...groups.entries()].map(([cat, items]) => (
        <section key={cat} style={{ marginTop: 16 }}>
          <div className="sm-section-title">{cat} ({items.length})</div>
          <div style={{ display: 'grid', gap: 10 }}>
            {items.map(b => (
              <div key={b.id} className="sm-card" style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
                {b.coverUrl ? <img src={b.coverUrl} alt={b.title} style={{ width: 48, height: 72, objectFit: 'cover', borderRadius: 6 }} /> : <div style={{ width:48, height:72, background:'var(--sm-surface-alt)', borderRadius:6 }} />}
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontWeight: 700, fontSize: 14 }}>{b.title}</div>
                  <div style={{ fontSize: 12, color: 'var(--sm-text-muted)' }}>{b.authors}</div>
                </div>
                <select className="sm-input" style={{ width: 'auto', minWidth: 140 }} value={b.category} onChange={e => moveBook(b.id, e.target.value)}>
                  {['Currently Reading','To Read','Finished', ...shelves.map(s => s.name)].map(c => <option key={c} value={c}>{c}</option>)}
                </select>
                <button className="sm-btn sm-btn-ghost" style={{ padding: '6px 10px' }} onClick={() => removeBook(b.id)}>Remove</button>
              </div>
            ))}
          </div>
        </section>
      ))}
    </div>
  )
}
