import type { Metadata } from 'next';
import './globals.css';
import { AppProviders } from '@/shared/providers/app-providers';
import Navbar from '@/shared/components/layout/Navbar';

export const metadata: Metadata = {
  title: 'GameListo',
  description: 'Tu biblioteca social de videojuegos',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es" suppressHydrationWarning>
      <body>
        <AppProviders>
          <Navbar />
          {children}
        </AppProviders>
      </body>
    </html>
  );
}
