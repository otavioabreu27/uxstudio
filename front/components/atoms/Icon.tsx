import { LucideIcon } from "lucide-react";

interface IconProps {
  icon: LucideIcon;
  className?: string;
}

export default function Icon({ icon: IconNode, className = "" }: IconProps) {
  return (
    <IconNode
      size={19}
      className={`text-(--white-100) ${className}`}
      strokeWidth={2}
    />
  );
}
