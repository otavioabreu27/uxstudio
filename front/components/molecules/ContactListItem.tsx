"use client";

import { useState } from "react";
import Image from "next/image";
import { BellOff, Headphones, MoreHorizontal } from "lucide-react";
import ActionMenu from "./ActionMenu";
import { Button } from "./Button";

export interface ContactListItemProps {
  id: string;
  image?: string | null;
  name: string;
  phoneNumber: string;
  onMute?: () => void;
  onCall?: () => void;
  onEdit?: () => void;
  onFavorite?: () => void;
  onRemove?: () => void;
}

export default function ContactListItem({
  image,
  name,
  phoneNumber,
  onMute,
  onCall,
  onEdit,
  onFavorite,
  onRemove,
}: ContactListItemProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const avatarSrc = image || "/Default.png";

  return (
    <div
      className="
        group
        flex items-center justify-between w-full
        p-3 rounded-xl
        hover:bg-grey-90
        transition-colors duration-200
        cursor-pointer
        relative
      "
    >
      <div className="flex items-center gap-4">
        <div className="relative w-10 h-10 rounded-full overflow-hidden bg-grey-80 shrink-0 border border-transparent group-hover:border-grey-70 transition-colors">
          <Image
            src={avatarSrc}
            alt={name}
            fill
            sizes="40px"
            className="object-cover"
            unoptimized={true}
          />
        </div>

        <div className="flex flex-col">
          <span className="text-body-base text-text-primary font-medium leading-tight">
            {name}
          </span>
          <span className="text-message text-text-tertiary mt-0.5 font-normal">
            {phoneNumber}
          </span>
        </div>
      </div>

      <div className="relative flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity duration-200 ease-in-out">
        <Button
          ghost
          icon={BellOff}
          onClick={(e) => {
            e.stopPropagation();
            onMute?.();
          }}
        />

        <Button
          ghost
          icon={Headphones}
          onClick={(e) => {
            e.stopPropagation();
            onCall?.();
          }}
        />

        <Button
          ghost
          icon={MoreHorizontal}
          className={isMenuOpen ? "bg-grey-80 text-text-primary" : ""}
          onClick={(e) => {
            e.stopPropagation();
            setIsMenuOpen(!isMenuOpen);
          }}
        />

        <ActionMenu
          isOpen={isMenuOpen}
          onClose={() => setIsMenuOpen(false)}
          onEdit={onEdit}
          onFavorite={onFavorite}
          onRemove={onRemove}
        />
      </div>
    </div>
  );
}
