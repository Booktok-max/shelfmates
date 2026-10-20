import { useEffect, useState } from 'react'
import { collection, onSnapshot, query } from 'firebase/firestore'
import { db } from '../firebase'

export interface ShelfBook {
  id: string
  userId: string
  googleBooksId: string
  title: string
  authors: string
  coverUrl: string
  category: string
  description: string
  genre: string
  pageCount: number
  rating: number
  isbn13: string
  notes: string
  personalRating: number
  savedAt: number
  lastUpdated: number
}

export interface CustomShelf {
  id: string
  userId: string
  name: string
  iconEmoji: string
  isDefault: boolean
  createdAt: number
  lastUpdated: number
}

export function useCustomShelves(uid: string | null) {
  const [shelves, setShelves] = useState<CustomShelf[]>([])
  const [loading, setLoading] = useState(true)
  useEffect(() => {
    if (!uid) { setShelves([]); setLoading(false); return }
    const q = query(collection(db, 'users', uid, 'custom_shelves'))
    const unsub = onSnapshot(q, (snap) => {
      setShelves(snap.docs.map(d => ({ id: d.id, ...(d.data() as any) })))
      setLoading(false)
    }, () => setLoading(false))
    return () => unsub()
  }, [uid])
  return { shelves, loading }
}

export function useShelfBooks(uid: string | null) {
  const [books, setBooks] = useState<ShelfBook[]>([])
  const [loading, setLoading] = useState(true)
  useEffect(() => {
    if (!uid) { setBooks([]); setLoading(false); return }
    const q = query(collection(db, 'users', uid, 'bookshelf'))
    const unsub = onSnapshot(q, (snap) => {
      setBooks(snap.docs.map(d => ({ id: d.id, ...(d.data() as any) })))
      setLoading(false)
    }, () => setLoading(false))
    return () => unsub()
  }, [uid])
  return { books, loading }
}

export function useReadingProgress(uid: string | null) {
  const [progress, setProgress] = useState<any[]>([])
  useEffect(() => {
    if (!uid) { setProgress([]); return }
    const q = query(collection(db, 'users', uid, 'reading_progress'))
    const unsub = onSnapshot(q, (snap) => {
      setProgress(snap.docs.map(d => ({ id: d.id, ...(d.data() as any) })))
    })
    return () => unsub()
  }, [uid])
  return progress
}

export function useClubMemberships(uid: string | null) {
  const [memberships, setMemberships] = useState<string[]>([])
  useEffect(() => {
    if (!uid) { setMemberships([]); return }
    const q = query(collection(db, 'users', uid, 'club_memberships'))
    const unsub = onSnapshot(q, (snap) => {
      setMemberships(snap.docs.map(d => d.id))
    })
    return () => unsub()
  }, [uid])
  return memberships
}
