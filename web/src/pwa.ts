/**
 * Service-worker registration and shell-update handling.
 *
 * `registerType: 'autoUpdate'` makes the generated worker call `skipWaiting()`
 * and `clientsClaim()`, so a new release takes control as soon as it installs.
 * That is only safe if the running page matches the new worker: Firebase
 * Hosting drops the previous release's hashed chunks, and the SPA rewrite then
 * answers a missing `/assets/*.js` request with `index.html`. A stale shell
 * that lazy-loads one of those chunks would fail with a module-parse error, so
 * reload once when control changes and the page always matches the precache.
 *
 * The previous build used the plugin's injected registration script, which only
 * called `register()` and never reloaded. That left long-lived tabs on the old
 * shell until the user happened to reload.
 */
export function registerServiceWorker(
  reload: () => void = () => window.location.reload(),
): void {
  if (!('serviceWorker' in navigator)) return

  // A first-time visitor has no controller yet; letting the new worker claim
  // them does not need a reload because the document is already the new shell.
  const hadController = Boolean(navigator.serviceWorker.controller)
  let reloading = false

  navigator.serviceWorker.addEventListener('controllerchange', () => {
    if (!hadController || reloading) return
    reloading = true
    reload()
  })

  window.addEventListener('load', () => {
    navigator.serviceWorker
      .register('/sw.js', { scope: '/' })
      .then((registration) => {
        // Browsers throttle update checks, so re-check when a long-lived tab is
        // foregrounded instead of waiting for the next full navigation.
        document.addEventListener('visibilitychange', () => {
          if (document.visibilityState !== 'visible') return
          registration.update().catch(() => undefined)
        })
      })
      .catch(() => undefined)
  })
}
