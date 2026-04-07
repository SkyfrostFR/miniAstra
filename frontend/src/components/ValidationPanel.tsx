import { useState, useEffect, useCallback } from 'react'
import { validation, type ValidationAnomaly, type ValidationReport } from '../api'
import { useAppStore } from '../store'

interface Props {
  scenarioId: string
  onClose: () => void
}

export function ValidationPanel({ scenarioId, onClose }: Props) {
  const { selectObject } = useAppStore()
  const [report, setReport] = useState<ValidationReport | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const fetchReport = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await validation.report(scenarioId)
      setReport(data)
    } catch {
      setError('Impossible de charger le rapport de validation')
    } finally {
      setLoading(false)
    }
  }, [scenarioId])

  useEffect(() => {
    fetchReport()
  }, [fetchReport])

  const handleAnomalyClick = (anomaly: ValidationAnomaly) => {
    selectObject(anomaly.objectId, anomaly.objectType)
  }

  return (
    <div className="validation-panel" role="complementary" aria-label="Panneau de validation">
      <div className="validation-panel__header">
        <div className="validation-panel__title">
          <span>Validation</span>
          {report && (
            <span
              className={`validation-panel__badge ${report.anomalyCount === 0 ? 'validation-panel__badge--ok' : 'validation-panel__badge--warn'}`}
              aria-label={`${report.anomalyCount} anomalie(s)`}
            >
              {report.anomalyCount}
            </span>
          )}
        </div>
        <div className="validation-panel__actions">
          <button
            className="btn btn-secondary btn-sm"
            onClick={fetchReport}
            disabled={loading}
            aria-label="Actualiser le rapport"
          >
            {loading ? '…' : 'Actualiser'}
          </button>
          <button
            className="validation-panel__close"
            onClick={onClose}
            aria-label="Fermer le panneau de validation"
          >
            ✕
          </button>
        </div>
      </div>

      <div className="validation-panel__body">
        {loading && <p className="validation-panel__status">Chargement…</p>}

        {!loading && error && (
          <p className="validation-panel__status validation-panel__status--error">{error}</p>
        )}

        {!loading && !error && report && report.anomalyCount === 0 && (
          <p className="validation-panel__status validation-panel__status--ok">
            Aucune anomalie détectée ✓
          </p>
        )}

        {!loading && !error && report && report.anomalyCount > 0 && (
          <ul className="validation-panel__list" role="list">
            {report.anomalies.map((anomaly, index) => (
              <li
                key={`${anomaly.objectId}-${anomaly.rule}-${index}`}
                className="validation-panel__item"
                role="button"
                tabIndex={0}
                onClick={() => handleAnomalyClick(anomaly)}
                onKeyDown={e => e.key === 'Enter' && handleAnomalyClick(anomaly)}
                aria-label={`Anomalie : ${anomaly.objectName} — ${anomaly.description}`}
              >
                <div className="validation-panel__item-header">
                  <span className="validation-panel__object-type">{anomaly.objectType}</span>
                  <span className="validation-panel__object-name">{anomaly.objectName}</span>
                </div>
                <div className="validation-panel__rule">{anomaly.rule}</div>
                <div className="validation-panel__description">{anomaly.description}</div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  )
}
