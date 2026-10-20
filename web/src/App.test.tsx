import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import App from './App'

const authState = {
  user: null as any,
  role: null as any,
  loading: false,
  signInGoogle: vi.fn(),
  signInGuest: vi.fn(),
  signOut: vi.fn(),
  saveRole: vi.fn(),
}

vi.mock('./auth/AuthContext', () => ({
  useAuth: () => authState,
  AuthProvider: ({ children }: any) => children,
}))

vi.mock('./firebase', () => ({
  auth: {},
  db: {},
  googleProvider: {},
  googleBooksKey: '',
}))

vi.mock('./data/shelves', () => ({
  useCustomShelves: () => ({ shelves: [], loading: false }),
  useShelfBooks: () => ({ books: [], loading: false }),
  useReadingProgress: () => [],
  useClubMemberships: () => [],
}))

describe('App route guards', () => {
  beforeEach(() => {
    authState.user = null
    authState.role = null
    authState.loading = false
  })

  it('redirects an unauthenticated visitor from a protected route to /signin', async () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>,
    )
    // The sign-in page is lazily imported, so allow for the chunk to resolve.
    await waitFor(() => expect(screen.getByText('Atomic Shelfmates')).toBeInTheDocument(), { timeout: 10000 })
  })

  it('renders a not-found page for unknown routes', async () => {
    render(
      <MemoryRouter initialEntries={['/does-not-exist']}>
        <App />
      </MemoryRouter>,
    )
    await waitFor(() => expect(screen.getByText(/page not found/i)).toBeInTheDocument(), { timeout: 10000 })
  })

  it('sends a signed-in user with no role to the role picker', async () => {
    authState.user = { uid: 'u1', displayName: 'Ada' }
    authState.role = null
    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>,
    )
    await waitFor(() => expect(screen.getByText(/choose your role/i)).toBeInTheDocument(), { timeout: 10000 })
  })

  it('lets a signed-in user with a role reach the home page', async () => {
    authState.user = { uid: 'u1', displayName: 'Ada' }
    authState.role = 'READER'
    render(
      <MemoryRouter initialEntries={['/']}>
        <App />
      </MemoryRouter>,
    )
    await waitFor(() => expect(screen.getByText(/welcome back, ada/i)).toBeInTheDocument(), { timeout: 10000 })
  })
})
