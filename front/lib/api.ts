const IS_SERVER = typeof window === 'undefined';

const API_BASE_URL = IS_SERVER
  ? process.env.INTERNAL_API_URL || "http://backend:8080"
  : process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

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

export const api = {
  async getContacts(): Promise<Contact[]> {
    const res = await fetch(`${API_BASE_URL}/contacts`, { cache: 'no-store' });
    if (!res.ok) throw new Error("Failed to fetch contacts");
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
    if (!res.ok) throw new Error("Failed to create contact");
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
    if (!res.ok) throw new Error("Failed to edit contact");
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
    if (!res.ok) throw new Error("Failed to delete contact");
  },
};
