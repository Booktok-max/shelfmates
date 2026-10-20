import { lazy, Suspense } from 'react'
import type { ReactElement } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './auth/AuthContext'
import Layout from './components/Layout'

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
