import { PlusIcon, Settings } from "lucide-react";
import { Button } from "@/components/molecules/Button";
import Image from "next/image";

interface HeaderProps {
  title: string;
  onAddContact: () => void;
}

export default function Header({ title, onAddContact }: HeaderProps) {
  return (
    <div className="flex items-center justify-between w-full h-30 px-8 border-b border-grey-80">
      <h1 className="text-h1 text-text-primary font-display">{title}</h1>

      {/* Right: Actions & Tools */}
      <div className="flex items-center gap-4">
        <Button variant="secondary" icon={Settings} />

        {/* User Avatar (Static for demo) */}
        <div className="w-8 h-8 rounded-full overflow-hidden relative border border-grey-70">
          <Image src="/Default.png" alt="User" fill className="object-cover" />
        </div>

        {/* Primary Action */}
        <Button
          variant="special"
          text="Add new"
          icon={PlusIcon}
          onClick={onAddContact}
        />
      </div>
    </div>
  );
}
