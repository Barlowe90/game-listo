import Link from 'next/link';
import type { ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { Card } from '@/shared/components/ui/Card';

export const PROFILE_SECTIONS = [
  {
    key: 'biblioteca',
    label: 'Biblioteca',
  },
  {
    key: 'amigos',
    label: 'Amigos',
  },
  {
    key: 'ajustes',
    label: 'Ajustes',
  },
] as const;

export type ProfileSectionKey = (typeof PROFILE_SECTIONS)[number]['key'];
export type LanguageCode = 'ESP' | 'ENG';

export function SurfaceCard({
  children,
  className,
}: Readonly<{
  children: ReactNode;
  className?: string;
}>) {
  return (
    <Card
      className={cn(
        'rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
        className,
      )}
    >
      {children}
    </Card>
  );
}

export function SidebarSectionLink({
  active,
  href,
  label,
}: Readonly<{
  active: boolean;
  href: string;
  label: string;
}>) {
  return (
    <Link
      href={href}
      aria-current={active ? 'page' : undefined}
      className={cn(
        'grid rounded-[calc(var(--radius-xl)+0.1rem)] border px-4 py-3 transition-[background-color,border-color,transform,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:-translate-y-px',
        active
          ? 'border-primary/30 bg-primary-soft text-foreground shadow-surface'
          : 'border-border bg-white/70 text-secondary hover:border-border-strong hover:bg-white hover:text-foreground',
      )}
    >
      <span className="text-sm font-semibold">{label}</span>
    </Link>
  );
}

export function normalizeLanguage(value: string | null | undefined): LanguageCode {
  return value === 'ENG' ? 'ENG' : 'ESP';
}

export function getSectionLabel(sectionKey: ProfileSectionKey) {
  return PROFILE_SECTIONS.find((section) => section.key === sectionKey)?.label ?? 'Perfil';
}

export function SimpleStateCard({
  action,
  title,
}: Readonly<{
  action?: ReactNode;
  title: string;
}>) {
  return (
    <SurfaceCard>
      <div className="grid justify-items-center gap-4 p-8 text-center">
        <h2 className="text-xl font-semibold tracking-tight text-foreground">{title}</h2>
        {action ? <div className="flex flex-wrap justify-center gap-3">{action}</div> : null}
      </div>
    </SurfaceCard>
  );
}
