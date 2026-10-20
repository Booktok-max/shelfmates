import { Link } from 'react-router-dom'
export default function NotFound() {
  return (
    <div className="sm-container sm-empty">
      <h3>Page not found</h3>
      <p>The page you're looking for doesn't exist.</p>
      <Link to="/" className="sm-btn sm-btn-primary">Go home</Link>
    </div>
  )
}
