import React from "react";

interface TextFieldProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
}

export function TextField({ label, className = "", ...props }: TextFieldProps) {
  return (
    <div className="flex flex-col gap-1.5 w-full">
      {label && (
        <label className="text-body-base text-text-secondary grey-80 tracking-wide">
          {label}
        </label>
      )}

      <input
        className={`
          w-full
          bg-grey-80
          border border-grey-60
          rounded-lg
          px-4 py-3

          text-body-base
          text-text-primary

          placeholder:text-text-tertiary

          hover:border-grey-50

          focus:border-grey-10
          focus:bg-grey-60

          transition-colors duration-100
          ${className}
        `}
        {...props}
      />
    </div>
  );
}
