import { useEffect, useRef } from 'react'
import maplibregl from 'maplibre-gl'
import 'maplibre-gl/dist/maplibre-gl.css'
import { useAppStore } from '../store'
import * as api from '../api'

const SVG_ICONS: Record<string, string> = {
  'icon-station': `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 36 36">
    <circle cx="18" cy="18" r="16" fill="#6366f1" stroke="white" stroke-width="2"/>
    <rect x="10" y="17" width="16" height="9" fill="white" rx="1.5"/>
    <polygon points="7,18 18,10 29,18" fill="white"/>
    <rect x="14" y="20" width="8" height="6" fill="#6366f1" rx="1"/>
    <rect x="10" y="24" width="3" height="2" fill="white" rx="1"/>
    <rect x="23" y="24" width="3" height="2" fill="white" rx="1"/>
  </svg>`,

  'icon-passenger-train': `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 36 36">
    <circle cx="18" cy="18" r="16" fill="#22c55e" stroke="white" stroke-width="2"/>
    <rect x="9" y="12" width="18" height="13" fill="white" rx="3"/>
    <rect x="11" y="14" width="5" height="4" fill="#22c55e" rx="1"/>
    <rect x="20" y="14" width="5" height="4" fill="#22c55e" rx="1"/>
    <rect x="9" y="21" width="18" height="3" fill="white" rx="1"/>
    <circle cx="13" cy="27" r="2.5" fill="white" stroke="#22c55e" stroke-width="1.5"/>
    <circle cx="23" cy="27" r="2.5" fill="white" stroke="#22c55e" stroke-width="1.5"/>
  </svg>`,

  'icon-freight-train': `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 36 36">
    <circle cx="18" cy="18" r="16" fill="#f59e0b" stroke="white" stroke-width="2"/>
    <rect x="9" y="12" width="18" height="12" fill="white" rx="2"/>
    <line x1="9" y1="18" x2="27" y2="18" stroke="#f59e0b" stroke-width="1.5"/>
    <line x1="18" y1="12" x2="18" y2="24" stroke="#f59e0b" stroke-width="1.5"/>
    <circle cx="13" cy="27" r="2.5" fill="white" stroke="#f59e0b" stroke-width="1.5"/>
    <circle cx="23" cy="27" r="2.5" fill="white" stroke="#f59e0b" stroke-width="1.5"/>
  </svg>`,

  'icon-obstacle': `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 36 36">
    <polygon points="18,3 34,31 2,31" fill="#ef4444" stroke="white" stroke-width="2"/>
    <rect x="16.5" y="13" width="3" height="10" fill="white" rx="1"/>
    <rect x="16.5" y="25" width="3" height="3" fill="white" rx="1"/>
  </svg>`,

  'icon-signal': `<svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 36 36">
    <rect x="4" y="3" width="28" height="30" rx="5" fill="#a855f7" stroke="white" stroke-width="2"/>
    <circle cx="18" cy="11" r="4" fill="#ff5555"/>
    <circle cx="18" cy="18" r="4" fill="#ffcc00"/>
    <circle cx="18" cy="25" r="4" fill="#44dd44"/>
  </svg>`,
}

async function loadIcons(map: maplibregl.Map): Promise<void> {
  await Promise.all(
    Object.entries(SVG_ICONS).map(([name, svg]) =>
      new Promise<void>((resolve) => {
        if (map.hasImage(name)) { resolve(); return }
        const img = new Image(36, 36)
        img.onload = () => { map.addImage(name, img); resolve() }
        img.onerror = () => resolve()  // ne jamais rejeter — continuer même si une icône échoue
        img.src = `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`
      })
    )
  )
}

export function ScenarioMap() {
  const mapContainer = useRef<HTMLDivElement>(null)
  const mapRef = useRef<maplibregl.Map | null>(null)
  const { activeScenarioId, selectedObjectId, selectedObjectType, selectObject } = useAppStore()
  const mapVersion = useAppStore(s => s.mapVersion)
  const stations = useAppStore(s => s.stations)
  const trackSegments = useAppStore(s => s.trackSegments)
  const passengerTrains = useAppStore(s => s.passengerTrains)
  const freightTrains = useAppStore(s => s.freightTrains)
  const obstacles = useAppStore(s => s.obstacles)
  const signals = useAppStore(s => s.signals)
  const currentTimeS = useAppStore(s => s.currentTimeS)

  // Init map
  useEffect(() => {
    if (!mapContainer.current) return

    const map = new maplibregl.Map({
      container: mapContainer.current,
      style: {
        version: 8,
        glyphs: 'https://demotiles.maplibre.org/font/{fontstack}/{range}.pbf',
        sources: {
          osm: {
            type: 'raster',
            tiles: ['https://tile.openstreetmap.org/{z}/{x}/{y}.png'],
            tileSize: 256,
            attribution: '© OpenStreetMap contributors',
          },
        },
        layers: [{ id: 'osm', type: 'raster', source: 'osm' }],
      },
      center: [2.3, 48.85],
      zoom: 10,
    })

    map.addControl(new maplibregl.NavigationControl(), 'top-right')
    mapRef.current = map

    map.on('load', () => { loadIcons(map) })

    return () => {
      map.remove()
      mapRef.current = null
    }
  }, [])

  // Load GeoJSON when scenario changes
  useEffect(() => {
    const map = mapRef.current
    if (!map || !activeScenarioId) return

    const loadGeoJson = async () => {
      try {
        const geojson = await api.scenarios.geojson(activeScenarioId)

        // Remove previous layers/sources
        const layerIds = [
          'tracks-line',
          'stations-point', 'stations-label',
          'trains-passenger-point', 'trains-freight-point',
          'obstacles-point', 'signals-point',
        ]
        layerIds.forEach(id => { if (map.getLayer(id)) map.removeLayer(id) })
        if (map.getSource('scenario')) map.removeSource('scenario')

        map.addSource('scenario', { type: 'geojson', data: geojson })

        // Track segments — lines
        map.addLayer({
          id: 'tracks-line',
          type: 'line',
          source: 'scenario',
          filter: ['==', ['get', 'objectType'], 'TRACK_SEGMENT'],
          paint: {
            'line-color': '#3b82f6',
            'line-width': 3,
          },
        })

        // Symbol layers for each point object type
        const pointTypes = [
          { id: 'stations-point',         type: 'STATION',         icon: 'icon-station',         label: true },
          { id: 'trains-passenger-point',  type: 'PASSENGER_TRAIN', icon: 'icon-passenger-train', label: false },
          { id: 'trains-freight-point',    type: 'FREIGHT_TRAIN',   icon: 'icon-freight-train',   label: false },
          { id: 'obstacles-point',         type: 'OBSTACLE',        icon: 'icon-obstacle',        label: false },
          { id: 'signals-point',           type: 'SIGNAL',          icon: 'icon-signal',          label: false },
        ]

        for (const pt of pointTypes) {
          map.addLayer({
            id: pt.id,
            type: 'symbol',
            source: 'scenario',
            filter: ['==', ['get', 'objectType'], pt.type],
            layout: {
              'icon-image': pt.icon,
              'icon-size': 0.85,
              'icon-allow-overlap': true,
              'icon-anchor': 'bottom',
              ...(pt.label ? {
                'text-field': ['get', 'name'],
                'text-offset': [0, 0.2],
                'text-anchor': 'top',
                'text-size': 11,
                'text-optional': true,
              } : {}),
            },
            paint: {
              ...(pt.label ? {
                'text-color': '#4f46e5',
                'text-halo-color': 'white',
                'text-halo-width': 2,
              } : {}),
            },
          })

          map.on('click', pt.id, (e) => {
            const feat = e.features?.[0]
            if (feat?.properties) {
              selectObject(feat.properties.id, feat.properties.objectType)
            }
          })

          map.on('mouseenter', pt.id, () => {
            map.getCanvas().style.cursor = 'pointer'
          })
          map.on('mouseleave', pt.id, () => {
            map.getCanvas().style.cursor = ''
          })
        }

        // Fit bounds
        const coords: [number, number][] = []
        geojson.features.forEach(f => {
          if (f.geometry.type === 'Point') {
            coords.push(f.geometry.coordinates as [number, number])
          } else if (f.geometry.type === 'LineString') {
            f.geometry.coordinates.forEach(c => coords.push(c as [number, number]))
          }
        })

        if (coords.length > 0) {
          const bounds = coords.reduce(
            (b, c) => b.extend(c),
            new maplibregl.LngLatBounds(coords[0], coords[0])
          )
          map.fitBounds(bounds, { padding: 40, maxZoom: 15 })
        }
      } catch {
        // Scenario may have no geodata yet
      }
    }

    if (map.loaded()) {
      loadGeoJson()
    } else {
      map.on('load', loadGeoJson)
    }
  }, [activeScenarioId, mapVersion])

  // Filtre temporel des obstacles selon currentTimeS
  useEffect(() => {
    const map = mapRef.current
    if (!map || !map.getLayer('obstacles-point')) return

    map.setFilter('obstacles-point', [
      'all',
      ['<=', ['get', 'appearAtS'], currentTimeS],
      ['any',
        ['!', ['has', 'disappearAtS']],
        ['==', ['get', 'disappearAtS'], null],
        ['>', ['get', 'disappearAtS'], currentTimeS],
      ],
    ])
  }, [currentTimeS])

  // Fly to selected object — computed from store data (always in sync)
  useEffect(() => {
    const map = mapRef.current
    if (!map || !selectedObjectId || !selectedObjectType) return

    const flyToTrack = (trackId: string) => {
      const track = trackSegments.find(t => t.id === trackId)
      if (!track) return
      const coords: [number, number][] = [
        [track.startLon, track.startLat],
        ...track.waypoints.map(([lat, lon]) => [lon, lat] as [number, number]),
        [track.endLon, track.endLat],
      ]
      const bounds = coords.reduce(
        (b, c) => b.extend(c),
        new maplibregl.LngLatBounds(coords[0], coords[0])
      )
      map.fitBounds(bounds, { padding: 60, maxZoom: 15, duration: 800 })
    }

    switch (selectedObjectType) {
      case 'STATION': {
        const s = stations.find(s => s.id === selectedObjectId)
        if (s) map.flyTo({ center: [s.lon, s.lat], zoom: 14, duration: 800 })
        break
      }
      case 'TRACK_SEGMENT': {
        flyToTrack(selectedObjectId)
        break
      }
      case 'PASSENGER_TRAIN': {
        const t = passengerTrains.find(t => t.id === selectedObjectId)
        if (t) flyToTrack(t.trackId)
        break
      }
      case 'FREIGHT_TRAIN': {
        const t = freightTrains.find(t => t.id === selectedObjectId)
        if (t) flyToTrack(t.trackId)
        break
      }
      case 'OBSTACLE': {
        const o = obstacles.find(o => o.id === selectedObjectId)
        if (o) flyToTrack(o.trackId)
        break
      }
      case 'SIGNAL': {
        const s = signals.find(s => s.id === selectedObjectId)
        if (s) flyToTrack(s.trackId)
        break
      }
    }
  }, [selectedObjectId, selectedObjectType, stations, trackSegments, passengerTrains, freightTrains, obstacles, signals])

  return <div ref={mapContainer} className="map-container" />
}
