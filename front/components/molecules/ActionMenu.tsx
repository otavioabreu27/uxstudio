import { Settings, Heart, Trash, LucideIcon } from "lucide-react";
import Icon from "@/components/atoms/Icon";

interface ActionMenuProps {
  isOpen: boolean;
  onEdit?: () => void;
  onFavorite?: () => void;
  onRemove?: () => void;
  onClose: () => void;
}

export default function ActionMenu({
  isOpen,
  onEdit,
  onFavorite,
  onRemove,
  onClose,
}: ActionMenuProps) {
  if (!isOpen) return null;

  return (
    <div
      className="
      absolute right-0 top-10
      z-50 w-48
      bg-grey-80
      border border-grey-70
      rounded-xl shadow-xl
      flex flex-col p-1
      animate-in fade-in zoom-in-95 duration-100
    "
    >
      <MenuItem icon={Settings} text="Edit" onClick={onEdit} />
      <MenuItem icon={Heart} text="Favourite" onClick={onFavorite} />
      <MenuItem icon={Trash} text="Remove" onClick={onRemove} />

      <div
        className="fixed inset-0 z-[-1]"
        onClick={(e) => {
          e.stopPropagation();
          onClose();
        }}
      />
    </div>
  );
}

function MenuItem({
  icon,
  text,
  onClick,
}: {
  icon: LucideIcon;
  text: string;
  onClick?: () => void;
}) {
  return (
    <button
      onClick={(e) => {
        e.stopPropagation();
        onClick?.();
      }}
      className="
        flex items-center gap-3
        w-full px-3 py-2.5
        rounded-lg
        text-body-base text-text-primary text-left
        hover:bg-grey-70
        transition-colors
      "
    >
      <Icon icon={icon} className="text-text-secondary" />
      <span>{text}</span>
    </button>
  );
}
