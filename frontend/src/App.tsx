import { useEffect, useRef, useCallback, useState } from 'react'
import { Toolbar } from './components/Toolbar'
import { ScenarioMap } from './components/ScenarioMap'
import { DataGrid } from './components/DataGrid'
import { StatusBar } from './components/StatusBar'
import { ValidationPanel } from './components/ValidationPanel'
import { TimeSlider } from './components/TimeSlider'
import { useAppStore } from './store'

export default function App() {
  const { loadScenarios, splitRatio, setSplitRatio, activeScenarioId } = useAppStore()
  const [showValidation, setShowValidation] = useState(false)
  const dragging = useRef(false)
  const containerRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    loadScenarios()
  }, [loadScenarios])

  const onMouseDown = useCallback(() => {
    dragging.current = true
    document.body.style.cursor = 'row-resize'
    document.body.style.userSelect = 'none'
  }, [])

  const onMouseMove = useCallback((e: MouseEvent) => {
    if (!dragging.current || !containerRef.current) return
    const rect = containerRef.current.getBoundingClientRect()
    const ratio = ((e.clientY - rect.top) / rect.height) * 100
    setSplitRatio(Math.min(80, Math.max(20, ratio)))
  }, [setSplitRatio])

  const onMouseUp = useCallback(() => {
    dragging.current = false
    document.body.style.cursor = ''
    document.body.style.userSelect = ''
  }, [])

  useEffect(() => {
    window.addEventListener('mousemove', onMouseMove)
    window.addEventListener('mouseup', onMouseUp)
    return () => {
      window.removeEventListener('mousemove', onMouseMove)
      window.removeEventListener('mouseup', onMouseUp)
    }
  }, [onMouseMove, onMouseUp])

  return (
    <div className="app-root">
      <Toolbar />
      <div className="app-body">
        <div className="main-content" ref={containerRef}>
          <div className="map-panel" style={{ height: `${splitRatio}%` }}>
            <ScenarioMap />
            {activeScenarioId && (
              <button
                className="btn btn-secondary validation-toggle-btn"
                onClick={() => setShowValidation(v => !v)}
                aria-pressed={showValidation}
                title="Ouvrir le panneau de validation"
              >
                Validation
              </button>
            )}
          </div>
          <TimeSlider />
          <div className="splitter" onMouseDown={onMouseDown} />
          <div className="grid-panel" style={{ height: `${100 - splitRatio}%` }}>
            <DataGrid />
          </div>
        </div>

        {showValidation && activeScenarioId && (
          <ValidationPanel
            scenarioId={activeScenarioId}
            onClose={() => setShowValidation(false)}
          />
        )}
      </div>
      <StatusBar />
    </div>
  )
}
