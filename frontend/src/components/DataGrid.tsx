import { useMemo, useState, useCallback } from 'react'
import { CreateObjectModal } from './CreateObjectModal'
import { AgGridReact } from '@ag-grid-community/react'
import { ClientSideRowModelModule } from '@ag-grid-community/client-side-row-model'
import type { ColDef, GridReadyEvent, RowClickedEvent, CellValueChangedEvent, ICellRendererParams } from '@ag-grid-community/core'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import { useAppStore } from '../store'
import * as api from '../api'

const TABS = [
  { key: 'stations', label: 'Gares' },
  { key: 'tracks', label: 'Voies' },
  { key: 'passenger', label: 'Trains voy.' },
  { key: 'freight', label: 'Trains fret' },
  { key: 'obstacles', label: 'Obstacles' },
  { key: 'signals', label: 'Signaux' },
] as const

// Cellule renderer pour le bouton de suppression
function DeleteCellRenderer(params: ICellRendererParams & { onDelete: (id: string) => void }) {
  return (
    <button
      className="btn-delete-row"
      title="Supprimer"
      onClick={(e) => {
        e.stopPropagation()
        params.onDelete(params.data.id)
      }}
    >
      ✕
    </button>
  )
}

export function DataGrid() {
  const {
    activeTab, setActiveTab,
    stations, trackSegments, passengerTrains, freightTrains, obstacles, signals,
    selectObject, activeScenarioId,
    loadScenarioData, refreshStats,
  } = useAppStore()
  const [showCreate, setShowCreate] = useState(false)

  // Suppression d'un objet selon l'onglet actif
  const handleDelete = useCallback(async (id: string) => {
    if (!activeScenarioId) return
    try {
      switch (activeTab) {
        case 'stations':  await api.stations.delete(activeScenarioId, id); break
        case 'tracks':    await api.trackSegments.delete(activeScenarioId, id); break
        case 'passenger': await api.passengerTrains.delete(activeScenarioId, id); break
        case 'freight':   await api.freightTrains.delete(activeScenarioId, id); break
        case 'obstacles': await api.obstacles.delete(activeScenarioId, id); break
        case 'signals':   await api.signals.delete(activeScenarioId, id); break
      }
      await loadScenarioData(activeScenarioId)
      await refreshStats()
    } catch (err) {
      console.error('Erreur lors de la suppression', err)
    }
  }, [activeTab, activeScenarioId, loadScenarioData, refreshStats])

  // Sauvegarde automatique sur modification inline
  const onCellValueChanged = useCallback(async (params: CellValueChangedEvent) => {
    if (!activeScenarioId) return
    const { id } = params.data
    const field = params.colDef.field as string
    const patch = { ...params.data, [field]: params.newValue }
    try {
      switch (activeTab) {
        case 'stations':  await api.stations.update(activeScenarioId, id, patch); break
        case 'tracks':    await api.trackSegments.update(activeScenarioId, id, patch); break
        case 'passenger': await api.passengerTrains.update(activeScenarioId, id, patch); break
        case 'freight':   await api.freightTrains.update(activeScenarioId, id, patch); break
        case 'obstacles': await api.obstacles.update(activeScenarioId, id, patch); break
        case 'signals':   await api.signals.update(activeScenarioId, id, patch); break
      }
      await refreshStats()
    } catch (err) {
      console.error('Erreur lors de la sauvegarde', err)
      // Remettre l'ancienne valeur en cas d'échec
      params.node.setDataValue(field, params.oldValue)
    }
  }, [activeTab, activeScenarioId, refreshStats])

  // Colonne Actions (suppression) — non éditable, largeur fixe
  const actionsColDef: ColDef = useMemo(() => ({
    headerName: '',
    field: 'id',
    width: 48,
    minWidth: 48,
    maxWidth: 48,
    sortable: false,
    filter: false,
    resizable: false,
    suppressMovable: true,
    editable: false,
    cellRenderer: DeleteCellRenderer,
    cellRendererParams: { onDelete: handleDelete },
  }), [handleDelete])

  const { rowData, colDefs } = useMemo(() => {
    switch (activeTab) {
      case 'stations':
        return {
          rowData: stations,
          colDefs: [
            { field: 'name', headerName: 'Nom', flex: 2, editable: true },
            { field: 'lat', headerName: 'Latitude', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'lon', headerName: 'Longitude', flex: 1, editable: true, type: 'numericColumn' },
            actionsColDef,
          ] satisfies ColDef[],
        }
      case 'tracks':
        return {
          rowData: trackSegments,
          colDefs: [
            { field: 'name', headerName: 'Nom', flex: 2, editable: true },
            { field: 'lengthM', headerName: 'Longueur (m)', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'maxSpeedKmh', headerName: 'Vit. max (km/h)', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'electrification', headerName: 'Élecr.', flex: 1, editable: true },
            { field: 'trackCount', headerName: 'Voies', flex: 1, editable: true, type: 'numericColumn' },
            actionsColDef,
          ] satisfies ColDef[],
        }
      case 'passenger':
        return {
          rowData: passengerTrains,
          colDefs: [
            { field: 'name', headerName: 'Nom', flex: 2, editable: true },
            { field: 'modelCode', headerName: 'Modèle', flex: 1, editable: true },
            { field: 'positionM', headerName: 'Position (m)', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'direction', headerName: 'Direction', flex: 1, editable: true },
            { field: 'passengerCount', headerName: 'Passagers', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'serviceNumber', headerName: 'N° service', flex: 1, editable: true },
            actionsColDef,
          ] satisfies ColDef[],
        }
      case 'freight':
        return {
          rowData: freightTrains,
          colDefs: [
            { field: 'name', headerName: 'Nom', flex: 2, editable: true },
            { field: 'modelCode', headerName: 'Modèle', flex: 1, editable: true },
            { field: 'positionM', headerName: 'Position (m)', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'loadT', headerName: 'Charge (t)', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'cargoType', headerName: 'Type fret', flex: 1, editable: true },
            actionsColDef,
          ] satisfies ColDef[],
        }
      case 'obstacles':
        return {
          rowData: obstacles,
          colDefs: [
            { field: 'name', headerName: 'Nom', flex: 2, editable: true },
            { field: 'type', headerName: 'Type', flex: 1, editable: true },
            { field: 'positionM', headerName: 'Position (m)', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'blocking', headerName: 'Bloquant', flex: 1, editable: true },
            { field: 'speedLimitKmh', headerName: 'Vit. limite', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'appearAtS', headerName: 'Apparaît (s)', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'disappearAtS', headerName: 'Disparaît (s)', flex: 1, editable: true, type: 'numericColumn' },
            actionsColDef,
          ] satisfies ColDef[],
        }
      case 'signals':
        return {
          rowData: signals,
          colDefs: [
            { field: 'name', headerName: 'Nom', flex: 2, editable: true },
            { field: 'type', headerName: 'Type', flex: 1, editable: true },
            { field: 'positionM', headerName: 'Position (m)', flex: 1, editable: true, type: 'numericColumn' },
            { field: 'direction', headerName: 'Direction', flex: 1, editable: true },
            { field: 'initialState', headerName: 'État initial', flex: 1, editable: true },
            actionsColDef,
          ] satisfies ColDef[],
        }
    }
  }, [activeTab, stations, trackSegments, passengerTrains, freightTrains, obstacles, signals, actionsColDef])

  const defaultColDef: ColDef = useMemo(() => ({
    sortable: true,
    resizable: true,
    filter: true,
  }), [])

  const onRowClicked = (e: RowClickedEvent) => {
    if (e.data?.id) {
      const typeMap: Record<typeof activeTab, string> = {
        stations: 'STATION',
        tracks: 'TRACK_SEGMENT',
        passenger: 'PASSENGER_TRAIN',
        freight: 'FREIGHT_TRAIN',
        obstacles: 'OBSTACLE',
        signals: 'SIGNAL',
      }
      selectObject(e.data.id, typeMap[activeTab])
    }
  }

  return (
    <div className="datagrid-panel">
      <div className="tabs">
        {TABS.map(t => (
          <button
            key={t.key}
            className={`tab-btn ${activeTab === t.key ? 'active' : ''}`}
            onClick={() => setActiveTab(t.key)}
          >
            {t.label}
          </button>
        ))}

        <div className="tab-filter">
          <input
            type="text"
            placeholder="Filtrer..."
            className="filter-input"
            id="grid-quick-filter"
          />
        </div>

        <button
          className="btn btn-icon"
          onClick={() => setShowCreate(true)}
          disabled={!activeScenarioId}
          title="Ajouter un élément"
        >+</button>
      </div>

      {showCreate && <CreateObjectModal tab={activeTab} onClose={() => setShowCreate(false)} />}

      <div className="ag-theme-alpine grid-area">
        <AgGridReact
          modules={[ClientSideRowModelModule]}
          rowData={rowData}
          columnDefs={colDefs}
          defaultColDef={defaultColDef}
          rowSelection="single"
          onRowClicked={onRowClicked}
          onCellValueChanged={onCellValueChanged}
          onGridReady={(e: GridReadyEvent) => {
            const input = document.getElementById('grid-quick-filter') as HTMLInputElement | null
            if (input) {
              input.addEventListener('input', () => {
                e.api.setGridOption('quickFilterText', input.value)
              })
            }
          }}
          getRowId={p => p.data.id}
          animateRows={false}
        />
      </div>
    </div>
  )
}
