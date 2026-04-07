// ─── Domaine ─────────────────────────────────────────────────────────────────

export interface Scenario {
  id: string
  name: string
  description: string | null
  durationS: number
  startTime: string
  createdAt: string
  updatedAt: string
}

export interface Station {
  id: string
  scenarioId: string
  name: string
  lat: number
  lon: number
}

export interface TrackSegment {
  id: string
  scenarioId: string
  name: string
  startLat: number
  startLon: number
  endLat: number
  endLon: number
  waypoints: [number, number][]
  lengthM: number
  maxSpeedKmh: number
  trackCount: number
  electrification: string
  gradePermil: number
  startStationId: string | null
  endStationId: string | null
}

export interface PassengerTrain {
  id: string
  scenarioId: string
  name: string
  modelCode: string
  trackId: string
  positionM: number
  direction: 'PAIR' | 'IMPAIR'
  initialSpeedKmh: number
  passengerCount: number
  serviceNumber: string | null
}

export interface FreightTrain {
  id: string
  scenarioId: string
  name: string
  modelCode: string
  trackId: string
  positionM: number
  direction: 'PAIR' | 'IMPAIR'
  initialSpeedKmh: number
  loadT: number
  cargoType: string
}

export interface Obstacle {
  id: string
  scenarioId: string
  name: string
  type: string
  trackId: string
  positionM: number
  lengthM: number
  blocking: boolean
  speedLimitKmh: number
  visibilityM: number
  appearAtS: number
  disappearAtS: number | null
}

export interface Signal {
  id: string
  scenarioId: string
  name: string
  type: string
  trackId: string
  positionM: number
  direction: 'PAIR' | 'IMPAIR'
  initialState: string
}

export interface ScenarioStats {
  trackSegments: number
  passengerTrains: number
  freightTrains: number
  obstacles: number
  signals: number
}

// ─── GeoJSON ─────────────────────────────────────────────────────────────────

export type ObjectType =
  | 'TRACK_SEGMENT'
  | 'PASSENGER_TRAIN'
  | 'FREIGHT_TRAIN'
  | 'OBSTACLE'
  | 'SIGNAL'
  | 'STATION'
