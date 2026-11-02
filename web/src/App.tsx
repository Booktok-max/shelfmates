import { lazy, Suspense, useEffect } from 'react'
import type { ReactElement } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './auth/AuthContext'
import Layout from './components/Layout'
import { doc, setDoc } from 'firebase/firestore'
import { db } from './firebase'
import { findSharedCatalogueBook, getSharedCatalogueBook } from './api/catalogue'
import { buildCatalogueShelfBookDoc } from './data/serialize'

const SignIn = lazy(() => import('./pages/SignIn'))
const Home = lazy(() => import('./pages/Home'))
const Discover = lazy(() => import('./pages/Discover'))
const Shelves = lazy(() => import('./pages/Shelves'))
const BookDetail = lazy(() => import('./pages/BookDetail'))
const Reader = lazy(() => import('./pages/Reader'))
const Profile = lazy(() => import('./pages/Profile'))
const Clubs = lazy(() => import('./pages/Clubs'))
const NotFound = lazy(() => import('./pages/NotFound'))

function PageFallback() {
  return (
    <div className="sm-container sm-empty" role="status" aria-live="polite">
      Loading…
    </div>
  )
}

/**
 * Gate for every app route.
 *
 * Two conditions, in the same order the Android app applies them
 * (ui/AuthGate.kt -> resolveAuthDestination):
 *   1. no signed-in user  -> /signin
 *   2. signed in but the profile has no role yet -> /signin, which renders the
 *      one-time role picker. The Firestore rules allow a user to assign a
 *      self-service role exactly once, so this must be settled before use.
 */
function RequireAuth({ children }: { children: ReactElement }) {
  const { user, role, loading } = useAuth()
  if (loading) return <div className="sm-container sm-empty">Loading…</div>
  if (!user) return <Navigate to="/signin" replace />
  if (!role) return <Navigate to="/signin" replace />
  return children
}

export default function App() {
  const { user, loading, role } = useAuth()

  // Capture the book identity before RequireAuth redirects to /signin.
  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const queryId = params.get('saveBook')
    const pathMatch = window.location.pathname.match(/^\\/book\\/catalogue:([0-9a-f-]{36})$/i)
    const pending = queryId && /^[0-9a-f-]{36}$/i.test(queryId)
      ? { id: queryId }
      : pathMatch?.[1] ? { id: pathMatch[1] }
      : params.get('saveBookTitle') && params.get('saveBookAuthor')
        ? { title: params.get('saveBookTitle'), author: params.get('saveBookAuthor'), isbn: params.get('saveBookIsbn') || undefined }
        : null
    if (pending) {
      localStorage.setItem('atomicShelf.pendingCatalogueSave.v1', JSON.stringify(pending))
      window.history.replaceState({}, '', '/')
    }
  }, [])

  useEffect(() => {
    if (loading || !user || role !== 'READER') return
    const raw = localStorage.getItem('atomicShelf.pendingCatalogueSave.v1')
    if (!raw) return
    let pending: { id?: string; title?: string; author?: string; isbn?: string }
    try { pending = JSON.parse(raw) } catch { pending = { id: raw } }
    let cancelled = false
    ;(async () => {
      try {
        const book = pending.id
          ? await getSharedCatalogueBook(pending.id)
          : pending.title && pending.author
            ? await findSharedCatalogueBook({ title: pending.title, author: pending.author, isbn: pending.isbn })
            : null
        if (!book || cancelled) return
        const payload = buildCatalogueShelfBookDoc(user.uid, book, 'To Read')
        await setDoc(doc(db, 'users', user.uid, 'bookshelf', payload.id), payload, { merge: true })
        if (!cancelled) localStorage.removeItem('atomicShelf.pendingCatalogueSave.v1')
      } catch (error) {
        // Keep the pending handoff for retry; never save an approximate match.
        console.warn('Shelfmates catalogue save remains pending:', error)
      }
    })()
    return () => { cancelled = true }
  }, [loading, user?.uid, role])
  if (loading) return <div className="sm-container sm-empty">Loading Shelfmates…</div>
  return (
    <Suspense fallback={<PageFallback />}>
      <Routes>
        <Route path="/signin" element={user && role ? <Navigate to="/" replace /> : <SignIn />} />
        <Route element={<Layout />}>
          <Route path="/" element={<RequireAuth><Home /></RequireAuth>} />
          <Route path="/discover" element={<RequireAuth><Discover /></RequireAuth>} />
          <Route path="/shelves" element={<RequireAuth><Shelves /></RequireAuth>} />
          <Route path="/book/:id" element={<RequireAuth><BookDetail /></RequireAuth>} />
          <Route path="/reader" element={<RequireAuth><Reader /></RequireAuth>} />
          <Route path="/profile" element={<RequireAuth><Profile /></RequireAuth>} />
          <Route path="/clubs" element={<RequireAuth><Clubs /></RequireAuth>} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </Suspense>
  )
}
