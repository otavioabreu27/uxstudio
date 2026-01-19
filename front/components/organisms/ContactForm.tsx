"use client";

import { useRef, useState } from "react";
import Image from "next/image";
import { Plus, RefreshCw, Trash, User } from "lucide-react";
import { Button } from "@/components/molecules/Button";
import { TextField } from "@/components/molecules/TextField";
import Icon from "@/components/atoms/Icon";

export interface ContactFormData {
  name: string;
  phoneNumber: string;
  email: string;
  picture?: string | null;
}

interface ContactFormProps {
  initialData?: ContactFormData;
  isEditMode?: boolean;
  onCancel?: () => void;
  onSubmit?: (data: ContactFormData) => void;
}

export default function ContactForm({
  initialData,
  isEditMode = false,
  onCancel,
  onSubmit,
}: ContactFormProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [formData, setFormData] = useState<ContactFormData>({
    name: initialData?.name || "",
    phoneNumber: initialData?.phoneNumber || "",
    email: initialData?.email || "",
    picture: initialData?.picture || null,
  });

  const title = isEditMode ? "Edit contact" : "Add contact";
  const doneButtonText = isEditMode ? "Save" : "Done";


  const handleAddPicture = () => {
    fileInputRef.current?.click();
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData({ ...formData, picture: reader.result as string });
      };
      reader.readAsDataURL(file);
    }
  };

  const handleRemovePicture = () => {
    setFormData({ ...formData, picture: null });
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  return (
    <div className="flex flex-col w-full max-w-91 bg-grey-100 p-6 rounded-3xl gap-6 shadow-2xl">
      <h1 className="text-h1 text-text-primary">{title}</h1>

      <div className="flex items-center gap-4">
        <input
          type="file"
          ref={fileInputRef}
          onChange={handleFileChange}
          accept="image/png, image/jpeg"
          className="hidden"
        />
        <div className="relative w-22 h-22 rounded-full bg-grey-80 flex items-center justify-center overflow-hidden shrink-0">
          {formData.picture ? (
            <Image
              src={formData.picture}
              alt="Profile"
              fill
              className="object-cover"
              unoptimized={true}
            />
          ) : (
            <Icon icon={User} className="w-8 h-8 text-text-primary" />
          )}
        </div>

        <div className="flex items-center gap-2">
          {!formData.picture ? (
            <Button
              variant="secondary"
              icon={Plus}
              text="Add picture"
              onClick={handleAddPicture}
            />
          ) : (
            <>
              <Button
                variant="secondary"
                icon={RefreshCw}
                text="Change picture"
                onClick={handleAddPicture}
              />
              <Button
                variant="secondary"
                icon={Trash}
                onClick={handleRemovePicture}
              />
            </>
          )}
        </div>
      </div>

      <div className="flex flex-col gap-4">
        <TextField
          label="Name"
          placeholder="Jamie Wright"
          value={formData.name}
          onChange={(e) => setFormData({ ...formData, name: e.target.value })}
        />
        <TextField
          label="Phone number"
          placeholder="+01 234 5678"
          value={formData.phoneNumber}
          onChange={(e) =>
            setFormData({ ...formData, phoneNumber: e.target.value })
          }
        />
        <TextField
          label="Email address"
          placeholder="jamie.wright@mail.com"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        />
      </div>

      <div className="flex justify-end items-center gap-2 mt-2">
        <Button ghost text="Cancel" onClick={onCancel} />
        <Button
          variant="primary"
          text={doneButtonText}
          onClick={() => onSubmit?.(formData)}
        />
      </div>
    </div>
  );
}
