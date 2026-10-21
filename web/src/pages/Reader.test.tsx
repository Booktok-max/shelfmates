import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'

/**
 * The Reader writes reading progress straight to the shared Firestore subtree
 * (`users/{uid}/reading_progress/{bookId}`) using the same document shape as the
 * Android app. These tests pin the path and the payload, because a mismatch
 * would silently desync progress between the two clients.
 */
const setDoc = vi.fn()
const doc = vi.fn((...args: any[]) => ({ path: args.slice(1).join('/') }))

vi.mock('firebase/firestore', () => ({
  doc: (...a: any[]) => (doc as any)(...a),
  setDoc: (...a: any[]) => (setDoc as any)(...a),
}))

vi.mock('../firebase', () => ({ db: { __fake: true } }))

const user = { uid: 'uid-reader-1' }
vi.mock('../auth/AuthContext', () => ({
  useAuth: () => ({ user }),
}))

vi.mock('jszip', () => ({ default: { loadAsync: vi.fn() } }))

// pdf.js is heavy and irrelevant here: stub it so the component renders fast.
vi.mock('pdfjs-dist', () => ({
  GlobalWorkerOptions: {},
  getDocument: vi.fn(),
}))
vi.mock('pdfjs-dist/build/pdf.worker.min.mjs?url', () => ({ default: 'worker.js' }))

import Reader from './Reader'

describe('Reader', () => {
  beforeEach(() => {
    setDoc.mockReset()
    doc.mockClear()
  })

  it('renders the upload affordance', () => {
    render(<Reader />)
    expect(screen.getByText(/open a local epub or pdf/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: 'Open file' })).toBeInTheDocument()
  })

  it('does not offer to save progress before a document is open', () => {
    render(<Reader />)
    expect(screen.queryByRole('button', { name: /save progress/i })).not.toBeInTheDocument()
  })

  it('rejects a file that is neither EPUB nor PDF', async () => {
    const { container } = render(<Reader />)
    const input = container.querySelector('input[type=file]') as HTMLInputElement
    const bad = new File([new Uint8Array([1, 2, 3])], 'notes.txt', { type: 'text/plain' })

    // applyAccept:false simulates an OS picker that let an unexpected file
    // through; the component must still refuse it rather than crash.
    const u = userEvent.setup({ applyAccept: false })
    await u.upload(input, bad)

    await waitFor(() =>
      expect(screen.getByText(/unsupported format/i)).toBeInTheDocument(),
    )
    expect(setDoc).not.toHaveBeenCalled()
  })

  it('writes progress to the Android-compatible path and shape', async () => {
    setDoc.mockResolvedValue(undefined)
    const { container } = render(<Reader />)

    // An EPUB with one spine item so the reader has a paragraph to save.
    const JSZip = (await import('jszip')).default as any
    const zip = {
      file: (name: string) => ({
        async: async () => {
          if (name === 'META-INF/container.xml') {
            return '<container><rootfile full-path="OEBPS/content.opf"/></container>'
          }
          if (name === 'OEBPS/content.opf') {
            return '<manifest><item id="c1" href="ch1.xhtml"/></manifest><spine><itemref idref="c1"/></spine>'
          }
          if (name === 'OEBPS/ch1.xhtml') {
            return '<html><body><p>Chapter one text.</p></body></html>'
          }
          return null
        },
      }),
    }
    JSZip.loadAsync.mockResolvedValue(zip)

    const input = container.querySelector('input[type=file]') as HTMLInputElement
    const epub = new File([new Uint8Array([0])], 'My Book.epub', { type: 'application/epub+zip' })

    const u = userEvent.setup()
    await u.upload(input, epub)

    const save = await screen.findByRole('button', { name: /save progress/i })
    await u.click(save)

    await waitFor(() => expect(setDoc).toHaveBeenCalledTimes(1))

    expect(doc).toHaveBeenCalledWith(
      expect.anything(),
      'users',
      'uid-reader-1',
      'reading_progress',
      'My Book',
    )

    const payload = setDoc.mock.calls[0][1]
    expect(payload).toMatchObject({
      userId: 'uid-reader-1',
      bookId: 'My Book',
      format: 'EPUB',
      lastPage: 0,
      lastChapterIndex: 0,
      totalPagesOrChapters: 1,
      progressPercent: 100,
    })
    expect(typeof payload.lastReadTimestamp).toBe('number')
    // merge:true so Android-written fields on the same doc are preserved.
    expect(setDoc.mock.calls[0][2]).toEqual({ merge: true })
  })
})
