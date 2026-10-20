import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import SignIn from './SignIn'

const mockAuth = {
  user: null as any,
  role: null as any,
  loading: false,
  signInGoogle: vi.fn(),
  signInGuest: vi.fn(),
  signOut: vi.fn(),
  saveRole: vi.fn(),
}

vi.mock('../auth/AuthContext', () => ({
  useAuth: () => mockAuth,
}))

function renderSignIn() {
  return render(
    <MemoryRouter>
      <SignIn />
    </MemoryRouter>,
  )
}

describe('SignIn', () => {
  beforeEach(() => {
    mockAuth.user = null
    mockAuth.role = null
    mockAuth.loading = false
    mockAuth.signInGoogle.mockReset()
    mockAuth.signInGuest.mockReset()
    mockAuth.saveRole.mockReset()
  })

  it('renders the brand and both sign-in paths', () => {
    renderSignIn()
    expect(screen.getByText('Atomic Shelfmates')).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /continue with google/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /continue as guest/i })).toBeInTheDocument()
  })

  it('renders the sign-in screen inside a main landmark', () => {
    const { container } = renderSignIn()
    expect(container.querySelector('main')).not.toBeNull()
  })

  it('renders the role picker inside a main landmark', () => {
    mockAuth.user = { uid: 'u1', displayName: 'Ada' }
    const { container } = renderSignIn()
    expect(container.querySelector('main')).not.toBeNull()
  })

  it('starts the Google sign-in flow', async () => {
    const user = userEvent.setup()
    renderSignIn()
    await user.click(screen.getByRole('button', { name: /continue with google/i }))
    expect(mockAuth.signInGoogle).toHaveBeenCalledTimes(1)
  })

  it('starts the anonymous guest flow', async () => {
    const user = userEvent.setup()
    renderSignIn()
    await user.click(screen.getByRole('button', { name: /continue as guest/i }))
    expect(mockAuth.signInGuest).toHaveBeenCalledTimes(1)
  })

  it('surfaces a sign-in error without faking success', async () => {
    mockAuth.signInGoogle.mockRejectedValue(new Error('popup closed'))
    const user = userEvent.setup()
    renderSignIn()
    await user.click(screen.getByRole('button', { name: /continue with google/i }))
    await waitFor(() => expect(screen.getByText(/popup closed/i)).toBeInTheDocument())
  })

  it('asks for a role when a user is signed in without one', () => {
    mockAuth.user = { uid: 'u1', displayName: 'Ada' }
    renderSignIn()
    expect(screen.getByText(/choose your role/i)).toBeInTheDocument()
    expect(screen.getByText('Reader')).toBeInTheDocument()
    expect(screen.getByText('Author')).toBeInTheDocument()
    expect(screen.getByText('Book Club Member')).toBeInTheDocument()
  })

  it('does not offer the ADMIN role', () => {
    mockAuth.user = { uid: 'u1', displayName: 'Ada' }
    renderSignIn()
    expect(screen.queryByText('Admin')).not.toBeInTheDocument()
  })

  it('saves the picked role exactly once', async () => {
    mockAuth.user = { uid: 'u1', displayName: 'Ada' }
    const user = userEvent.setup()
    renderSignIn()
    await user.click(screen.getByText('Reader'))
    await user.click(screen.getByRole('button', { name: /continue/i }))
    await waitFor(() => expect(mockAuth.saveRole).toHaveBeenCalledWith('READER'))
  })
})
