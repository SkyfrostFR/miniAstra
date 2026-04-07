import { useState } from 'react'
import { useAppStore } from '../store'
import * as api from '../api'
import { CreateScenarioModal } from './CreateScenarioModal'

export function Toolbar() {
  const { scenarios, activeScenarioId, setActiveScenario } = useAppStore()
  const [showCreate, setShowCreate] = useState(false)

  const handleExport = async () => {
    if (!activeScenarioId) return
    try {
      const blob = await api.scenarios.export(activeScenarioId)
      const scenario = scenarios.find(s => s.id === activeScenarioId)
      const name = scenario?.name ?? 'scenario'
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `${name}-${new Date().toISOString().slice(0, 10)}.xlsx`
      a.click()
      URL.revokeObjectURL(url)
    } catch {
      alert('Erreur lors de l\'export Excel')
    }
  }

  return (
    <header className="toolbar">
      <div className="toolbar-left">
        <span className="app-title">miniAstra</span>

        <select
          className="scenario-select"
          value={activeScenarioId ?? ''}
          onChange={e => setActiveScenario(e.target.value)}
        >
          <option value="" disabled>Sélectionner un scénario</option>
          {scenarios.map(s => (
            <option key={s.id} value={s.id}>{s.name}</option>
          ))}
        </select>

        <button
          className="btn btn-icon"
          onClick={() => setShowCreate(true)}
          title="Nouveau scénario"
        >+</button>
      </div>

      {showCreate && <CreateScenarioModal onClose={() => setShowCreate(false)} />}

      <div className="toolbar-right">
        <button
          className="btn btn-secondary"
          onClick={handleExport}
          disabled={!activeScenarioId}
          title="Exporter en Excel"
        >
          Exporter .xlsx
        </button>
      </div>
    </header>
  )
}
