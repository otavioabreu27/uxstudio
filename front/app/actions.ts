"use server";

import { api } from "@/lib/api";
import { revalidatePath } from "next/cache";

interface ContactData {
  name: string;
  email: string;
  phoneNumber: string;
  picture?: string;
}

interface ActionResult {
  success: boolean;
  message: string;
}

export async function createContactAction(
  data: ContactData
): Promise<ActionResult> {
  try {
    await api.createContact({
      name: data.name,
      email: data.email,
      phoneNumber: data.phoneNumber,
      imageBase64: data.picture,
    });
    revalidatePath("/");
    return { success: true, message: "Contact created successfully." };
  } catch (error: any) {
    const userMessage = error.message || "An unexpected error occurred.";
    return { success: false, message: userMessage };
  }
}

interface EditedContactData {
  editedName: string;
  editedEmail: string;
  editedPhoneNumber: string;
  editedImageBase64?: string;
}

export async function updateContactAction(
  id: string,
  data: EditedContactData
): Promise<ActionResult> {
  try {
    await api.editContact(id, data);
    revalidatePath("/");
    return { success: true, message: "Contact updated successfully." };
  } catch (error: any) {
    const userMessage = error.message || "An unexpected error occurred.";
    return { success: false, message: userMessage };
  }
}

export async function deleteContactAction(id: string): Promise<ActionResult> {
  try {
    await api.deleteContact(id);
    revalidatePath("/");
    return { success: true, message: "Contact deleted successfully." };
  } catch (error: any) {
    const userMessage = error.message || "An unexpected error occurred.";
    return { success: false, message: userMessage };
  }
}
