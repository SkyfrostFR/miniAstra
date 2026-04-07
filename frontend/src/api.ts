import axios from 'axios'
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

const client = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

// ─── Scenarios ───────────────────────────────────────────────────────────────

export const scenarios = {
  list: () => client.get<Scenario[]>('/scenarios').then(r => r.data),
  get: (id: string) => client.get<Scenario>(`/scenarios/${id}`).then(r => r.data),
  lastUsed: () => client.get<Scenario>('/scenarios/last-used').then(r => r.data),
  create: (data: Omit<Scenario, 'id' | 'createdAt' | 'updatedAt'>) =>
    client.post<Scenario>('/scenarios', data).then(r => r.data),
  update: (id: string, data: Partial<Scenario>) =>
    client.put<Scenario>(`/scenarios/${id}`, data).then(r => r.data),
  delete: (id: string) => client.delete(`/scenarios/${id}`),
  stats: (id: string) => client.get<ScenarioStats>(`/scenarios/${id}/stats`).then(r => r.data),
  geojson: (id: string) => client.get<GeoJSON.FeatureCollection>(`/scenarios/${id}/geojson`).then(r => r.data),
  export: (id: string) =>
    client.get(`/scenarios/${id}/export`, { responseType: 'blob' }).then(r => r.data),
}

// ─── Track Segments ──────────────────────────────────────────────────────────

export const trackSegments = {
  list: (scenarioId: string) =>
    client.get<TrackSegment[]>(`/scenarios/${scenarioId}/track-segments`).then(r => r.data),
  create: (scenarioId: string, data: Omit<TrackSegment, 'id' | 'scenarioId' | 'lengthM'>) =>
    client.post<TrackSegment>(`/scenarios/${scenarioId}/track-segments`, data).then(r => r.data),
  update: (scenarioId: string, id: string, data: Partial<TrackSegment>) =>
    client.put<TrackSegment>(`/scenarios/${scenarioId}/track-segments/${id}`, data).then(r => r.data),
  delete: (scenarioId: string, id: string) =>
    client.delete(`/scenarios/${scenarioId}/track-segments/${id}`),
}

// ─── Passenger Trains ────────────────────────────────────────────────────────

export const passengerTrains = {
  list: (scenarioId: string) =>
    client.get<PassengerTrain[]>(`/scenarios/${scenarioId}/passenger-trains`).then(r => r.data),
  create: (scenarioId: string, data: Omit<PassengerTrain, 'id' | 'scenarioId'>) =>
    client.post<PassengerTrain>(`/scenarios/${scenarioId}/passenger-trains`, data).then(r => r.data),
  update: (scenarioId: string, id: string, data: Partial<PassengerTrain>) =>
    client.put<PassengerTrain>(`/scenarios/${scenarioId}/passenger-trains/${id}`, data).then(r => r.data),
  delete: (scenarioId: string, id: string) =>
    client.delete(`/scenarios/${scenarioId}/passenger-trains/${id}`),
}

// ─── Freight Trains ──────────────────────────────────────────────────────────

export const freightTrains = {
  list: (scenarioId: string) =>
    client.get<FreightTrain[]>(`/scenarios/${scenarioId}/freight-trains`).then(r => r.data),
  create: (scenarioId: string, data: Omit<FreightTrain, 'id' | 'scenarioId'>) =>
    client.post<FreightTrain>(`/scenarios/${scenarioId}/freight-trains`, data).then(r => r.data),
  update: (scenarioId: string, id: string, data: Partial<FreightTrain>) =>
    client.put<FreightTrain>(`/scenarios/${scenarioId}/freight-trains/${id}`, data).then(r => r.data),
  delete: (scenarioId: string, id: string) =>
    client.delete(`/scenarios/${scenarioId}/freight-trains/${id}`),
}

// ─── Obstacles ───────────────────────────────────────────────────────────────

export const obstacles = {
  list: (scenarioId: string) =>
    client.get<Obstacle[]>(`/scenarios/${scenarioId}/obstacles`).then(r => r.data),
  create: (scenarioId: string, data: Omit<Obstacle, 'id' | 'scenarioId'>) =>
    client.post<Obstacle>(`/scenarios/${scenarioId}/obstacles`, data).then(r => r.data),
  update: (scenarioId: string, id: string, data: Partial<Obstacle>) =>
    client.put<Obstacle>(`/scenarios/${scenarioId}/obstacles/${id}`, data).then(r => r.data),
  delete: (scenarioId: string, id: string) =>
    client.delete(`/scenarios/${scenarioId}/obstacles/${id}`),
}

// ─── Signals ─────────────────────────────────────────────────────────────────

export const signals = {
  list: (scenarioId: string) =>
    client.get<Signal[]>(`/scenarios/${scenarioId}/signals`).then(r => r.data),
  create: (scenarioId: string, data: Omit<Signal, 'id' | 'scenarioId'>) =>
    client.post<Signal>(`/scenarios/${scenarioId}/signals`, data).then(r => r.data),
  update: (scenarioId: string, id: string, data: Partial<Signal>) =>
    client.put<Signal>(`/scenarios/${scenarioId}/signals/${id}`, data).then(r => r.data),
  delete: (scenarioId: string, id: string) =>
    client.delete(`/scenarios/${scenarioId}/signals/${id}`),
}

// ─── Stations ────────────────────────────────────────────────────────────────

export const stations = {
  list: (scenarioId: string) =>
    client.get<Station[]>(`/scenarios/${scenarioId}/stations`).then(r => r.data),
  create: (scenarioId: string, data: Omit<Station, 'id' | 'scenarioId'>) =>
    client.post<Station>(`/scenarios/${scenarioId}/stations`, data).then(r => r.data),
  update: (scenarioId: string, id: string, data: Partial<Station>) =>
    client.put<Station>(`/scenarios/${scenarioId}/stations/${id}`, data).then(r => r.data),
  delete: (scenarioId: string, id: string) =>
    client.delete(`/scenarios/${scenarioId}/stations/${id}`),
}

// ─── Validation ──────────────────────────────────────────────────────────────

export interface ValidationAnomaly {
  objectType: string
  objectId: string
  objectName: string
  rule: string
  description: string
}

export interface ValidationReport {
  scenarioId: string
  anomalyCount: number
  anomalies: ValidationAnomaly[]
}

export const validation = {
  report: (scenarioId: string) =>
    client.get<ValidationReport>(`/scenarios/${scenarioId}/validation-report`).then(r => r.data),
}
