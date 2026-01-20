"use client";

import { useState } from "react";
import Header from "@/components/organisms/Header";
import ContactListItem from "@/components/molecules/ContactListItem";
import ContactForm, {
  ContactFormData,
} from "@/components/organisms/ContactForm";
import { Button } from "../molecules/Button";
import { ArrowLeft, Sun } from "lucide-react";
import { Contact } from "@/lib/api";
import {
  createContactAction,
  deleteContactAction,
  updateContactAction,
} from "@/app/actions";
import Toast from "../molecules/Toast";

interface ContactsTemplateProps {
  initialContacts: Contact[];
}

type Notification = {
  message: string;
  type: "success" | "error";
};

export default function ContactsTemplate({
  initialContacts,
}: ContactsTemplateProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingContact, setEditingContact] = useState<Contact | null>(null);
  const [notification, setNotification] = useState<Notification | null>(null);

  const contacts = initialContacts;

  const handleEdit = (contact: Contact) => {
    setEditingContact(contact);
    setIsEditModalOpen(true);
  };

  const showNotification = (notification: Notification) => {
    setNotification(notification);
  };

  const handleCreateContact = async (data: ContactFormData) => {
    const result = await createContactAction({
      name: data.name,
      phoneNumber: data.phoneNumber,
      email: data.email,
      picture: data.picture?.startsWith("data:") ? data.picture : undefined,
    });

    showNotification({
      message: result.message,
      type: result.success ? "success" : "error",
    });

    if (result.success) {
      setIsModalOpen(false);
    }
  };

  const handleUpdateContact = async (data: ContactFormData) => {
    if (!editingContact) return;
    const result = await updateContactAction(editingContact.id, {
      editedName: data.name,
      editedPhoneNumber: data.phoneNumber,
      editedEmail: data.email,
      editedImageBase64: data.picture?.startsWith("data:")
        ? data.picture
        : undefined,
    });

    showNotification({
      message: result.message,
      type: result.success ? "success" : "error",
    });

    if (result.success) {
      setIsEditModalOpen(false);
    }
  };

  const handleRemoveContact = async (id: string) => {
    const result = await deleteContactAction(id);
    showNotification({
      message: result.message,
      type: result.success ? "success" : "error",
    });
  };

  return (
    <div className="w-full h-screen bg-grey-100 text-text-primary grid grid-cols-[30%_1fr_30%] overflow-hidden">
      {notification && (
        <Toast
          message={notification.message}
          type={notification.type}
          onClose={() => setNotification(null)}
        />
      )}

      <aside className="flex flex-col items-center h-full border-r border-transparent">
        <div className="h-30 w-full flex items-center justify-end">
          <Button variant="secondary" icon={ArrowLeft} />
        </div>
      </aside>

      <main className="flex flex-col relative h-full overflow-y-auto">
        <Header title="Contacts" onAddContact={() => setIsModalOpen(true)} />

        <div className="flex flex-col gap-2 mt-1 w-full max-w-3xl mx-auto">
          {contacts.map((contact) => (
            <ContactListItem
              key={contact.id}
              id={contact.id}
              name={contact.name}
              phoneNumber={contact.phone}
              image={contact.picture}
              onMute={() => console.log("Mute", contact.id)}
              onCall={() => console.log("Call", contact.id)}
              onEdit={() => handleEdit(contact)}
              onRemove={() => handleRemoveContact(contact.id)}
            />
          ))}
        </div>
      </main>

      <aside className="flex flex-col items-center h-full border-l border-transparent">
        <div className="h-30 w-full flex items-center justify-start">
          <Button variant="secondary" icon={Sun} />
        </div>
      </aside>

      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
          <ContactForm
            onCancel={() => setIsModalOpen(false)}
            onSubmit={handleCreateContact}
          />
          <div
            className="absolute inset-0 z-[-1]"
            onClick={() => setIsModalOpen(false)}
          />
        </div>
      )}

      {isEditModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm animate-in fade-in duration-200">
          <ContactForm
            isEditMode
            onCancel={() => setIsEditModalOpen(false)}
            onSubmit={handleUpdateContact}
            initialData={{
              name: editingContact?.name || "",
              phoneNumber: editingContact?.phone || "",
              email: editingContact?.email || "",
              picture: editingContact?.picture,
            }}
          />
          <div
            className="absolute inset-0 z-[-1]"
            onClick={() => setIsEditModalOpen(false)}
          />
        </div>
      )}
    </div>
  );
}
