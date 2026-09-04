import axios from 'axios';

export interface CloudinaryUploadResponse {
  secure_url: string;
  url: string;
  resource_type: string;
  format: string;
  duration?: number;
  public_id: string;
}

const getCloudinaryConfig = () => {
  const cloudName = (import.meta.env.VITE_CLOUDINARY_CLOUD_NAME || 'jrgwnblg').trim();
  const uploadPreset = (import.meta.env.VITE_CLOUDINARY_UPLOAD_PRESET || 'landlens_upload').trim();

  return { cloudName, uploadPreset };
};

export const cloudinaryService = {
  uploadFile: async (file: File): Promise<CloudinaryUploadResponse> => {
    const { cloudName, uploadPreset } = getCloudinaryConfig();

    if (!cloudName || cloudName === 'YOUR_CLOUDINARY_CLOUD_NAME') {
      throw new Error(
        'Cloudinary Cloud Name is not configured. Please set VITE_CLOUDINARY_CLOUD_NAME in frontend-react/.env.production with your Cloudinary Cloud Name.'
      );
    }

    if (!uploadPreset || uploadPreset === 'YOUR_CLOUDINARY_UNSIGNED_UPLOAD_PRESET') {
      throw new Error(
        'Cloudinary Upload Preset is not configured. Please set VITE_CLOUDINARY_UPLOAD_PRESET in frontend-react/.env.production with an Unsigned upload preset.'
      );
    }

    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', uploadPreset);

    const url = `https://api.cloudinary.com/v1_1/${cloudName}/auto/upload`;

    try {
      const response = await axios.post<CloudinaryUploadResponse>(url, formData);
      return response.data;
    } catch (error: any) {
      const status = error.response?.status;
      const serverMessage = error.response?.data?.error?.message || error.message;

      if (status === 401) {
        throw new Error(
          `Cloudinary Upload Failed (401 Unauthorized): ${serverMessage}. ` +
          `Verify that the preset '${uploadPreset}' in Cloud '${cloudName}' is set to Signing Mode: 'Unsigned' in Cloudinary Settings -> Upload -> Upload presets.`
        );
      } else if (status === 400) {
        throw new Error(
          `Cloudinary Upload Failed (400 Bad Request): ${serverMessage}. ` +
          `Check file size, format, or upload preset settings.`
        );
      } else {
        throw new Error(`Cloudinary Upload Failed (${status || 'Network Error'}): ${serverMessage}`);
      }
    }
  },

  getThumbnailUrl: (uploadRes: CloudinaryUploadResponse): string => {
    const url = uploadRes.secure_url;
    if (uploadRes.resource_type === 'video') {
      return url.replace(/\.[^/.]+$/, '.jpg').replace('/upload/', '/upload/so_1/');
    }
    if (uploadRes.resource_type === 'image') {
      return url.replace('/upload/', '/upload/c_scale,w_300/');
    }
    return '/assets/images/document-icon.png';
  }
};
