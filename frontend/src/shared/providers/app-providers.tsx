'use client';

import { PropsWithChildren, useState } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { IntlProvider } from 'react-intl';
import { ThemeProvider } from 'next-themes';

import { env } from '@/shared/config/env';
import esMessages from '@/shared/config/i18n/messages/es';
import enMessages from '@/shared/config/i18n/messages/en';
import { AuthProvider } from '@/features/auth/providers/AuthProvider';

const messagesByLocale = {
  es: esMessages,
  en: enMessages,
};

export function AppProviders({ children }: PropsWithChildren) {
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 30_000,
            refetchOnWindowFocus: false,
          },
        },
      }),
  );

  const locale = env.defaultLocale as 'es' | 'en';

  return (
    <ThemeProvider attribute="class" defaultTheme="dark" enableSystem>
      <IntlProvider locale={locale} messages={messagesByLocale[locale]}>
        <AuthProvider>
          <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
        </AuthProvider>
      </IntlProvider>
    </ThemeProvider>
  );
}
