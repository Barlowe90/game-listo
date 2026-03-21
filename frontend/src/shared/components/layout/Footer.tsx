import { Linkedin } from 'lucide-react';
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
              integrated
                ? 'hover:bg-white/10 hover:text-inverse'
                : 'hover:bg-card hover:text-foreground',
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
              integrated
                ? 'hover:bg-white/10 hover:text-inverse'
                : 'hover:bg-card hover:text-foreground',
            )}
          >
            <Linkedin className="size-5 shrink-0" aria-hidden="true" />
          </a>
        </div>
      </Container>
    </footer>
  );
}
