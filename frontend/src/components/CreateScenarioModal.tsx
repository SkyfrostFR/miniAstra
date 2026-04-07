import { useState } from 'react'
import { Modal } from './Modal'
import * as api from '../api'
import { useAppStore } from '../store'

interface Props {
  onClose: () => void
}

export function CreateScenarioModal({ onClose }: Props) {
  const { loadScenarios, setActiveScenario } = useAppStore()
  const [name, setName] = useState('')
  const [description, setDescription] = useState('')
  const [durationS, setDurationS] = useState(3600)
  const [startTime, setStartTime] = useState('08:00:00')
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!name.trim()) return
    setSaving(true)
    setError(null)
    try {
      const created = await api.scenarios.create({ name: name.trim(), description: description.trim() || null, durationS, startTime })
      await loadScenarios()
      await setActiveScenario(created.id)
      onClose()
    } catch {
      setError('Erreur lors de la création du scénario')
      setSaving(false)
    }
  }

  return (
    <Modal title="Nouveau scénario" onClose={onClose}>
      <form onSubmit={handleSubmit} className="modal-form">
        <label>
          Nom *
          <input autoFocus value={name} onChange={e => setName(e.target.value)} placeholder="Mon scénario" required />
        </label>
        <label>
          Description
          <textarea value={description} onChange={e => setDescription(e.target.value)} rows={2} placeholder="(optionnel)" />
        </label>
        <div className="form-row">
          <label>
            Durée (s)
            <input type="number" min={1} value={durationS} onChange={e => setDurationS(Number(e.target.value))} />
          </label>
          <label>
            Heure de départ
            <input type="text" value={startTime} onChange={e => setStartTime(e.target.value)} placeholder="08:00:00" />
          </label>
        </div>
        {error && <p className="form-error">{error}</p>}
        <div className="form-actions">
          <button type="button" className="btn btn-secondary" onClick={onClose} disabled={saving}>Annuler</button>
          <button type="submit" className="btn btn-primary" disabled={saving || !name.trim()}>
            {saving ? 'Création…' : 'Créer'}
          </button>
        </div>
      </form>
    </Modal>
  )
}
