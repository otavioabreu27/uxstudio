
const API_BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || "http://localhost:8080";

const MINIO_BASE_URL = process.env.NEXT_PUBLIC_MINIO_URL || "http://localhost:9000/uxstudio-contacts";

export interface Contact {
  id: string;
  name: string;
  phone: string;
  picture?: string;
  email: string;
}

export interface ContactCreationRequest {
  name: string;
  phoneNumber: string;
  email: string;
  imageBase64?: string;
}

export interface ContactEditionRequest {
  editedName?: string;
  editedPhoneNumber?: string;
  editedEmail?: string;
  editedImageBase64?: string;
}

// Helper function to handle fetch errors and extract userMessage
async function handleFetchError(res: Response): Promise<never> {
  let errorMessage = `HTTP error! status: ${res.status}`;
  try {
    const errorBody = await res.json();
    if (errorBody && typeof errorBody === 'object' && 'userMessage' in errorBody) {
      errorMessage = errorBody.userMessage;
    } else {
      errorMessage = `Backend error: ${res.status} ${res.statusText || ''}`;
    }
  } catch (parseError) {
    errorMessage = `Failed to parse error response from backend. Status: ${res.status}`;
  }
  throw new Error(errorMessage);
}


export const api = {
  async getContacts(): Promise<Contact[]> {
    const res = await fetch(`${API_BASE_URL}/contacts`, { cache: 'no-store' });
    if (!res.ok) await handleFetchError(res);
    const data = await res.json();
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    return data.map((c: any) => ({
      id: c.id,
      name: c.name,
      phone: c.phoneNumber,
      email: c.email,
      picture: c.imageId ? `${MINIO_BASE_URL}/${c.imageId}` : undefined,
    }));
  },

  async createContact(data: ContactCreationRequest): Promise<Contact> {
    const res = await fetch(`${API_BASE_URL}/contacts`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    });
    console.log(res);
    if (!res.ok) await handleFetchError(res);
    const c = await res.json();
    return {
      id: c.id,
      name: c.name,
      phone: c.phoneNumber,
      email: c.email,
      picture: c.imageId ? `${MINIO_BASE_URL}/${c.imageId}` : undefined,
    };
  },

  async editContact(id: string, data: ContactEditionRequest): Promise<Contact> {
    const res = await fetch(`${API_BASE_URL}/contacts/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res.ok) await handleFetchError(res);
    const c = await res.json();
    return {
        id: c.id,
        name: c.name,
        phone: c.phoneNumber,
        email: c.email,
        picture: c.imageId ? `${MINIO_BASE_URL}/${c.imageId}` : undefined,
    };
  },

  async deleteContact(id: string): Promise<void> {
    const res = await fetch(`${API_BASE_URL}/contacts/${id}`, {
      method: "DELETE",
    });
    if (!res.ok) await handleFetchError(res);
  },
};
