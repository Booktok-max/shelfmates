import { lazy, Suspense, useCallback, useEffect, useLayoutEffect, useRef, useState } from 'react'
import type { ReactElement } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './auth/AuthContext'
import { findVolumeByIsbn } from './api/googleBooks'
import Layout from './components/Layout'
import { doc, setDoc } from 'firebase/firestore'
import { db } from './firebase'
import { CatalogueLookupError, findSharedCatalogueBook, getSharedCatalogueBook, type SharedCatalogueBook } from './api/catalogue'
import { buildCatalogueShelfBookDoc, buildMilkyRecommendationShelfBookDoc } from './data/serialize'
import { parsePendingCatalogueSave, pendingCatalogueSaveKey, type PendingCatalogueSave } from './data/catalogueHandoff'

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
  const [catalogueSaveNotice, setCatalogueSaveNotice] = useState<{ kind: 'info' | 'success' | 'error'; message: string } | null>(null)
  const [ambiguousBooks, setAmbiguousBooks] = useState<SharedCatalogueBook[]>([])
  const [ambiguousPending, setAmbiguousPending] = useState<PendingCatalogueSave | null>(null)
  const processingKey = useRef<string | null>(null)

  const savePending = useCallback(async (pending: PendingCatalogueSave, selectedBook?: SharedCatalogueBook) => {
    const uid = user?.uid
    if (!uid) return
    const key = pendingCatalogueSaveKey(pending)
    if (processingKey.current === key) return
    processingKey.current = key

    try {
      setCatalogueSaveNotice({ kind: 'info', message: 'Checking the catalogue and saving your book…' })
      let payload: ReturnType<typeof buildCatalogueShelfBookDoc> | ReturnType<typeof buildMilkyRecommendationShelfBookDoc>
      let savedTitle = pending.title ?? ''

      if (selectedBook) {
        payload = buildCatalogueShelfBookDoc(uid, selectedBook, 'To Read')
        savedTitle = selectedBook.title
      } else if (pending.id) {
        const book = await getSharedCatalogueBook(pending.id)
        payload = buildCatalogueShelfBookDoc(uid, book, 'To Read')
        savedTitle = book.title
      } else if (pending.title && pending.author) {
        try {
          const sharedBook = await findSharedCatalogueBook({
            title: pending.title,
            author: pending.author,
            isbn: pending.isbn,
          })
          payload = buildCatalogueShelfBookDoc(uid, sharedBook, 'To Read')
        } catch (error) {
          if (error instanceof CatalogueLookupError && error.kind === 'ambiguous') {
            setAmbiguousBooks(error.matches)
            setAmbiguousPending(pending)
            setCatalogueSaveNotice({
              kind: 'info',
              message: 'Which edition did you mean? Choose the matching catalogue record before saving.',
            })
            return
          }
          if (error instanceof CatalogueLookupError && error.kind === 'not-found') {
            // Milky can contain a valid book that StoryPal's shared catalogue
            // does not yet know. Preserve it as a recommendation, not a fake
            // Google Books edition. Network/5xx failures never take this path.
            // The catalogue miss is still a valid recommendation. Enrich it
            // with a verified ISBN-matched Google Books description when one
            // exists, but keep it metadata-only (no Google Books reader ID).
            let metadata = null
            if (pending.isbn) {
              try {
                metadata = await findVolumeByIsbn(pending.isbn, {
                  title: pending.title,
                  author: pending.author,
                })
              } catch (lookupError) {
                console.info('Optional ISBN description lookup was unavailable:', lookupError)
              }
            }
            payload = buildMilkyRecommendationShelfBookDoc(uid, {
              title: pending.title,
              author: pending.author,
              isbn: pending.isbn,
              coverUrl: pending.coverUrl || (metadata ? coverUrl(metadata) : undefined),
              description: metadata?.volumeInfo?.description,
              genres: metadata?.volumeInfo?.categories,
            }, 'To Read')
          } else {
            throw error
          }
        }
      } else {
        throw new Error('The book details were incomplete, so it was not saved.')
      }

      await setDoc(doc(db, 'users', uid, 'bookshelf', payload.id), payload, { merge: true })
      localStorage.removeItem('atomicShelf.pendingCatalogueSave.v1')
      setAmbiguousBooks([])
      setAmbiguousPending(null)
      setCatalogueSaveNotice({
        kind: 'success',
        message: 'Saved “' + savedTitle + '” to your To Read shelf' + (payload.catalogueProvider === 'milky' ? ' as a recommendation from The Big Shelf' : '') + '.',
      })
    } catch (error) {
      console.warn('Shelfmates catalogue save remains pending:', error)
      const reason = error instanceof Error ? error.message : 'A temporary error prevented the save.'
      setCatalogueSaveNotice({
        kind: 'error',
        message: reason + ' Your book has not been saved; the request is kept for retry.',
      })
    } finally {
      if (processingKey.current === key) processingKey.current = null
    }
  }, [user?.uid])

  // Capture book identity in the layout phase, before child <Navigate> passive effects
  // can replace the incoming URL while RequireAuth redirects an unsigned-in reader.
  useLayoutEffect(() => {
    const params = new URLSearchParams(window.location.search)
    const queryId = params.get('saveBook')
    const pathMatch = window.location.pathname.match(/^\/book\/catalogue:([0-9a-f-]{36})$/i)
    const incoming = queryId
      ? { id: queryId }
      : pathMatch?.[1] ? { id: pathMatch[1] }
      : params.get('saveBookTitle') || params.get('saveBookAuthor')
        ? {
            title: params.get('saveBookTitle'),
            author: params.get('saveBookAuthor'),
            isbn: params.get('saveBookIsbn'),
            coverUrl: params.get('saveBookCover'),
          }
        : null

    if (!incoming) return
    const parsed = parsePendingCatalogueSave(incoming)
    if (parsed.ok) {
      localStorage.setItem('atomicShelf.pendingCatalogueSave.v1', JSON.stringify(parsed.value))
      setCatalogueSaveNotice({
        kind: 'info',
        message: 'Book received. Sign in to your Shelfmates account to save it to your To Read shelf.',
      })
    } else {
      setCatalogueSaveNotice({ kind: 'error', message: parsed.reason })
    }
    window.history.replaceState({}, '', '/')
  }, [])

  useEffect(() => {
    if (loading || !user || !role) return
    const raw = localStorage.getItem('atomicShelf.pendingCatalogueSave.v1')
    if (!raw) return
    const parsed = parsePendingCatalogueSave(raw)
    if (!parsed.ok) {
      setCatalogueSaveNotice({ kind: 'error', message: `${parsed.reason} Dismiss this request to clear it.` })
      return
    }
    void savePending(parsed.value)
  }, [loading, user?.uid, role, savePending])

  const retryPendingSave = useCallback(() => {
    const raw = localStorage.getItem('atomicShelf.pendingCatalogueSave.v1')
    if (!raw) return
    const parsed = parsePendingCatalogueSave(raw)
    if (!parsed.ok) {
      setCatalogueSaveNotice({ kind: 'error', message: parsed.reason })
      return
    }
    void savePending(parsed.value)
  }, [savePending])

  const dismissPendingSave = useCallback(() => {
    localStorage.removeItem('atomicShelf.pendingCatalogueSave.v1')
    setAmbiguousBooks([])
    setAmbiguousPending(null)
    setCatalogueSaveNotice(null)
  }, [])

  if (loading) return <div className="sm-container sm-empty">Loading Shelfmates…</div>
  return (
    <Suspense fallback={<PageFallback />}>
      {catalogueSaveNotice && (
        <div
          role="status"
          aria-live="polite"
          className="sm-container"
          style={{ paddingTop: 12, paddingBottom: 12 }}
        >
          <div
            style={{
              border: '1px solid var(--sm-rule)',
              borderRadius: 12,
              padding: '12px 16px',
              color: catalogueSaveNotice.kind === 'error' ? 'var(--sm-coral)' : 'var(--sm-text)',
              background: 'var(--sm-surface)',
              fontSize: 14,
            }}
          >
            {catalogueSaveNotice.message}
            {catalogueSaveNotice.kind === 'error' && (
              <div style={{ display: 'flex', gap: 8, marginTop: 10 }}>
                {user && role && <button className="sm-btn sm-btn-primary" onClick={retryPendingSave}>Retry save</button>}
                <button className="sm-btn sm-btn-ghost" onClick={dismissPendingSave}>Dismiss</button>
              </div>
            )}
          </div>
        </div>
      )}
      {ambiguousBooks.length > 0 && ambiguousPending && (
        <div className="sm-container" role="group" aria-label="Choose catalogue edition">
          <div className="sm-card" style={{ marginBottom: 12, padding: 16 }}>
            <h2 style={{ margin: '0 0 6px', fontSize: 18 }}>Which edition did you mean?</h2>
            <p style={{ margin: '0 0 12px', color: 'var(--sm-text-muted)', fontSize: 13 }}>
              More than one exact match was found. Choose the correct record; nothing has been saved yet.
            </p>
            <div style={{ display: 'grid', gap: 8 }}>
              {ambiguousBooks.map(book => (
                <button
                  key={book.id}
                  className="sm-btn sm-btn-ghost"
                  style={{ textAlign: 'left', display: 'flex', flexDirection: 'column', alignItems: 'flex-start', gap: 3, padding: 12 }}
                  onClick={() => void savePending(ambiguousPending, book)}
                >
                  <strong>{book.title}</strong>
                  <span style={{ color: 'var(--sm-text-muted)', fontSize: 12 }}>{(book.authors ?? []).join(', ')}</span>
                  <span style={{ color: 'var(--sm-text-subtle)', fontSize: 11 }}>Choose this edition</span>
                </button>
              ))}
            </div>
            <button className="sm-btn sm-btn-ghost" style={{ marginTop: 10 }} onClick={dismissPendingSave}>Cancel save</button>
          </div>
        </div>
      )}
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
