import { Link, useLocation, useNavigate, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

const NAV = [
  { to: '/', label: 'Home', icon: '🏠' },
  { to: '/discover', label: 'Discover', icon: '🔍' },
  { to: '/shelves', label: 'Shelves', icon: '📚' },
  { to: '/profile', label: 'Profile', icon: '👤' },
]

export function Layout() {
  const loc = useLocation()
  const nav = useNavigate()
  const { user, signOut } = useAuth()
  return (
    <div className="sm-app">
      <header className="sm-topbar">
        <div className="sm-topbar-inner">
          <Link to="/" className="sm-brand">✦ SHELFMATES ✦</Link>
          <nav className="sm-topnav">
            {NAV.map(n => (
              <Link key={n.to} to={n.to} className={loc.pathname === n.to ? 'active' : ''}>{n.label}</Link>
            ))}
            {user ? (
              <button className="sm-btn sm-btn-ghost" onClick={() => signOut().then(() => nav('/signin'))}>Sign out</button>
            ) : (
              <Link to="/signin" className="sm-btn sm-btn-primary">Sign in</Link>
            )}
          </nav>
        </div>
      </header>
      <main className="sm-main"><Outlet /></main>
      <nav className="sm-bottomnav">
        {NAV.map(n => (
          <Link key={n.to} to={n.to} className={loc.pathname === n.to ? 'active' : ''}>
            <span className="sm-bottomnav-icon">{n.icon}</span>
            <span>{n.label}</span>
          </Link>
        ))}
      </nav>
    </div>
  )
}

export default Layout
