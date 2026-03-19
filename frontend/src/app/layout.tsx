import type { Metadata } from 'next';
import './globals.css';
import { AppProviders } from '@/shared/providers/app-providers';

export const metadata: Metadata = {
  title: 'GameListo',
  description: 'Tu biblioteca social de videojuegos',
  icons: {
    icon: [{ url: '/logo-gamelist.svg', type: 'image/svg+xml', sizes: 'any' }],
    shortcut: '/logo-gamelist.svg',
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="es">
      <body>
        <AppProviders>{children}</AppProviders>
      </body>
    </html>
  );
}
