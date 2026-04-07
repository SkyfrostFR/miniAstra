import { useState } from 'react'
import { z } from 'zod'
import { Modal } from './Modal'
import * as api from '../api'
import { useAppStore } from '../store'

type Tab = 'stations' | 'tracks' | 'passenger' | 'freight' | 'obstacles' | 'signals'

interface Props {
  tab: Tab
  onClose: () => void
}

// ─── Zod schemas ─────────────────────────────────────────────────────────────

const stationsSchema = z.object({
  name: z.string().min(1, 'Le nom est requis'),
  lat: z.coerce.number().min(-90).max(90),
  lon: z.coerce.number().min(-180).max(180),
})

const tracksSchema = z.object({
  name: z.string().min(1, 'Le nom est requis'),
  startLat: z.coerce.number().min(-90).max(90),
  startLon: z.coerce.number().min(-180).max(180),
  endLat: z.coerce.number().min(-90).max(90),
  endLon: z.coerce.number().min(-180).max(180),
  maxSpeedKmh: z.coerce.number().min(0, 'La vitesse max doit être >= 0'),
  trackCount: z.coerce.number().int().min(1, 'Le nombre de voies doit être >= 1'),
  gradePermil: z.coerce.number(),
})

const passengerSchema = z.object({
  name: z.string().min(1, 'Le nom est requis'),
  modelCode: z.string().min(1, 'Le modèle est requis'),
  trackId: z.string().min(1, 'La voie est requise'),
  positionM: z.coerce.number().min(0, 'La position doit être >= 0'),
  initialSpeedKmh: z.coerce.number().min(0, 'La vitesse initiale doit être >= 0'),
  passengerCount: z.coerce.number().int().min(0, 'Le nombre de passagers doit être >= 0'),
})

const freightSchema = z.object({
  name: z.string().min(1, 'Le nom est requis'),
  modelCode: z.string().min(1, 'Le modèle est requis'),
  trackId: z.string().min(1, 'La voie est requise'),
  positionM: z.coerce.number().min(0, 'La position doit être >= 0'),
  loadT: z.coerce.number().min(0, 'La charge doit être >= 0'),
  cargoType: z.enum(['VIDE', 'GENERAL', 'VRAC', 'CITERNE', 'DANGEREUX'], { required_error: 'Le type de fret est requis' }),
})

const obstaclesSchema = z.object({
  name: z.string().min(1, 'Le nom est requis'),
  trackId: z.string().min(1, 'La voie est requise'),
  positionM: z.coerce.number().min(0, 'La position doit être >= 0'),
  lengthM: z.coerce.number().gt(0, 'La longueur doit être > 0'),
  speedLimitKmh: z.coerce.number().min(0, 'La vitesse limite doit être >= 0'),
  visibilityM: z.coerce.number().min(0, 'La visibilité doit être >= 0'),
  appearAtS: z.coerce.number().min(0, "Le temps d'apparition doit être >= 0"),
})

const signalsSchema = z.object({
  name: z.string().min(1, 'Le nom est requis'),
  trackId: z.string().min(1, 'La voie est requise'),
  positionM: z.coerce.number().min(0, 'La position doit être >= 0'),
  type: z.enum(['CARRE', 'SEMAPHORE', 'AVERTISSEMENT', 'GUIDON', 'TGV_R']),
  direction: z.enum(['PAIR', 'IMPAIR', 'BIDIR']),
  initialState: z.enum(['VOIE_LIBRE', 'ARRET', 'AVERTISSEMENT']),
})

const SCHEMAS: Record<Tab, z.ZodObject<z.ZodRawShape>> = {
  stations: stationsSchema,
  tracks: tracksSchema,
  passenger: passengerSchema,
  freight: freightSchema,
  obstacles: obstaclesSchema,
  signals: signalsSchema,
}

// ─── Titles ───────────────────────────────────────────────────────────────────

const TITLES: Record<Tab, string> = {
  stations: 'Nouvelle gare',
  tracks: 'Nouvelle voie',
  passenger: 'Nouveau train voyageurs',
  freight: 'Nouveau train fret',
  obstacles: 'Nouvel obstacle',
  signals: 'Nouveau signal',
}

export function CreateObjectModal({ tab, onClose }: Props) {
  const { activeScenarioId, stations, trackSegments, loadScenarioData } = useAppStore()
  const [fields, setFields] = useState<Record<string, string | number | boolean>>({
    name: '',
    // station defaults
    lat: 48.85, lon: 2.3,
    // track defaults
    startLat: 48.85, startLon: 2.3, endLat: 48.86, endLon: 2.31,
    maxSpeedKmh: 120, trackCount: 1, electrification: 'NONE', gradePermil: 0,
    startStationId: '', endStationId: '',
    // train/obstacle/signal defaults
    modelCode: '',
    trackId: trackSegments[0]?.id ?? '',
    positionM: 0, direction: 'PAIR', initialSpeedKmh: 0,
    passengerCount: 0, serviceNumber: '',
    loadT: 0, cargoType: 'VIDE',
    type: 'CHANTIER', lengthM: 10, blocking: false,
    speedLimitKmh: 0, visibilityM: 100, appearAtS: 0, disappearAtS: '',
    initialState: 'VOIE_LIBRE',
  })
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})

  const set = (k: string, v: string | number | boolean) => {
    setFields(prev => {
      const next = { ...prev, [k]: v }
      // Si on change la gare de départ, on met à jour les coordonnées de départ
      if (k === 'startStationId') {
        const station = stations.find(s => s.id === v)
        if (station) {
          next.startLat = station.lat
          next.startLon = station.lon
        }
      }
      // Si on change la gare d'arrivée, on met à jour les coordonnées d'arrivée
      if (k === 'endStationId') {
        const station = stations.find(s => s.id === v)
        if (station) {
          next.endLat = station.lat
          next.endLon = station.lon
        }
      }
      return next
    })
    // Clear individual field error on change
    if (fieldErrors[k]) {
      setFieldErrors(prev => { const next = { ...prev }; delete next[k]; return next })
    }
  }

  const f = (k: string) => fields[k]

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!activeScenarioId) return

    // Zod validation
    const schema = SCHEMAS[tab]
    const result = schema.safeParse(fields)
    if (!result.success) {
      const errors: Record<string, string> = {}
      for (const issue of result.error.issues) {
        const key = issue.path[0] as string
        if (key && !errors[key]) {
          errors[key] = issue.message
        }
      }
      setFieldErrors(errors)
      return
    }
    setFieldErrors({})

    setSaving(true)
    setError(null)
    try {
      const trackId = f('trackId') as string
      switch (tab) {
        case 'stations':
          await api.stations.create(activeScenarioId, {
            name: f('name') as string,
            lat: Number(f('lat')),
            lon: Number(f('lon')),
          })
          break
        case 'tracks':
          await api.trackSegments.create(activeScenarioId, {
            name: f('name') as string,
            startLat: Number(f('startLat')), startLon: Number(f('startLon')),
            endLat: Number(f('endLat')), endLon: Number(f('endLon')),
            waypoints: [],
            maxSpeedKmh: Number(f('maxSpeedKmh')),
            trackCount: Number(f('trackCount')),
            electrification: f('electrification') as string,
            gradePermil: Number(f('gradePermil')),
            startStationId: (f('startStationId') as string) || null,
            endStationId: (f('endStationId') as string) || null,
          })
          break
        case 'passenger':
          await api.passengerTrains.create(activeScenarioId, {
            name: f('name') as string,
            modelCode: f('modelCode') as string,
            trackId,
            positionM: Number(f('positionM')),
            direction: f('direction') as 'PAIR' | 'IMPAIR',
            initialSpeedKmh: Number(f('initialSpeedKmh')),
            passengerCount: Number(f('passengerCount')),
            serviceNumber: (f('serviceNumber') as string) || null,
          })
          break
        case 'freight':
          await api.freightTrains.create(activeScenarioId, {
            name: f('name') as string,
            modelCode: f('modelCode') as string,
            trackId,
            positionM: Number(f('positionM')),
            direction: f('direction') as 'PAIR' | 'IMPAIR',
            initialSpeedKmh: Number(f('initialSpeedKmh')),
            loadT: Number(f('loadT')),
            cargoType: f('cargoType') as string,
          })
          break
        case 'obstacles':
          await api.obstacles.create(activeScenarioId, {
            name: f('name') as string,
            type: f('type') as string,
            trackId,
            positionM: Number(f('positionM')),
            lengthM: Number(f('lengthM')),
            blocking: Boolean(f('blocking')),
            speedLimitKmh: Number(f('speedLimitKmh')),
            visibilityM: Number(f('visibilityM')),
            appearAtS: Number(f('appearAtS')),
            disappearAtS: f('disappearAtS') !== '' ? Number(f('disappearAtS')) : null,
          })
          break
        case 'signals':
          await api.signals.create(activeScenarioId, {
            name: f('name') as string,
            type: f('type') as string,
            trackId,
            positionM: Number(f('positionM')),
            direction: f('direction') as 'PAIR' | 'IMPAIR',
            initialState: f('initialState') as string,
          })
          break
      }
      await loadScenarioData(activeScenarioId)
      onClose()
    } catch {
      setError('Erreur lors de la création')
      setSaving(false)
    }
  }

  // Helper to render a field error message
  const fieldError = (key: string) =>
    fieldErrors[key] ? <span className="field-error">{fieldErrors[key]}</span> : null

  const trackSelect = (
    <label>
      Voie *
      <select value={f('trackId') as string} onChange={e => set('trackId', e.target.value)} required>
        {trackSegments.length === 0
          ? <option value="">Aucune voie disponible</option>
          : trackSegments.map(t => <option key={t.id} value={t.id}>{t.name}</option>)
        }
      </select>
      {fieldError('trackId')}
    </label>
  )

  const directionSelect = (
    <label>
      Direction
      <select value={f('direction') as string} onChange={e => set('direction', e.target.value)}>
        <option value="PAIR">PAIR</option>
        <option value="IMPAIR">IMPAIR</option>
      </select>
    </label>
  )

  return (
    <Modal title={TITLES[tab]} onClose={onClose}>
      <form onSubmit={handleSubmit} className="modal-form">
        <label>
          Nom *
          <input autoFocus value={f('name') as string} onChange={e => set('name', e.target.value)} required />
          {fieldError('name')}
        </label>

        {tab === 'stations' && <>
          <div className="form-row">
            <label>Latitude<input type="number" step="any" value={f('lat') as number} onChange={e => set('lat', e.target.value)} />{fieldError('lat')}</label>
            <label>Longitude<input type="number" step="any" value={f('lon') as number} onChange={e => set('lon', e.target.value)} />{fieldError('lon')}</label>
          </div>
        </>}

        {tab === 'tracks' && <>
          <div className="form-row">
            <label>
              Lat. départ
              <input type="number" step="any" value={f('startLat') as number}
                onChange={e => set('startLat', e.target.value)}
                disabled={!!f('startStationId')} />
              {fieldError('startLat')}
            </label>
            <label>
              Lon. départ
              <input type="number" step="any" value={f('startLon') as number}
                onChange={e => set('startLon', e.target.value)}
                disabled={!!f('startStationId')} />
              {fieldError('startLon')}
            </label>
          </div>
          <div className="form-row">
            <label>
              Lat. arrivée
              <input type="number" step="any" value={f('endLat') as number}
                onChange={e => set('endLat', e.target.value)}
                disabled={!!f('endStationId')} />
              {fieldError('endLat')}
            </label>
            <label>
              Lon. arrivée
              <input type="number" step="any" value={f('endLon') as number}
                onChange={e => set('endLon', e.target.value)}
                disabled={!!f('endStationId')} />
              {fieldError('endLon')}
            </label>
          </div>
          <div className="form-row">
            <label>Vit. max (km/h)<input type="number" min={0} value={f('maxSpeedKmh') as number} onChange={e => set('maxSpeedKmh', e.target.value)} />{fieldError('maxSpeedKmh')}</label>
            <label>Nb voies<input type="number" min={1} value={f('trackCount') as number} onChange={e => set('trackCount', e.target.value)} />{fieldError('trackCount')}</label>
          </div>
          <div className="form-row">
            <label>Électrification
              <select value={f('electrification') as string} onChange={e => set('electrification', e.target.value)}>
                <option value="NONE">Aucune</option>
                <option value="AC_25KV">25 kV AC</option>
                <option value="AC_15KV">15 kV AC</option>
                <option value="DC_1500V">1500 V DC</option>
                <option value="DC_3000V">3000 V DC</option>
              </select>
            </label>
            <label>Pente (‰)<input type="number" step="any" value={f('gradePermil') as number} onChange={e => set('gradePermil', e.target.value)} /></label>
          </div>
          <div className="form-row">
            <label>Gare départ (optionnel)
              <select value={f('startStationId') as string} onChange={e => set('startStationId', e.target.value)}>
                <option value="">— Aucune —</option>
                {stations.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
              </select>
            </label>
            <label>Gare arrivée (optionnel)
              <select value={f('endStationId') as string} onChange={e => set('endStationId', e.target.value)}>
                <option value="">— Aucune —</option>
                {stations.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
              </select>
            </label>
          </div>
        </>}

        {(tab === 'passenger' || tab === 'freight') && <>
          {trackSelect}
          <div className="form-row">
            <label>Modèle<input value={f('modelCode') as string} onChange={e => set('modelCode', e.target.value)} placeholder="TGV, BB27000…" />{fieldError('modelCode')}</label>
            <label>Position (m)<input type="number" min={0} value={f('positionM') as number} onChange={e => set('positionM', e.target.value)} />{fieldError('positionM')}</label>
          </div>
          <div className="form-row">
            {directionSelect}
            <label>Vit. initiale (km/h)<input type="number" min={0} value={f('initialSpeedKmh') as number} onChange={e => set('initialSpeedKmh', e.target.value)} />{fieldError('initialSpeedKmh')}</label>
          </div>
          {tab === 'passenger' && <div className="form-row">
            <label>Passagers<input type="number" min={0} value={f('passengerCount') as number} onChange={e => set('passengerCount', e.target.value)} />{fieldError('passengerCount')}</label>
            <label>N° service<input value={f('serviceNumber') as string} onChange={e => set('serviceNumber', e.target.value)} placeholder="(optionnel)" /></label>
          </div>}
          {tab === 'freight' && <div className="form-row">
            <label>Charge (t)<input type="number" min={0} step="any" value={f('loadT') as number} onChange={e => set('loadT', e.target.value)} />{fieldError('loadT')}</label>
            <label>Type fret
              <select value={f('cargoType') as string} onChange={e => set('cargoType', e.target.value)}>
                <option value="VIDE">Vide</option>
                <option value="GENERAL">Général</option>
                <option value="VRAC">Vrac</option>
                <option value="CITERNE">Citerne</option>
                <option value="DANGEREUX">Dangereux</option>
              </select>
              {fieldError('cargoType')}
            </label>
          </div>}
        </>}

        {tab === 'obstacles' && <>
          {trackSelect}
          <div className="form-row">
            <label>Type
              <select value={f('type') as string} onChange={e => set('type', e.target.value)}>
                <option value="CHANTIER">Chantier</option>
                <option value="ANIMAL">Animal</option>
                <option value="VEHICULE">Véhicule</option>
                <option value="PERSONNE">Personne</option>
                <option value="EBOULEMENT">Éboulement</option>
                <option value="AUTRE">Autre</option>
              </select>
            </label>
            <label>Position (m)<input type="number" min={0} value={f('positionM') as number} onChange={e => set('positionM', e.target.value)} />{fieldError('positionM')}</label>
          </div>
          <div className="form-row">
            <label>Longueur (m)<input type="number" min={0} value={f('lengthM') as number} onChange={e => set('lengthM', e.target.value)} />{fieldError('lengthM')}</label>
            <label>Vit. limite (km/h)<input type="number" min={0} value={f('speedLimitKmh') as number} onChange={e => set('speedLimitKmh', e.target.value)} />{fieldError('speedLimitKmh')}</label>
          </div>
          <div className="form-row">
            <label>Visibilité (m)<input type="number" min={0} value={f('visibilityM') as number} onChange={e => set('visibilityM', e.target.value)} />{fieldError('visibilityM')}</label>
            <label style={{ flexDirection: 'row', alignItems: 'center', gap: 8 }}>
              Bloquant
              <input type="checkbox" checked={Boolean(f('blocking'))} onChange={e => set('blocking', e.target.checked)} style={{ width: 'auto' }} />
            </label>
          </div>
          <div className="form-row">
            <label>Apparaît (s)<input type="number" min={0} value={f('appearAtS') as number} onChange={e => set('appearAtS', e.target.value)} />{fieldError('appearAtS')}</label>
            <label>Disparaît (s)<input type="number" min={0} value={f('disappearAtS') as string} onChange={e => set('disappearAtS', e.target.value)} placeholder="(optionnel)" /></label>
          </div>
        </>}

        {tab === 'signals' && <>
          {trackSelect}
          <div className="form-row">
            <label>Type
              <select value={f('type') as string} onChange={e => set('type', e.target.value)}>
                <option value="CARRE">Carré</option>
                <option value="SEMAPHORE">Sémaphore</option>
                <option value="AVERTISSEMENT">Avertissement</option>
                <option value="GUIDON">Guidon</option>
                <option value="TGV_R">TGV-R</option>
              </select>
              {fieldError('type')}
            </label>
            <label>Position (m)<input type="number" min={0} value={f('positionM') as number} onChange={e => set('positionM', e.target.value)} />{fieldError('positionM')}</label>
          </div>
          <div className="form-row">
            <label>Direction
              <select value={f('direction') as string} onChange={e => set('direction', e.target.value)}>
                <option value="PAIR">PAIR</option>
                <option value="IMPAIR">IMPAIR</option>
                <option value="BIDIR">BIDIR</option>
              </select>
            </label>
            <label>État initial
              <select value={f('initialState') as string} onChange={e => set('initialState', e.target.value)}>
                <option value="VOIE_LIBRE">Voie libre</option>
                <option value="ARRET">Arrêt</option>
                <option value="AVERTISSEMENT">Avertissement</option>
              </select>
              {fieldError('initialState')}
            </label>
          </div>
        </>}

        {error && <p className="form-error">{error}</p>}
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={onClose} disabled={saving}>Annuler</button>
          <button type="submit" className="btn btn-primary" disabled={saving || !(f('name') as string).trim()}>
            {saving ? 'Création…' : 'Créer'}
          </button>
        </div>
      </form>
    </Modal>
  )
}
