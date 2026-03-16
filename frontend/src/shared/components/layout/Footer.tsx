import Link from 'next/link';
import { cn } from '@/lib/cn';
import { Container } from '@/shared/components/layout/Container';

const LINKEDIN_URL = 'https://www.linkedin.com/in/adri-r/';

export interface FooterProps {
  integrated?: boolean;
}

export function Footer({ integrated = false }: FooterProps) {
  return (
    <footer className={cn(integrated ? 'bg-transparent' : 'bg-surface')}>
      <Container size="wide" className="py-4 lg:py-6">
        <div
          className={cn(
            'mt-4 flex flex-col gap-3 pt-4 text-sm sm:flex-row sm:items-center sm:justify-between',
            integrated ? 'text-white/80' : 'text-muted-foreground',
          )}
        >
          <Link
            href="/nosotros"
            className={cn(
              'inline-flex min-h-[var(--target-min-size)] items-center rounded-pill px-3 py-2 transition-colors',
              integrated ? 'hover:bg-white/10 hover:text-inverse' : 'hover:bg-card hover:text-foreground',
            )}
          >
            Nosotros
          </Link>
          <a
            href={LINKEDIN_URL}
            target="_blank"
            rel="noreferrer"
            aria-label="Visitar LinkedIn"
            className={cn(
              'inline-flex min-h-[var(--target-min-size)] w-fit items-center justify-center rounded-pill px-3 py-2 transition-colors',
              integrated ? 'hover:bg-white/10 hover:text-inverse' : 'hover:bg-card hover:text-foreground',
            )}
          >
            <svg aria-hidden="true" viewBox="0 0 24 24" className="size-5 fill-current">
              <path d="M19 3A2 2 0 0 1 21 5V19A2 2 0 0 1 19 21H5A2 2 0 0 1 3 19V5A2 2 0 0 1 5 3H19ZM8.34 10.66H5.67V18H8.34V10.66ZM7 6.4A1.6 1.6 0 1 0 7 9.6A1.6 1.6 0 0 0 7 6.4ZM18.35 13.49C18.35 11.25 17.15 10.21 15.55 10.21C14.26 10.21 13.68 10.92 13.36 11.41V10.66H10.69V18H13.36V14.1C13.36 13.07 13.56 12.08 14.84 12.08C16.11 12.08 16.13 13.27 16.13 14.16V18H18.8L18.35 13.49Z" />
            </svg>
          </a>
        </div>
      </Container>
    </footer>
  );
}
