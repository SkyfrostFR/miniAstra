import { create } from 'zustand'
import type {
  FreightTrain,
  Obstacle,
  PassengerTrain,
  Scenario,
  ScenarioStats,
  Signal,
  Station,
  TrackSegment,
} from './types'
import * as api from './api'

interface AppState {
  // ── Scenario ──────────────────────────────────────────────────────────────
  scenarios: Scenario[]
  activeScenarioId: string | null
  stats: ScenarioStats | null

  // ── Objects ───────────────────────────────────────────────────────────────
  stations: Station[]
  trackSegments: TrackSegment[]
  passengerTrains: PassengerTrain[]
  freightTrains: FreightTrain[]
  obstacles: Obstacle[]
  signals: Signal[]

  // ── UI ────────────────────────────────────────────────────────────────────
  selectedObjectId: string | null
  selectedObjectType: string | null
  activeTab: 'stations' | 'tracks' | 'passenger' | 'freight' | 'obstacles' | 'signals'
  splitRatio: number  // 0–100, percentage for map panel height
  currentTimeS: number  // temps courant simulé pour les obstacles (secondes)

  // ── Loading / errors ──────────────────────────────────────────────────────
  loading: boolean
  error: string | null
  mapVersion: number  // increments each time map needs to reload GeoJSON

  // ── Actions ───────────────────────────────────────────────────────────────
  loadScenarios: () => Promise<void>
  setActiveScenario: (id: string) => Promise<void>
  loadScenarioData: (id: string) => Promise<void>
  refreshStats: () => Promise<void>
  selectObject: (id: string | null, type: string | null) => void
  setActiveTab: (tab: AppState['activeTab']) => void
  setSplitRatio: (ratio: number) => void
  setCurrentTimeS: (t: number) => void
  clearError: () => void
}

export const useAppStore = create<AppState>((set, get) => ({
  scenarios: [],
  activeScenarioId: null,
  stats: null,
  stations: [],
  trackSegments: [],
  passengerTrains: [],
  freightTrains: [],
  obstacles: [],
  signals: [],
  selectedObjectId: null,
  selectedObjectType: null,
  activeTab: 'tracks',
  splitRatio: 55,
  currentTimeS: 0,
  loading: false,
  error: null,
  mapVersion: 0,

  loadScenarios: async () => {
    set({ loading: true, error: null })
    try {
      const scenarios = await api.scenarios.list()
      set({ scenarios, loading: false })

      // Auto-select last used scenario
      if (scenarios.length > 0 && !get().activeScenarioId) {
        try {
          const last = await api.scenarios.lastUsed()
          await get().setActiveScenario(last.id)
        } catch {
          await get().setActiveScenario(scenarios[0].id)
        }
      }
    } catch (e) {
      set({ loading: false, error: 'Impossible de charger les scénarios' })
    }
  },

  setActiveScenario: async (id: string) => {
    set({ activeScenarioId: id })
    await get().loadScenarioData(id)
  },

  loadScenarioData: async (id: string) => {
    set({ loading: true, error: null })
    try {
      const [stns, tracks, passenger, freight, obs, sigs, stats] = await Promise.all([
        api.stations.list(id),
        api.trackSegments.list(id),
        api.passengerTrains.list(id),
        api.freightTrains.list(id),
        api.obstacles.list(id),
        api.signals.list(id),
        api.scenarios.stats(id),
      ])
      set({
        stations: stns,
        trackSegments: tracks,
        passengerTrains: passenger,
        freightTrains: freight,
        obstacles: obs,
        signals: sigs,
        stats,
        loading: false,
        mapVersion: get().mapVersion + 1,
      })
    } catch (e) {
      set({ loading: false, error: 'Impossible de charger les données du scénario' })
    }
  },

  refreshStats: async () => {
    const id = get().activeScenarioId
    if (!id) return
    try {
      const stats = await api.scenarios.stats(id)
      set({ stats })
    } catch {
      // silent
    }
  },

  selectObject: (id, type) => set({ selectedObjectId: id, selectedObjectType: type }),
  setActiveTab: (tab) => set({ activeTab: tab }),
  setSplitRatio: (ratio) => set({ splitRatio: ratio }),
  setCurrentTimeS: (t) => set({ currentTimeS: t }),
  clearError: () => set({ error: null }),
}))
