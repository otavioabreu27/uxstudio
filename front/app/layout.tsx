import localFont from "next/font/local";
import { Lexend_Deca } from "next/font/google";
import "./globals.css";

const glysa = localFont({
  src: [
    {
      path: "./fonts/Glysa.woff2",
      weight: "500",
    },
  ],
  variable: "--font-glysa",
});

const lexend = Lexend_Deca({
  subsets: ["latin"],
  weight: ["400"],
  variable: "--font-lexend",
});

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body
        className={`${glysa.variable} ${lexend.variable} bg-grey-100 text-text-primary antialiased`}
      >
        {children}
      </body>
    </html>
  );
}
