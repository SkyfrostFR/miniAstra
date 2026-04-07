import { useAppStore } from '../store'

function formatSeconds(totalSeconds: number): string {
  const h = Math.floor(totalSeconds / 3600)
  const m = Math.floor((totalSeconds % 3600) / 60)
  const s = totalSeconds % 60
  return [h, m, s].map(n => String(n).padStart(2, '0')).join(':')
}

export function TimeSlider() {
  const activeScenarioId = useAppStore(s => s.activeScenarioId)
  const scenarios = useAppStore(s => s.scenarios)
  const currentTimeS = useAppStore(s => s.currentTimeS)
  const setCurrentTimeS = useAppStore(s => s.setCurrentTimeS)

  if (!activeScenarioId) return null

  const scenario = scenarios.find(sc => sc.id === activeScenarioId)
  const durationS = scenario?.durationS ?? 0

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setCurrentTimeS(Number(e.target.value))
  }

  return (
    <div className="time-slider">
      <label className="time-slider__label" htmlFor="time-slider-input">
        T
      </label>
      <input
        id="time-slider-input"
        className="time-slider__range"
        type="range"
        min={0}
        max={durationS}
        step={1}
        value={currentTimeS}
        onChange={handleChange}
      />
      <span className="time-slider__display">{formatSeconds(currentTimeS)}</span>
    </div>
  )
}
