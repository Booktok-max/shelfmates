import { useAuth } from '../auth/AuthContext'
import { useShelfBooks, useReadingProgress, useClubMemberships } from '../data/shelves'

export default function Profile() {
  const { user, role, signOut } = useAuth()
  const { books } = useShelfBooks(user?.uid ?? null)
  const progress = useReadingProgress(user?.uid ?? null)
  const memberships = useClubMemberships(user?.uid ?? null)

  return (
    <div className="sm-container">
      <h1>Profile</h1>
      <div className="sm-card" style={{ display: 'flex', gap: 16, alignItems: 'center', flexWrap: 'wrap' }}>
        {user?.photoURL ? <img src={user.photoURL} alt="" style={{ width: 64, height: 64, borderRadius: '50%' }} /> : <div style={{ width:64, height:64, borderRadius:'50%', background:'var(--sm-surface-alt)', display:'flex', alignItems:'center', justifyContent:'center', fontSize:28 }}>👤</div>}
        <div style={{ flex: 1 }}>
          <div style={{ fontWeight: 700, fontSize: 18 }}>{user?.displayName ?? 'Reader'}</div>
          <div style={{ fontSize: 13, color: 'var(--sm-text-muted)' }}>{user?.email || (user?.isAnonymous ? 'Anonymous guest' : '')}</div>
          <div style={{ marginTop: 6 }}><span className="sm-chip">{role ?? 'No role'}</span></div>
        </div>
        <button className="sm-btn sm-btn-ghost" onClick={() => signOut()}>Sign out</button>
      </div>

      <div className="sm-section-title">Stats</div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(120px, 1fr))', gap: 12 }}>
        <div className="sm-card" style={{ textAlign: 'center' }}><div style={{ fontSize: 24, fontWeight: 800 }}>{books.length}</div><div style={{ fontSize: 12, color: 'var(--sm-text-muted)' }}>Saved books</div></div>
        <div className="sm-card" style={{ textAlign: 'center' }}><div style={{ fontSize: 24, fontWeight: 800 }}>{progress.length}</div><div style={{ fontSize: 12, color: 'var(--sm-text-muted)' }}>Reading progress</div></div>
        <div className="sm-card" style={{ textAlign: 'center' }}><div style={{ fontSize: 24, fontWeight: 800 }}>{memberships.length}</div><div style={{ fontSize: 12, color: 'var(--sm-text-muted)' }}>Club memberships</div></div>
      </div>

      <div className="sm-section-title">Platform features not on web</div>
      <div className="sm-card">
        <p style={{ margin: 0, fontSize: 14, color: 'var(--sm-text-muted)' }}>
          Rewards/bookmarks, ARC campaigns, notifications, voice rooms, and AI companions are Android-only today
          (backed by the device's local database). They are not shared with the web and are not shown here.
        </p>
      </div>
    </div>
  )
}
