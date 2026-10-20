import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const ROLES = [
  { value: 'READER', label: 'Reader', desc: 'Discover, save, and track books.' },
  { value: 'AUTHOR', label: 'Author', desc: 'Share ARCs and connect with readers.' },
  { value: 'BOOK_CLUB_MEMBER', label: 'Book Club Member', desc: 'Join clubs and discussions.' },
] as const

export default function SignIn() {
  const { user, role, loading, signInGoogle, signInGuest, saveRole } = useAuth()
  const nav = useNavigate()
  const [busy, setBusy] = useState(false)
  const [err, setErr] = useState<string | null>(null)
  const [picked, setPicked] = useState<string | null>(null)

  useEffect(() => {
    if (!loading && user && role) nav('/', { replace: true })
  }, [user, role, loading, nav])

  async function doGoogle() {
    setBusy(true); setErr(null)
    try { await signInGoogle() } catch (e: any) { setErr(e?.message ?? 'Sign-in failed') }
    finally { setBusy(false) }
  }
  async function doGuest() {
    setBusy(true); setErr(null)
    try { await signInGuest() } catch (e: any) { setErr(e?.message ?? 'Guest sign-in failed') }
    finally { setBusy(false) }
  }
  async function confirmRole() {
    if (!picked) return
    setBusy(true); setErr(null)
    try { await saveRole(picked as any); nav('/', { replace: true }) }
    catch (e: any) { setErr(e?.message ?? 'Could not save role') }
    finally { setBusy(false) }
  }

  if (loading) return <div className="sm-container sm-empty">Loading…</div>

  // Role selection step
  if (user && !role) {
    return (
      <div className="sm-container" style={{ maxWidth: 520, paddingTop: 40 }}>
        <h1 style={{ textAlign: 'center' }}>Choose your role</h1>
        <p style={{ textAlign: 'center', color: 'var(--sm-text-muted)' }}>This can only be set once.</p>
        <div style={{ display: 'grid', gap: 12, marginTop: 24 }}>
          {ROLES.map(r => (
            <button key={r.value} className="sm-card" style={{
              textAlign: 'left', cursor: 'pointer',
              border: picked === r.value ? '2px solid var(--sm-gold)' : '1px solid var(--sm-rule)',
              background: picked === r.value ? 'var(--sm-gold-light)' : 'var(--sm-surface)',
            }} onClick={() => setPicked(r.value)}>
              <div style={{ fontWeight: 700 }}>{r.label}</div>
              <div style={{ fontSize: 13, color: 'var(--sm-text-muted)' }}>{r.desc}</div>
            </button>
          ))}
        </div>
        {err && <p style={{ color: 'var(--sm-coral)', marginTop: 12 }}>{err}</p>}
        <button className="sm-btn sm-btn-primary" style={{ width: '100%', marginTop: 20 }} disabled={!picked || busy} onClick={confirmRole}>
          {busy ? 'Saving…' : 'Continue'}
        </button>
      </div>
    )
  }

  return (
    <div className="sm-container" style={{ maxWidth: 440, paddingTop: 60 }}>
      <div style={{ textAlign: 'center', marginBottom: 32 }}>
        <div style={{ fontSize: 48 }}>📚</div>
        <h1 style={{ margin: '8px 0 4px' }}>Atomic Shelfmates</h1>
        <p style={{ color: 'var(--sm-text-muted)' }}>A readers &amp; authors guild</p>
      </div>
      <div className="sm-card" style={{ display: 'grid', gap: 12 }}>
        <button className="sm-btn sm-btn-crimson" disabled={busy} onClick={doGoogle}>
          Continue with Google
        </button>
        <button className="sm-btn sm-btn-ghost" disabled={busy} onClick={doGuest}>
          Continue as Guest
        </button>
      </div>
      {err && <p style={{ color: 'var(--sm-coral)', marginTop: 12, textAlign: 'center' }}>{err}</p>}
      <p style={{ fontSize: 12, color: 'var(--sm-text-subtle)', textAlign: 'center', marginTop: 20 }}>
        By continuing you agree to the Shelfmates terms. Guest accounts are anonymous Firebase sessions.
      </p>
    </div>
  )
}
