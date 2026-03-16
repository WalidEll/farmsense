/**
 * offline-queue.ts
 * Stores pending API calls in PouchDB (IndexedDB under the hood).
 * On reconnect, replays them in order.
 *
 * US-062: Queue manual readings while offline
 *
 * PouchDB is loaded lazily to avoid crashing Vite's CJS→ESM shim
 * during initial module evaluation (which breaks Vue Router startup).
 */
import { api } from './api'

interface QueuedRequest {
  _id: string
  url: string
  method: 'POST' | 'PUT' | 'DELETE'
  body?: unknown
  createdAt: string
  retries: number
}

let _db: any = null

async function getDb() {
  if (_db) return _db
  const PouchDBModule = await import('pouchdb')
  const PouchDB = (PouchDBModule as any).default ?? PouchDBModule
  _db = new PouchDB('farmsense-offline-queue')
  return _db
}

export const offlineQueue = {
  async enqueue(method: 'POST' | 'PUT' | 'DELETE', url: string, body?: unknown) {
    const db = await getDb()
    const doc: QueuedRequest = {
      _id: `${Date.now()}-${Math.random().toString(36).slice(2)}`,
      url, method, body,
      createdAt: new Date().toISOString(),
      retries: 0
    }
    await db.put(doc)
    console.log(`[OfflineQueue] Queued ${method} ${url}`)
  },

  async flush() {
    const db = await getDb()
    const { rows } = await db.allDocs({ include_docs: true })
    const items = rows.map((r: any) => r.doc!).sort((a: any, b: any) => a.createdAt.localeCompare(b.createdAt))

    for (const item of items) {
      try {
        if (item.method === 'POST')   await api.post(item.url, item.body)
        if (item.method === 'PUT')    await api.put(item.url, item.body)
        if (item.method === 'DELETE') await api.delete(item.url)

        await db.remove(item._id, item._rev!)
        console.log(`[OfflineQueue] Synced ${item.method} ${item.url}`)
      } catch (e) {
        item.retries++
        if (item.retries >= 3) {
          console.error(`[OfflineQueue] Giving up on ${item.url} after 3 retries`)
          await db.remove(item._id, item._rev!)
        } else {
          await db.put(item)
        }
      }
    }
  },

  async count(): Promise<number> {
    const db = await getDb()
    const { total_rows } = await db.info()
    return total_rows
  }
}

// Auto-flush when online
window.addEventListener('online', () => {
  console.log('[OfflineQueue] Back online — flushing queue')
  offlineQueue.flush()
})
