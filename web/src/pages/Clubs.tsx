import { useAuth } from '../auth/AuthContext'
import { useClubMemberships } from '../data/shelves'

export default function Clubs() {
  const { user } = useAuth()
  const memberships = useClubMemberships(user?.uid ?? null)

  return (
    <div className="sm-container">
      <h1>Clubs</h1>
      <div className="sm-card" style={{ marginBottom: 16 }}>
        <p style={{ margin: 0, fontSize: 14, color: 'var(--sm-text-muted)' }}>
          Club content (threads, events, ARC campaigns) is stored on your Android device. On the web you can see the clubs
          your account has joined; full club browsing and participation require the Android app.
        </p>
      </div>
      <div className="sm-section-title">Your memberships ({memberships.length})</div>
      {memberships.length === 0 && <div className="sm-empty"><h3>No memberships yet</h3><p>Join clubs in the Android app to see them here.</p></div>}
      <div style={{ display: 'grid', gap: 8 }}>
        {memberships.map(id => (
          <div key={id} className="sm-card" style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <span style={{ fontSize: 22 }}>👥</span>
            <div style={{ fontWeight: 600 }}>{id}</div>
          </div>
        ))}
      </div>
      <div style={{ marginTop: 20 }}>
        <a className="sm-btn sm-btn-ghost" href="https://play.google.com/store" target="_blank" rel="noreferrer">Get the Android app</a>
      </div>
    </div>
  )
}
