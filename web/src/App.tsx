import { lazy, Suspense, useEffect } from 'react'
import type { ReactElement } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './auth/AuthContext'
import Layout from './components/Layout'
import { doc, setDoc } from 'firebase/firestore'
import { db } from './firebase'
import { getSharedCatalogueBook } from './api/catalogue'
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

  // Capture the catalogue save before RequireAuth redirects to /signin. The
  // pending ID remains in localStorage until Firestore confirms the save.
  useEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const queryId = params.get('saveBook')
    const pathMatch = window.location.pathname.match(/^\\/book\\/catalogue:([0-9a-f-]{36})$/i)
    const pendingId = queryId || pathMatch?.[1]
    if (pendingId && /^[0-9a-f-]{36}$/i.test(pendingId)) {
      localStorage.setItem('atomicShelf.pendingCatalogueSave.v1', pendingId)
      window.history.replaceState({}, '', '/')
    }
  }, [])

  useEffect(() => {
    if (loading || !user || role !== 'READER') return
    const pendingId = localStorage.getItem('atomicShelf.pendingCatalogueSave.v1')
    if (!pendingId) return
    let cancelled = false
    ;(async () => {
      try {
        const book = await getSharedCatalogueBook(pendingId)
        if (cancelled) return
        const payload = buildCatalogueShelfBookDoc(user.uid, book, 'To Read')
        await setDoc(doc(db, 'users', user.uid, 'bookshelf', payload.id), payload, { merge: true })
        if (!cancelled) localStorage.removeItem('atomicShelf.pendingCatalogueSave.v1')
      } catch (error) {
        // Do not clear the handoff on a network or Firestore failure; a reload
        // after sign-in can safely retry the deterministic document write.
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
