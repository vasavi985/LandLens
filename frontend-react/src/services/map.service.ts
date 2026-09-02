import axios from 'axios';
import L from 'leaflet';
import type { Property } from '../models/property.models';

export const mapService = {
  initializeMap: (container: string | HTMLElement, center: [number, number] = [80.4365, 16.3067], zoom = 10): L.Map => {
    // Leaflet center is [lat, lng]
    const mapInstance = L.map(container).setView([center[1], center[0]], zoom);
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(mapInstance);

    return mapInstance;
  },

  geocode: async (address: string): Promise<[number, number] | null> => {
    try {
      const url = `https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(address)}&limit=1`;
      const response = await axios.get(url);
      const data = response.data;
      if (data && data.length > 0) {
        // Return [lng, lat] for Mapbox compatibility
        return [parseFloat(data[0].lon), parseFloat(data[0].lat)];
      }
      return null;
    } catch (e) {
      console.error("Geocoding failed:", e);
      return null;
    }
  },

  reverseGeocode: async (lng: number, lat: number): Promise<any> => {
    try {
      const url = `https://nominatim.openstreetmap.org/reverse?format=json&lon=${lng}&lat=${lat}&addressdetails=1`;
      const response = await axios.get(url, {
        headers: { 
          'Accept-Language': 'en'
        }
      });
      const data = response.data;
      const details = { address: '', village: '', district: '', state: '', pincode: '' };
      
      if (data) {
        details.address = data.display_name || '';
        const addr = data.address || {};
        details.pincode = addr.postcode || '';
        // Capture village/suburb/town/city
        details.village = addr.village || addr.suburb || addr.town || addr.city || '';
        details.district = addr.district || addr.county || addr.city_district || '';
        details.state = addr.state || '';
      }
      return details;
    } catch (e) {
      console.error("Reverse geocoding failed:", e);
      return { address: '', village: '', district: '', state: '', pincode: '' };
    }
  },

  getMarkerColor: (status: string): string => {
    switch (status) {
      case 'APPROVED': return '#10b981';
      case 'PENDING_AI':
      case 'PENDING_GOVT': return '#f59e0b';
      case 'REJECTED':
      case 'DISPUTED':
      default: return '#ef4444';
    }
  },

  addPropertyMarker: (
    mapInstance: L.Map,
    property: Property,
    onClick?: (p: Property) => void,
    onScheduleVisit?: (p: Property) => void
  ): L.Marker => {
    const color = mapService.getMarkerColor(property.status);

    const container = document.createElement('div');
    container.style.fontFamily = 'system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
    container.style.padding = '4px 2px';
    container.style.minWidth = '190px';

    const titleEl = document.createElement('h4');
    titleEl.style.margin = '0 0 4px 0';
    titleEl.style.color = '#0f172a';
    titleEl.style.fontSize = '13px';
    titleEl.style.fontWeight = '700';
    titleEl.style.lineHeight = '1.3';
    titleEl.textContent = property.title || 'Untitled Property';

    const detailsEl = document.createElement('p');
    detailsEl.style.margin = '0 0 10px 0';
    detailsEl.style.color = '#475569';
    detailsEl.style.fontSize = '11.5px';
    detailsEl.style.fontWeight = '600';
    const formattedPrice = property.price ? `₹${property.price.toLocaleString('en-IN')}` : 'Price N/A';
    const areaText = property.area ? `${property.area} acres` : '';
    detailsEl.textContent = [formattedPrice, areaText].filter(Boolean).join(' • ');

    container.appendChild(titleEl);
    container.appendChild(detailsEl);

    if (onScheduleVisit) {
      const scheduleBtn = document.createElement('button');
      scheduleBtn.type = 'button';
      scheduleBtn.style.width = '100%';
      scheduleBtn.style.padding = '7.5px 12px';
      scheduleBtn.style.backgroundColor = '#2563eb';
      scheduleBtn.style.color = '#ffffff';
      scheduleBtn.style.border = 'none';
      scheduleBtn.style.borderRadius = '10px';
      scheduleBtn.style.fontSize = '11.5px';
      scheduleBtn.style.fontWeight = '700';
      scheduleBtn.style.cursor = 'pointer';
      scheduleBtn.style.display = 'flex';
      scheduleBtn.style.alignItems = 'center';
      scheduleBtn.style.justifyContent = 'center';
      scheduleBtn.style.gap = '6px';
      scheduleBtn.style.boxShadow = '0 2px 5px rgba(37,99,235,0.25)';
      scheduleBtn.style.transition = 'all 0.15s ease';

      scheduleBtn.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><rect width="18" height="18" x="3" y="4" rx="2" ry="2"/><line x1="16" x2="16" y1="2" y2="6"/><line x1="8" x2="8" y1="2" y2="6"/><line x1="3" x2="21" y1="10" y2="10"/></svg>
        <span>Schedule Visit</span>
      `;

      scheduleBtn.onmouseover = () => { scheduleBtn.style.backgroundColor = '#1d4ed8'; };
      scheduleBtn.onmouseout = () => { scheduleBtn.style.backgroundColor = '#2563eb'; };

      scheduleBtn.addEventListener('click', (e) => {
        e.stopPropagation();
        onScheduleVisit(property);
      });

      container.appendChild(scheduleBtn);
    }

    // Custom colored Leaflet marker icon using L.divIcon
    const svgIcon = L.divIcon({
      html: `<div style="display: flex; justify-content: center; align-items: center; width: 30px; height: 30px; background-color: ${color}; border: 2px solid white; border-radius: 50%; box-shadow: 0 2px 5px rgba(0,0,0,0.3);"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg></div>`,
      className: 'custom-leaflet-marker',
      iconSize: [30, 30],
      iconAnchor: [15, 30],
      popupAnchor: [0, -30]
    });

    const marker = L.marker([property.latitude, property.longitude], { icon: svgIcon })
      .bindPopup(container)
      .addTo(mapInstance);

    if (onClick) {
      marker.on('click', () => {
        onClick(property);
      });
    }

    return marker;
  }
};
