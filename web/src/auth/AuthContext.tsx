import { createContext, useContext, useEffect, useState } from 'react'
import type { ReactNode } from 'react'
import {
  onAuthStateChanged, signInWithPopup, signInAnonymously, signOut as fbSignOut,
} from 'firebase/auth'
import type { User } from 'firebase/auth'
import { doc, getDoc, setDoc } from 'firebase/firestore'
import { auth, db, googleProvider } from '../firebase'
import { buildProfileDoc } from '../data/serialize'

export type Role = 'READER' | 'AUTHOR' | 'BOOK_CLUB_MEMBER' | 'ADMIN' | null

interface AuthContextValue {
  user: User | null
  role: Role
  loading: boolean
  signInGoogle: () => Promise<void>
  signInGuest: () => Promise<void>
  signOut: () => Promise<void>
  saveRole: (role: Exclude<Role, 'ADMIN' | null>) => Promise<void>
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

const SELF_ASSIGNABLE = ['READER', 'AUTHOR', 'BOOK_CLUB_MEMBER'] as const

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [role, setRole] = useState<Role>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const unsub = onAuthStateChanged(auth, async (u) => {
      setUser(u)
      if (u) {
        // sync profile (merge, never overwrites role)
        try {
          await setDoc(doc(db, 'users', u.uid), buildProfileDoc(u.uid, {
            email: u.email,
            displayName: u.displayName,
            photoUrl: u.photoURL,
            isAnonymous: u.isAnonymous,
          }), { merge: true })
        } catch (e) {
          console.warn('profile sync failed', e)
        }
        // fetch role
        try {
          const snap = await getDoc(doc(db, 'users', u.uid))
          const r = snap.exists() ? (snap.data().role as string | undefined) : undefined
          setRole((r as Role) ?? null)
        } catch {
          setRole(null)
        }
      } else {
        setRole(null)
      }
      setLoading(false)
    })
    return () => unsub()
  }, [])

  async function signInGoogle() {
    await signInWithPopup(auth, googleProvider)
  }
  async function signInGuest() {
    await signInAnonymously(auth)
  }
  async function signOut() {
    await fbSignOut(auth)
  }
  async function saveRole(r: Exclude<Role, 'ADMIN' | null>) {
    if (!user) throw new Error('not signed in')
    if (!SELF_ASSIGNABLE.includes(r as any)) throw new Error('invalid role')
    // only allow if no role yet (rule enforces immutability)
    const snap = await getDoc(doc(db, 'users', user.uid))
    if (snap.exists() && snap.data().role) {
      throw new Error('role already set')
    }
    await setDoc(doc(db, 'users', user.uid), { role: r, uid: user.uid, userId: user.uid }, { merge: true })
    setRole(r)
  }

  return (
    <AuthContext.Provider value={{ user, role, loading, signInGoogle, signInGuest, signOut, saveRole }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth outside provider')
  return ctx
}
