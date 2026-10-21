import { describe, it, expect, vi, afterEach } from 'vitest'
import { registerServiceWorker } from './pwa'

type Handler = () => void

/**
 * Minimal stand-in for a ServiceWorkerContainer. jsdom has no service worker
 * implementation, so the module is exercised against this fake instead.
 */
function installContainer(hasController: boolean) {
  const handlers = new Map<string, Handler[]>()
  const update = vi.fn().mockResolvedValue(undefined)
  const register = vi.fn().mockResolvedValue({ update })

  Object.defineProperty(navigator, 'serviceWorker', {
    configurable: true,
    value: {
      controller: hasController ? ({} as ServiceWorker) : null,
      register,
      addEventListener: (type: string, handler: Handler) => {
        handlers.set(type, [...(handlers.get(type) ?? []), handler])
      },
    },
  })

  return {
    register,
    update,
    fire: (type: string) => (handlers.get(type) ?? []).forEach((handler) => handler()),
  }
}

async function flush() {
  await Promise.resolve()
  await Promise.resolve()
}

describe('registerServiceWorker', () => {
  afterEach(() => {
    Object.defineProperty(navigator, 'serviceWorker', {
      configurable: true,
      value: undefined,
    })
    vi.restoreAllMocks()
  })

  it('registers the worker at the site root on load', async () => {
    const worker = installContainer(false)
    registerServiceWorker(vi.fn())

    window.dispatchEvent(new Event('load'))
    await flush()

    expect(worker.register).toHaveBeenCalledWith('/sw.js', { scope: '/' })
  })

  it('reloads once when a new worker takes over an existing page', async () => {
    const worker = installContainer(true)
    const reload = vi.fn()
    registerServiceWorker(reload)

    window.dispatchEvent(new Event('load'))
    await flush()

    worker.fire('controllerchange')
    worker.fire('controllerchange')

    expect(reload).toHaveBeenCalledTimes(1)
  })

  it('does not reload when the first worker claims a fresh page', async () => {
    const worker = installContainer(false)
    const reload = vi.fn()
    registerServiceWorker(reload)

    window.dispatchEvent(new Event('load'))
    await flush()

    worker.fire('controllerchange')

    expect(reload).not.toHaveBeenCalled()
  })

  it('checks for an update when the tab becomes visible again', async () => {
    const worker = installContainer(true)
    registerServiceWorker(vi.fn())

    window.dispatchEvent(new Event('load'))
    await flush()

    Object.defineProperty(document, 'visibilityState', {
      configurable: true,
      get: () => 'visible',
    })
    document.dispatchEvent(new Event('visibilitychange'))
    await flush()

    expect(worker.update).toHaveBeenCalled()
  })
})
