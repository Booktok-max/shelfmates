import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import Discover from './Discover'

const searchVolumes = vi.fn()
vi.mock('../api/googleBooks', () => ({
  searchVolumes: (...a: any[]) => searchVolumes(...a),
  coverUrl: (v: any) => v.volumeInfo.imageLinks?.thumbnail ?? '',
  authorsStr: (v: any) => (v.volumeInfo.authors ?? []).join(', '),
}))

function renderDiscover() {
  return render(
    <MemoryRouter initialEntries={['/discover']}>
      <Discover />
    </MemoryRouter>,
  )
}

describe('Discover', () => {
  beforeEach(() => {
    searchVolumes.mockReset()
  })

  it('shows results returned by the search API', async () => {
    searchVolumes.mockResolvedValue({
      totalItems: 1,
      items: [{
        id: 'v1',
        volumeInfo: { title: 'Dune', authors: ['Frank Herbert'], imageLinks: { thumbnail: 'https://x/d.jpg' } },
      }],
    })
    const user = userEvent.setup()
    renderDiscover()
    await user.type(screen.getByPlaceholderText(/search books/i), 'dune')
    await user.click(screen.getByRole('button', { name: 'Search' }))
    await waitFor(() => expect(screen.getByText('Dune')).toBeInTheDocument())
    expect(screen.getByText('Frank Herbert')).toBeInTheDocument()
  })

  it('shows an error state when the API fails', async () => {
    searchVolumes.mockRejectedValue(new Error('Google Books error 403'))
    const user = userEvent.setup()
    renderDiscover()
    await user.type(screen.getByPlaceholderText(/search books/i), 'quux')
    await user.click(screen.getByRole('button', { name: 'Search' }))
    await waitFor(() => expect(screen.getByText(/search failed/i)).toBeInTheDocument())
    expect(screen.getByText(/403/)).toBeInTheDocument()
  })

  it('does not show a fake result list when the API returns nothing', async () => {
    searchVolumes.mockResolvedValue({ totalItems: 0, items: [] })
    const user = userEvent.setup()
    renderDiscover()
    await user.type(screen.getByPlaceholderText(/search books/i), 'zzzz')
    await user.click(screen.getByRole('button', { name: 'Search' }))
    await waitFor(() => expect(screen.getByText(/no results/i)).toBeInTheDocument())
  })

  it('offers subject topics instead of unsupported bestseller, trending, and award feeds', () => {
    renderDiscover()

    expect(screen.getByRole('button', { name: 'Romance' })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Sci-Fi' })).toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'NYT Bestsellers' })).not.toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Trending' })).not.toBeInTheDocument()
    expect(screen.queryByRole('button', { name: 'Award Winners' })).not.toBeInTheDocument()
  })

})
