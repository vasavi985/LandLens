import React, { useEffect, useRef, useState, useCallback } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { mapService } from '../../services/map.service';
import type { Property } from '../../models/property.models';
import { parseBoundaryFromDescription } from '../../utils/boundary';
import { MapPin, Layers, RefreshCw, Trash2 } from 'lucide-react';

// Fix Leaflet marker icon asset paths in Vite
delete (L.Icon.Default.prototype as any)._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
});

interface LocationSelectData {
  lat: number;
  lng: number;
  address: string;
  village: string;
  district: string;
  state: string;
  pincode: string;
  area?: number;
  boundary?: [number, number][];
}

interface MapProps {
  onLocationSelected?: (data: LocationSelectData) => void;
  onScheduleVisit?: (property: Property) => void;
  center?: [number, number];
  zoom?: number;
  mode?: 'picker' | 'view' | 'detail';
  properties?: Property[];
  className?: string;
  initialBoundary?: [number, number][];
  pickerLat?: number;
  pickerLng?: number;
}

// Calculate polygon area in acres using Shoelace formula on projected coordinates
const calculatePolygonArea = (coordinates: [number, number][]): number => {
  if (coordinates.length < 3) return 0;

  const R = 6378137; // Earth's radius in meters
  let area = 0;

  for (let i = 0; i < coordinates.length; i++) {
    const j = (i + 1) % coordinates.length;
    const p1 = coordinates[i];
    const p2 = coordinates[j];

    const lat1 = (p1[1] * Math.PI) / 180;
    const lat2 = (p2[1] * Math.PI) / 180;
    const lng1 = (p1[0] * Math.PI) / 180;
    const lng2 = (p2[0] * Math.PI) / 180;

    area += (lng2 - lng1) * (2 + Math.sin(lat1) + Math.sin(lat2));
  }

  area = (Math.abs(area) * R * R) / 2;
  const areaInAcres = area / 4046.86;
  return Number(areaInAcres.toFixed(2));
};

const calculateCentroid = (pts: [number, number][]): [number, number] => {
  if (!pts || pts.length === 0) return [0, 0];
  let sumLng = 0, sumLat = 0;
  pts.forEach(p => { sumLng += p[0]; sumLat += p[1]; });
  return [sumLng / pts.length, sumLat / pts.length];
};

export const Map: React.FC<MapProps> = ({
  onLocationSelected,
  onScheduleVisit,
  center = [80.4365, 16.3067],
  zoom = 10,
  mode = 'picker',
  properties = [],
  className = '',
  initialBoundary,
  pickerLat: propPickerLat,
  pickerLng: propPickerLng
}) => {
  const mapContainer = useRef<HTMLDivElement>(null);
  const mapRef = useRef<L.Map | null>(null);
  const pickerMarkerRef = useRef<L.Marker | null>(null);
  const boundaryMarkersRef = useRef<L.Marker[]>([]);
  const propertyMarkersRef = useRef<L.Marker[]>([]);
  const polylineRef = useRef<L.Polyline | null>(null);
  const polygonRef = useRef<L.Polygon | null>(null);
  const approvedPolygonsRef = useRef<L.Polygon[]>([]);

  const boundaryPointsRef = useRef<[number, number][]>([]);
  const [pointCount, setPointCount] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [drawMode, setDrawMode] = useState<'pin' | 'draw'>('pin');
  const [mapError, setMapError] = useState<string | null>(null);

  const drawModeRef = useRef(drawMode);
  useEffect(() => {
    drawModeRef.current = drawMode;
  }, [drawMode]);

  const onLocationSelectedRef = useRef(onLocationSelected);
  const onScheduleVisitRef = useRef(onScheduleVisit);

  useEffect(() => {
    onLocationSelectedRef.current = onLocationSelected;
    onScheduleVisitRef.current = onScheduleVisit;
  }, [onLocationSelected, onScheduleVisit]);

  const pickerLng = propPickerLng ?? center[0];
  const pickerLat = propPickerLat ?? center[1];

  // Watch picker coordinate changes from parent
  useEffect(() => {
    const map = mapRef.current;
    if (!map || mode !== 'picker') return;

    if (pickerMarkerRef.current) {
      pickerMarkerRef.current.setLatLng([pickerLat, pickerLng]);
      const timer = setTimeout(() => {
        if (mapRef.current) {
          mapRef.current.setView([pickerLat, pickerLng]);
        }
      }, 50);
      return () => clearTimeout(timer);
    }
  }, [pickerLat, pickerLng, mode]);

  const drawPolygon = useCallback(() => {
    const map = mapRef.current;
    if (!map) return;

    const points = boundaryPointsRef.current;
    if (points.length < 2) {
      if (polylineRef.current) { polylineRef.current.remove(); polylineRef.current = null; }
      if (polygonRef.current) { polygonRef.current.remove(); polygonRef.current = null; }
      return;
    }

    const latlngs = points.map(pt => [pt[1], pt[0]] as [number, number]);
    const closedCoords = [...latlngs, latlngs[0]];

    try {
      if (polylineRef.current) {
        polylineRef.current.setLatLngs(closedCoords);
      } else {
        polylineRef.current = L.polyline(closedCoords, { color: '#10b981', weight: 4 }).addTo(map);
      }

      if (points.length >= 3) {
        if (polygonRef.current) {
          polygonRef.current.setLatLngs([latlngs]);
        } else {
          polygonRef.current = L.polygon(latlngs, {
            color: '#10b981',
            weight: 0,
            fillColor: '#10b981',
            fillOpacity: 0.22
          }).addTo(map);
        }
      } else {
        if (polygonRef.current) { polygonRef.current.remove(); polygonRef.current = null; }
      }
    } catch (e) {
      console.error(e);
    }
  }, []);

  const handleReverseGeocode = useCallback(async (lng: number, lat: number, currentBoundary: [number, number][]) => {
    try {
      const details = await mapService.reverseGeocode(lng, lat);
      const calculatedArea = currentBoundary.length >= 3 ? calculatePolygonArea(currentBoundary) : undefined;
      if (onLocationSelectedRef.current) {
        onLocationSelectedRef.current({
          lat: Number(lat.toFixed(6)),
          lng: Number(lng.toFixed(6)),
          address: details.address || '',
          village: details.village || '',
          district: details.district || '',
          state: details.state || '',
          pincode: details.pincode || '',
          area: calculatedArea,
          boundary: currentBoundary.length > 0 ? [...currentBoundary] : undefined
        });
      }
    } catch {
      if (onLocationSelectedRef.current) {
        onLocationSelectedRef.current({
          lat: Number(lat.toFixed(6)),
          lng: Number(lng.toFixed(6)),
          address: '', village: '', district: '', state: '', pincode: '',
          boundary: currentBoundary.length > 0 ? [...currentBoundary] : undefined
        });
      }
    }
  }, []);

  const addBoundaryMarker = useCallback((lng: number, lat: number, index: number) => {
    const map = mapRef.current;
    if (!map) return;

    const icon = L.divIcon({
      html: '<div class="w-3.5 h-3.5 bg-blue-500 border-2 border-white rounded-full shadow-lg cursor-pointer"></div>',
      className: 'custom-boundary-marker',
      iconSize: [14, 14],
      iconAnchor: [7, 7]
    });

    const marker = L.marker([lat, lng], { icon, draggable: true }).addTo(map);

    marker.on('drag', (e: any) => {
      const pos = e.target.getLatLng();
      boundaryPointsRef.current[index] = [pos.lng, pos.lat];
      drawPolygon();
    });

    marker.on('dragend', () => {
      const centroid = calculateCentroid(boundaryPointsRef.current);
      handleReverseGeocode(centroid[0], centroid[1], boundaryPointsRef.current);
    });

    boundaryMarkersRef.current.push(marker);
    setPointCount(boundaryPointsRef.current.length);
  }, [drawPolygon, handleReverseGeocode]);

  const clearBoundary = useCallback((emitEvent = false) => {
    boundaryPointsRef.current = [];
    setPointCount(0);
    boundaryMarkersRef.current.forEach(m => m.remove());
    boundaryMarkersRef.current = [];

    if (polylineRef.current) { polylineRef.current.remove(); polylineRef.current = null; }
    if (polygonRef.current) { polygonRef.current.remove(); polygonRef.current = null; }

    if (emitEvent) {
      const lat = pickerMarkerRef.current ? pickerMarkerRef.current.getLatLng().lat : pickerLat;
      const lng = pickerMarkerRef.current ? pickerMarkerRef.current.getLatLng().lng : pickerLng;
      handleReverseGeocode(lng, lat, []);
    }
  }, [pickerLat, pickerLng, handleReverseGeocode]);

  const loadBoundary = useCallback((boundary: [number, number][]) => {
    clearBoundary();
    if (!boundary || boundary.length === 0) return;

    boundaryPointsRef.current = [...boundary];
    setPointCount(boundary.length);
    boundary.forEach((pt, idx) => addBoundaryMarker(pt[0], pt[1], idx));

    const map = mapRef.current;
    if (map) {
      const cent = calculateCentroid(boundary);
      map.setView([cent[1], cent[0]]);
      if (pickerMarkerRef.current) pickerMarkerRef.current.setLatLng([cent[1], cent[0]]);
      drawPolygon();
    }
  }, [clearBoundary, addBoundaryMarker, drawPolygon]);

  // Render ONLY Approved Lands Green Outline
  const renderCategorizedLandPolygons = useCallback((map: L.Map, propsList: Property[]) => {
    approvedPolygonsRef.current.forEach(p => p.remove());
    approvedPolygonsRef.current = [];

    if (!propsList || propsList.length === 0) return;

    propsList.forEach(p => {
      // ONLY draw outline for APPROVED lands!
      if (p.status !== 'APPROVED') return;

      let coords: [number, number][] | null = parseBoundaryFromDescription(p.description || '');
      if (!coords || coords.length < 3) {
        if (p.latitude && p.longitude) {
          const areaSqMeters = (p.area || 1) * 4046.86;
          const r = Math.max(120, Math.sqrt(areaSqMeters / Math.PI));
          const R = 6378137;
          const pts: [number, number][] = [];
          for (let i = 0; i < 6; i++) {
            const angle = (i * 60) * Math.PI / 180;
            const dLat = (r * Math.sin(angle)) / R * 180 / Math.PI;
            const dLng = (r * Math.cos(angle)) / (R * Math.cos(p.latitude * Math.PI / 180)) * 180 / Math.PI;
            pts.push([p.longitude + dLng, p.latitude + dLat]);
          }
          coords = pts;
        }
      }

      if (coords && coords.length > 2) {
        // Map to [lat, lng]
        const latlngs = coords.map(pt => [pt[1], pt[0]] as [number, number]);
        const poly = L.polygon(latlngs, {
          color: '#047857',
          fillColor: '#10b981',
          fillOpacity: 0.12,
          weight: 4
        }).addTo(map);
        approvedPolygonsRef.current.push(poly);
      }
    });
  }, []);

  // Initial mount - run exactly ONCE
  useEffect(() => {
    if (!mapContainer.current) return;

    let map: L.Map;
    try {
      map = mapService.initializeMap(mapContainer.current, center, zoom);
      mapRef.current = map;
    } catch (e: any) {
      console.error("Failed to initialize Leaflet Map:", e);
      setMapError(e.message || "Failed to initialize Leaflet Map");
      setLoading(false);
      return;
    }

    const resizeObserver = new ResizeObserver(() => {
      if (mapRef.current) {
        mapRef.current.invalidateSize();
      }
    });
    resizeObserver.observe(mapContainer.current);

    // Custom Locate Button
    const LocateControl = L.Control.extend({
      options: { position: 'topright' },
      onAdd: function() {
        const btn = L.DomUtil.create('button', 'leaflet-bar leaflet-control leaflet-custom-locate');
        btn.type = 'button';
        btn.title = 'Locate Me';
        btn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="#333" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><circle cx="12" cy="12" r="3"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/></svg>`;
        btn.style.width = '30px';
        btn.style.height = '30px';
        btn.style.display = 'flex';
        btn.style.alignItems = 'center';
        btn.style.justifyContent = 'center';
        btn.style.backgroundColor = '#ffffff';
        btn.style.border = 'none';
        btn.style.borderRadius = '4px';
        btn.style.cursor = 'pointer';
        btn.style.boxShadow = '0 1px 5px rgba(0,0,0,0.4)';

        btn.onclick = function() {
          map.locate({ setView: true, maxZoom: 14 });
        };
        return btn;
      }
    });
    map.addControl(new LocateControl());

    setLoading(false);

    if (mode === 'picker') {
      const pickerIcon = L.divIcon({
        html: '<div style="display: flex; justify-content: center; align-items: center; width: 30px; height: 30px; background-color: #10b981; border: 2px solid white; border-radius: 50%; box-shadow: 0 2px 5px rgba(0,0,0,0.3);"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg></div>',
        className: 'custom-picker-marker',
        iconSize: [30, 30],
        iconAnchor: [15, 30]
      });

      pickerMarkerRef.current = L.marker([pickerLat, pickerLng], { icon: pickerIcon, draggable: true }).addTo(map);

      pickerMarkerRef.current.on('dragend', (e: any) => {
        const pos = e.target.getLatLng();
        handleReverseGeocode(pos.lng, pos.lat, boundaryPointsRef.current);
      });

      if (initialBoundary && initialBoundary.length > 0) {
        loadBoundary(initialBoundary);
      } else {
        handleReverseGeocode(pickerLng, pickerLat, []);
      }
    } else if (mode === 'detail' && properties.length > 0) {
      const p = properties[0];
      mapService.addPropertyMarker(map, p);
      const isApproved = p.status === 'APPROVED';

      const parsed = parseBoundaryFromDescription(p.description || '');
      if (parsed && parsed.length > 0) {
        boundaryPointsRef.current = parsed;
        parsed.forEach((pt) => {
          const icon = L.divIcon({
            html: `<div class="w-2.5 h-2.5 rounded-full border border-white ${isApproved ? 'bg-emerald-500' : 'bg-amber-500'}"></div>`,
            className: 'custom-boundary-point-icon',
            iconSize: [10, 10],
            iconAnchor: [5, 5]
          });
          L.marker([pt[1], pt[0]], { icon }).addTo(map);
        });
        drawPolygon();

        const bounds = L.latLngBounds(parsed.map(pt => [pt[1], pt[0]]));
        map.fitBounds(bounds, { padding: [40, 40] });
      } else {
        const areaSqMeters = (p.area || 1) * 4046.86;
        const r = Math.sqrt(areaSqMeters / Math.PI);
        const pts: [number, number][] = [];
        const R = 6378137;
        for (let i = 0; i < 6; i++) {
          const angle = (i * 60) * Math.PI / 180;
          const dLat = (r * Math.sin(angle)) / R * 180 / Math.PI;
          const dLng = (r * Math.cos(angle)) / (R * Math.cos(p.latitude * Math.PI / 180)) * 180 / Math.PI;
          pts.push([p.longitude + dLng, p.latitude + dLat]);
        }
        pts.push(pts[0]);

        const latlngs = pts.map(pt => [pt[1], pt[0]] as [number, number]);
        const poly = L.polygon(latlngs, {
          color: isApproved ? '#059669' : '#d97706',
          fillColor: isApproved ? '#10b981' : '#f59e0b',
          fillOpacity: isApproved ? 0 : 0.10,
          weight: 3.5
        }).addTo(map);

        const bounds = L.latLngBounds(latlngs);
        map.fitBounds(bounds, { padding: [40, 40] });
      }
    } else if (mode === 'view') {
      propertyMarkersRef.current.forEach(m => m.remove());
      propertyMarkersRef.current = properties.map(p => mapService.addPropertyMarker(map, p, undefined, (prop) => {
        if (onScheduleVisitRef.current) {
          onScheduleVisitRef.current(prop);
        }
      }));
      renderCategorizedLandPolygons(map, properties);
      
      if (properties.length > 0) {
        const bounds = L.latLngBounds(properties.map(p => [p.latitude, p.longitude]));
        map.fitBounds(bounds, { padding: [50, 50] });
      }
    }

    map.on('click', (e: L.LeafletMouseEvent) => {
      if (mode !== 'picker') return;

      if (drawModeRef.current === 'pin') {
        if (pickerMarkerRef.current) {
          pickerMarkerRef.current.setLatLng(e.latlng);
          handleReverseGeocode(e.latlng.lng, e.latlng.lat, boundaryPointsRef.current);
        }
      } else {
        const pt: [number, number] = [e.latlng.lng, e.latlng.lat];
        boundaryPointsRef.current = [...boundaryPointsRef.current, pt];
        addBoundaryMarker(pt[0], pt[1], boundaryPointsRef.current.length - 1);
        drawPolygon();

        const cent = calculateCentroid(boundaryPointsRef.current);
        handleReverseGeocode(cent[0], cent[1], boundaryPointsRef.current);
      }
    });

    return () => {
      resizeObserver.disconnect();
      if (pickerMarkerRef.current) pickerMarkerRef.current.remove();
      boundaryMarkersRef.current.forEach(m => m.remove());
      propertyMarkersRef.current.forEach(m => m.remove());
      if (polylineRef.current) polylineRef.current.remove();
      if (polygonRef.current) polygonRef.current.remove();
      approvedPolygonsRef.current.forEach(p => p.remove());
      map.remove();
    };
  }, []);

  // Watch properties updates (in view mode and picker mode)
  useEffect(() => {
    const map = mapRef.current;
    if (!map || (mode !== 'view' && mode !== 'picker')) return;

    if (properties && properties.length > 0) {
      propertyMarkersRef.current.forEach(m => m.remove());
      propertyMarkersRef.current = properties.map(p => mapService.addPropertyMarker(map, p, undefined, (prop) => {
        if (onScheduleVisitRef.current) {
          onScheduleVisitRef.current(prop);
        }
      }));
      renderCategorizedLandPolygons(map, properties);
    }
  }, [properties, mode, renderCategorizedLandPolygons]);

  // Handle switching drawMode
  useEffect(() => {
    const map = mapRef.current;
    if (!map) return;
    if (drawMode === 'pin') {
      if (pickerMarkerRef.current) pickerMarkerRef.current.addTo(map);
      boundaryMarkersRef.current.forEach(m => m.remove());
    } else {
      if (pickerMarkerRef.current) pickerMarkerRef.current.remove();
      boundaryMarkersRef.current.forEach(m => m.addTo(map));
    }
  }, [drawMode]);

  return (
    <div className={`relative w-full h-full min-h-[300px] overflow-hidden rounded-2xl ${className}`}>
      <div ref={mapContainer} className="w-full h-full z-0" />

      {loading && (
        <div className="absolute inset-0 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center z-50">
          <div className="flex items-center gap-2 text-white bg-slate-900/90 px-4 py-2 rounded-xl text-xs font-bold shadow-lg">
            <RefreshCw className="w-4 h-4 animate-spin text-emerald-400" />
            <span>Loading Geospatial Boundary Map...</span>
          </div>
        </div>
      )}

      {/* Map Land Status Color Legend Overlay */}
      {mode === 'view' && (
        <div className="absolute bottom-4 left-4 bg-white/95 backdrop-blur-md px-3.5 py-2.5 rounded-xl border border-slate-200 shadow-md text-xs font-bold space-y-1.5 z-40 text-left">
          <div className="text-[10px] text-slate-500 font-extrabold uppercase tracking-wider mb-1">Land Status Legend</div>
          <div className="flex items-center gap-2 text-slate-800">
            <span className="w-3.5 h-3.5 rounded-xs bg-emerald-500/20 border-2 border-emerald-600" />
            <span>Approved Land (Green Outline)</span>
          </div>
          <div className="flex items-center gap-2 text-slate-800">
            <span className="w-3 h-3 rounded-full bg-amber-500 border border-amber-600" />
            <span>Pending / Unapproved Pin</span>
          </div>
        </div>
      )}

      {mode === 'picker' && (
        <React.Fragment>
          {properties && properties.length > 0 && (
            <div className="absolute bottom-4 right-4 z-40">
              <button
                type="button"
                onClick={() => {
                  const map = mapRef.current;
                  if (!map || properties.length === 0) return;
                  const bounds = L.latLngBounds(properties.map(p => [p.latitude, p.longitude]));
                  map.fitBounds(bounds, { padding: [60, 60] });
                }}
                className="text-[9px] font-extrabold text-blue-600 bg-blue-50 px-2 py-0.5 rounded-full hover:bg-blue-100 transition-colors cursor-pointer"
              >
                Fit All
              </button>
            </div>
          )}

          <div className="absolute top-4 left-4 z-40 glass-card p-3 flex flex-col gap-2 max-w-[210px] shadow-2xl border border-gray-200">
            <span className="text-[10px] font-bold text-gray-900 uppercase tracking-wider">Boundary Drawing Tool</span>
            <div className="flex bg-gray-100/50 p-0.5 rounded-lg border border-gray-200">
              <button
                type="button"
                onClick={() => setDrawMode('pin')}
                className={`flex-1 py-1 text-[9px] font-bold rounded-md transition-all text-center flex items-center justify-center gap-1 cursor-pointer
                  ${drawMode === 'pin' ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
              >
                <MapPin className="w-2.5 h-2.5" /> Pin Point
              </button>
              <button
                type="button"
                onClick={() => setDrawMode('draw')}
                className={`flex-1 py-1 text-[9px] font-bold rounded-md transition-all text-center flex items-center justify-center gap-1 cursor-pointer
                  ${drawMode === 'draw' ? 'bg-white text-gray-900 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
              >
                Draw Area
              </button>
            </div>

            {drawMode === 'draw' && (
              <div className="space-y-2 pt-1 border-t border-gray-200 text-left">
                <p className="text-[9px] text-gray-600 leading-tight">
                  Click map to add boundary points. Drag points to adjust.
                </p>
                {pointCount > 0 && (
                  <div className="flex justify-between items-center text-[9px] text-accent-400 font-semibold">
                    <span>Points Placed:</span>
                    <span>{pointCount}</span>
                  </div>
                )}
                <button
                  type="button"
                  onClick={() => clearBoundary(true)}
                  className="w-full py-1.5 bg-danger-500/10 hover:bg-danger-500/20 text-danger-400 font-bold text-[9px] rounded-lg transition border border-danger-500/20 flex items-center justify-center gap-1 cursor-pointer"
                >
                  <Trash2 className="w-2.5 h-2.5" /> Clear Points
                </button>
              </div>
            )}
          </div>
        </React.Fragment>
      )}
    </div>
  );
};
