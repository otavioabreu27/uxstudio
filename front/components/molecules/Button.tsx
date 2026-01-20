import React, { ButtonHTMLAttributes } from "react";
import Icon from "../atoms/Icon";
import { LucideIcon } from "lucide-react";

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  icon?: LucideIcon;
  text?: string;
  variant?: "primary" | "secondary" | "special";
  ghost?: boolean;
}

export function Button({
  icon: IconNode,
  text,
  children,
  variant = "primary",
  ghost = false,
  className = "",
  ...props
}: ButtonProps) {

  const content = text || children;

  const baseStyles = `
      flex flex-row items-center justify-center gap-2
      transition-all duration-150
      font-medium text-body-base
      disabled:opacity-50 disabled:cursor-not-allowed
      cursor:pointer
    `;

  const variants = {
    primary: "rounded-lg bg-grey-60 text-text-primary hover:bg-grey-50 active:bg-grey-40 border border-transparent",
    secondary: "rounded-lg bg-grey-100 text-text-primary hover:bg-grey-90 active:bg-grey-80 border border-transparent",
    special: "rounded-3xl bg-grey-60 text-text-primary hover:bg-grey-50 active:bg-grey-40 border border-transparent",
  };

  const ghostVariants = {
      primary: "bg-transparent rounded-lg bg-grey-60 text-text-primary hover:bg-grey-50 active:bg-grey-40 border border-transparent",
      secondary: "bg-transparent rounded-lg bg-grey-100 text-text-primary hover:bg-grey-90 active:bg-grey-80 border border-transparent",
      special: "bg-transparent rounded-3xl bg-grey-60 text-text-primary hover:bg-grey-50 active:bg-grey-40 border border-transparent",
    };


  const ghostStyles = "bg-transparent hover:bg-grey-80 active:bg-grey-70 border-transparent";

  const appliedStyle = ghost ? ghostVariants[variant] : variants[variant];


  const hasContent = Boolean(content);

  const paddingClass = (!hasContent && IconNode)
    ? "p-2 aspect-square"
    : "px-4 py-2";

  return (
    <button
      className={`
        ${baseStyles}
        ${appliedStyle}
        ${paddingClass}
        ${className}
      `}
      {...props}
    >
      {IconNode && (
        <Icon
          icon={IconNode}
          className="text-current"
        />
      )}

      {content && <span>{content}</span>}
    </button>
  );
}
