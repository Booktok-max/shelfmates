import { useState, useRef } from 'react'
import { useAuth } from '../auth/AuthContext'
import { doc, setDoc } from 'firebase/firestore'
import { db } from '../firebase'
import { buildReadingProgressDoc } from '../data/serialize'

type ReaderState = { title: string; paragraphs: string[]; format: 'EPUB' | 'PDF' }

export default function Reader() {
  const { user } = useAuth()
  const [state, setState] = useState<ReaderState | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [index, setIndex] = useState(0) // current page/paragraph index
  const [fontSize, setFontSize] = useState(18)
  const [theme, setTheme] = useState<'light' | 'sepia' | 'dark'>('light')
  const fileRef = useRef<HTMLInputElement>(null)

  async function handleFile(f: File) {
    setLoading(true); setError(null); setState(null)
    try {
      const lower = f.name.toLowerCase()
      if (lower.endsWith('.epub')) {
        const JSZip = (await import('jszip')).default
        const zip = await JSZip.loadAsync(f)
        // find OPF
        const container = await zip.file('META-INF/container.xml')?.async('string')
        let opfPath = 'OEBPS/content.opf'
        if (container) {
          const m = container.match(/full-path="([^"]+)"/)
          if (m) opfPath = m[1]
        }
        const opf = await zip.file(opfPath)?.async('string')
        const base = opfPath.replace(/[^/]+$/, '')
        // spine order
        const manifest: Record<string, string> = {}
        const manifestRe = /<item[^>]*id="([^"]+)"[^>]*href="([^"]+)"/g
        let mm
        while ((mm = manifestRe.exec(opf || '')) !== null) manifest[mm[1]] = mm[2]
        const spine: string[] = []
        const spineRe = /<itemref[^>]*idref="([^"]+)"/g
        let sm
        while ((sm = spineRe.exec(opf || '')) !== null) {
          const href = manifest[sm[1]]
          if (href) spine.push(base + href)
        }
        const paragraphs: string[] = []
        let title = f.name.replace(/\.epub$/i, '')
        for (const p of spine) {
          const html = await zip.file(p)?.async('string')
          if (!html) continue
          // strip tags
          const text = html.replace(/<script[\s\S]*?<\/script>/gi, '')
            .replace(/<style[\s\S]*?<\/style>/gi, '')
            .replace(/<[^>]+>/g, ' ')
            .replace(/\s+/g, ' ').trim()
          if (text) paragraphs.push(text)
        }
        if (paragraphs.length === 0) throw new Error('No readable text found in EPUB')
        setState({ title, paragraphs, format: 'EPUB' })
        setIndex(0)
      } else if (lower.endsWith('.pdf')) {
        const pdfjsLib = await import('pdfjs-dist')
        const pdfWorker = (await import('pdfjs-dist/build/pdf.worker.min.mjs?url')).default
        pdfjsLib.GlobalWorkerOptions.workerSrc = pdfWorker
        const buf = await f.arrayBuffer()
        const pdf = await pdfjsLib.getDocument({ data: buf }).promise
        const paragraphs: string[] = []
        for (let i = 1; i <= pdf.numPages; i++) {
          const page = await pdf.getPage(i)
          const content = await page.getTextContent()
          const text = content.items.map((it: any) => it.str).join(' ')
          paragraphs.push(`— Page ${i} —\n${text}`)
        }
        setState({ title: f.name.replace(/\.pdf$/i, ''), paragraphs, format: 'PDF' })
        setIndex(0)
      } else {
        throw new Error('Unsupported format. Please choose an EPUB or PDF file.')
      }
    } catch (e: any) {
      setError(e?.message ?? 'Failed to read file')
    } finally { setLoading(false) }
  }

  async function saveProgress() {
    if (!user || !state) return
    const pct = state.paragraphs.length ? Math.round(((index + 1) / state.paragraphs.length) * 100) : 0
    try {
      await setDoc(doc(db, 'users', user.uid, 'reading_progress', state.title),
        buildReadingProgressDoc(user.uid, state.title, index, index, state.paragraphs.length, pct, state.format),
        { merge: true })
    } catch (e) { console.warn(e) }
  }

  const themes = {
    light: { bg: '#FFFFFF', text: '#0F172A', sec: '#475569' },
    sepia: { bg: '#FAF4EB', text: '#2C2216', sec: '#6B5844' },
    dark: { bg: '#09090B', text: '#FAFAFA', sec: '#A1A1AA' },
  } as const
  const t = themes[theme]

  return (
    <div className="sm-container">
      <h1>Reader</h1>
      <p style={{ color: 'var(--sm-text-muted)', fontSize: 14 }}>
        Open a local EPUB or PDF file. Reading progress is saved to your account.
        (Shared ARC manuscripts from the Android app are not downloadable on the web yet.)
      </p>

      <div className="sm-card" style={{ display: 'flex', gap: 8, flexWrap: 'wrap', alignItems: 'center', margin: '12px 0' }}>
        <input ref={fileRef} type="file" accept=".epub,.pdf,application/epub+zip,application/pdf" style={{ display: 'none' }} onChange={e => { const f = e.target.files?.[0]; if (f) handleFile(f) }} />
        <button className="sm-btn sm-btn-primary" onClick={() => fileRef.current?.click()} disabled={loading}>{loading ? 'Reading…' : 'Open file'}</button>
        {state && (
          <>
            <button className="sm-btn sm-btn-ghost" onClick={() => setFontSize(s => Math.max(12, s - 2))}>A−</button>
            <button className="sm-btn sm-btn-ghost" onClick={() => setFontSize(s => Math.min(32, s + 2))}>A+</button>
            {(['light','sepia','dark'] as const).map(th => (
              <button key={th} className="sm-chip" style={{ background: theme === th ? 'var(--sm-crimson)' : undefined, color: theme === th ? '#fff' : undefined }} onClick={() => setTheme(th)}>{th}</button>
            ))}
            <button className="sm-btn sm-btn-ghost" onClick={saveProgress}>Save progress</button>
          </>
        )}
      </div>

      {error && <div className="sm-empty"><h3>Could not read file</h3><p>{error}</p></div>}

      {state && (
        <div style={{ background: t.bg, color: t.text, borderRadius: 12, padding: '24px 20px', minHeight: 300, boxShadow: 'var(--sm-shadow)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12, fontSize: 12, color: t.sec }}>
            <span>{state.title} · {state.format}</span>
            <span>{index + 1} / {state.paragraphs.length}</span>
          </div>
          <div style={{ fontSize, lineHeight: 1.7, whiteSpace: 'pre-wrap', maxHeight: '60vh', overflow: 'auto' }}>
            {state.paragraphs[index]}
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 16 }}>
            <button className="sm-btn sm-btn-ghost" disabled={index === 0} onClick={() => setIndex(i => Math.max(0, i - 1))}>Previous</button>
            <button className="sm-btn sm-btn-primary" disabled={index >= state.paragraphs.length - 1} onClick={() => setIndex(i => Math.min(state.paragraphs.length - 1, i + 1))}>Next</button>
          </div>
        </div>
      )}
    </div>
  )
}
