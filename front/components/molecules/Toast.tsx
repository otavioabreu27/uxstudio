"use client";

import { useEffect } from "react";
import { X, CheckCircle, AlertTriangle } from "lucide-react";

interface ToastProps {
  message: string;
  type: "success" | "error";
  onClose: () => void;
}

const toastTypes = {
  success: {
    icon: <CheckCircle className="h-6 w-6 text-green-500" />,
    style: "bg-green-100 border-green-500 text-green-700",
  },
  error: {
    icon: <AlertTriangle className="h-6 w-6 text-red-500" />,
    style: "bg-red-100 border-red-500 text-red-700",
  },
};

export default function Toast({ message, type, onClose }: ToastProps) {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, 2000);

    return () => {
      clearTimeout(timer);
    };
  }, [onClose]);

  const { icon, style } = toastTypes[type];

  return (
    <div
      className={`fixed top-5 right-5 z-100 flex items-center p-4 rounded-lg shadow-lg animate-in fade-in slide-in-from-top-5 ${style}`}
      role="alert"
    >
      <div className="shrink-0">{icon}</div>
      <div className="ml-3 text-sm font-medium">{message}</div>
      <button
        type="button"
        className="ml-auto -mx-1.5 -my-1.5 rounded-lg p-1.5 inline-flex h-8 w-8"
        onClick={onClose}
        aria-label="Close"
      >
        <X className="h-5 w-5" />
      </button>
    </div>
  );
}
