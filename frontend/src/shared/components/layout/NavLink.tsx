'use client';

import type { AnchorHTMLAttributes } from 'react';
import Link, { type LinkProps } from 'next/link';
import { usePathname } from 'next/navigation';
import { cn } from '@/lib/cn';

type LinkMatch = 'exact' | 'prefix';

export interface NavLinkProps
  extends LinkProps,
    Omit<AnchorHTMLAttributes<HTMLAnchorElement>, keyof LinkProps> {
  href: string;
  match?: LinkMatch;
  stacked?: boolean;
}

function isActivePath(pathname: string, href: string, match: LinkMatch) {
  if (href === '/') {
    return pathname === href;
  }

  if (match === 'exact') {
    return pathname === href;
  }

  return pathname === href || pathname.startsWith(`${href}/`);
}

export function NavLink({
  href,
  match = 'prefix',
  stacked = false,
  className,
  children,
  ...props
}: NavLinkProps) {
  const pathname = usePathname();
  const isActive = isActivePath(pathname, href, match);

  return (
    <Link
      href={href}
      className={cn(
        'inline-flex min-h-[var(--target-min-size)] items-center rounded-pill px-4 text-sm font-medium transition-colors focus-visible:outline-none',
        stacked && 'w-full justify-between',
        isActive
          ? 'bg-primary-soft text-primary'
          : 'text-secondary hover:bg-surface hover:text-foreground',
        className,
      )}
      aria-current={isActive ? 'page' : undefined}
      {...props}
    >
      {children}
    </Link>
  );
}

