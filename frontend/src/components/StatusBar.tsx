import { useAppStore } from '../store'

export function StatusBar() {
  const { stats, activeScenarioId, loading, error } = useAppStore()

  if (error) {
    return (
      <footer className="status-bar status-error">
        {error}
      </footer>
    )
  }

  if (loading) {
    return <footer className="status-bar">Chargement...</footer>
  }

  if (!activeScenarioId || !stats) {
    return <footer className="status-bar">Aucun scénario sélectionné</footer>
  }

  return (
    <footer className="status-bar">
      <span className="stat-item">Voies: <strong>{stats.trackSegments}</strong></span>
      <span className="stat-sep">|</span>
      <span className="stat-item">Trains voy.: <strong>{stats.passengerTrains}</strong></span>
      <span className="stat-sep">|</span>
      <span className="stat-item">Trains fret: <strong>{stats.freightTrains}</strong></span>
      <span className="stat-sep">|</span>
      <span className="stat-item">Obstacles: <strong>{stats.obstacles}</strong></span>
      <span className="stat-sep">|</span>
      <span className="stat-item">Signaux: <strong>{stats.signals}</strong></span>
    </footer>
  )
}
