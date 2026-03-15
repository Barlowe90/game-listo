'use client';

import { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import AvatarUsuario from '@/features/auth/components/AvatarUsuario';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/cn';
import { Container } from '@/shared/components/layout/Container';
import { NavLink } from '@/shared/components/layout/NavLink';
import { SearchBar } from '@/shared/components/layout/SearchBar';
import { Button } from '@/shared/components/ui/Button';
import {
  Dropdown,
  DropdownContent,
  DropdownItem,
  DropdownLabel,
  DropdownTrigger,
} from '@/shared/components/ui/Dropdown';
import { Skeleton } from '@/shared/components/ui/Skeleton';

const navigationItems = [
  { href: '/biblioteca', label: 'Mi biblioteca' },
  { href: '/contacto', label: 'Contacto' },
] as const;

const videojuegosItems = [
  {
    href: '/catalogo',
    label: 'Explorar catalogo',
    description: 'Busca fichas, descubre generos y entra en las cards del MVP.',
  },
  {
    href: '/videojuego/demo',
    label: 'Ficha demo',
    description: 'Revisa tabs, dialog, grids y secciones en accion.',
  },
] as const;

function AuthControl() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div
        className="flex items-center gap-3 rounded-pill border border-border bg-surface p-2 shadow-surface"
        role="status"
        aria-live="polite"
        aria-label="Cargando sesion"
      >
        <Skeleton variant="avatar" size="sm" />
        <div className="hidden min-w-20 gap-2 sm:grid">
          <Skeleton variant="line" size="sm" className="w-20" />
          <Skeleton variant="line" size="sm" className="w-12" />
        </div>
      </div>
    );
  }

  if (isAuthenticated) {
    return <AvatarUsuario />;
  }

  return (
    <Button asChild>
      <Link href="/login">Iniciar sesion</Link>
    </Button>
  );
}

export function Header() {
  const pathname = usePathname();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const isVideojuegosActive = pathname === '/catalogo' || pathname.startsWith('/videojuego/');

  return (
    <header className="border-b border-border bg-background">
      <Container size="wide" className="py-4">
        <div className="flex items-center justify-between gap-4">
          <Link
            href="/"
            className="inline-flex min-h-[var(--target-min-size)] items-center gap-3 rounded-pill px-3 py-2 transition-colors hover:bg-surface"
          >
            <span className="flex size-10 items-center justify-center rounded-pill bg-primary text-sm font-bold text-primary-foreground shadow-surface">
              G
            </span>
            <span className="grid">
              <span className="text-sm font-semibold text-foreground">GameListo</span>
              <span className="text-xs text-muted-foreground">Layout shell MVP</span>
            </span>
          </Link>

          <div className="hidden flex-1 items-center justify-end gap-3 lg:flex">
            <div className="w-full max-w-md">
              <SearchBar size="sm" />
            </div>

            <nav className="flex items-center gap-2" aria-label="Navegacion principal">
              <Dropdown>
                <DropdownTrigger
                  className={cn(
                    isVideojuegosActive
                      ? 'bg-primary-soft text-primary hover:bg-primary-soft hover:text-primary'
                      : undefined,
                  )}
                >
                  Videojuegos
                </DropdownTrigger>
                <DropdownContent align="start">
                  <DropdownLabel>Explorar</DropdownLabel>
                  {videojuegosItems.map((item) => (
                    <DropdownItem key={item.href} asChild>
                      <Link href={item.href}>
                        <span className="grid gap-1 py-2">
                          <span className="font-medium text-foreground">{item.label}</span>
                          <span className="text-xs leading-relaxed text-muted-foreground">
                            {item.description}
                          </span>
                        </span>
                      </Link>
                    </DropdownItem>
                  ))}
                </DropdownContent>
              </Dropdown>

              {navigationItems.map((item) => (
                <NavLink key={item.href} href={item.href}>
                  {item.label}
                </NavLink>
              ))}
            </nav>
            <AuthControl />
          </div>

          <button
            type="button"
            className="inline-flex min-h-[var(--target-min-size)] items-center rounded-pill border border-border bg-surface px-4 text-sm font-semibold text-foreground shadow-surface transition-colors hover:border-border-strong hover:bg-card lg:hidden"
            onClick={() => setIsMobileMenuOpen((currentValue) => !currentValue)}
            aria-expanded={isMobileMenuOpen}
            aria-controls="mobile-navigation"
          >
            {isMobileMenuOpen ? 'Cerrar' : 'Menu'}
          </button>
        </div>

        {isMobileMenuOpen ? (
          <div
            id="mobile-navigation"
            className="mt-4 grid gap-4 border-t border-border pt-4 lg:hidden"
          >
            <SearchBar onSearch={() => setIsMobileMenuOpen(false)} />

            <div className="grid gap-2" role="navigation" aria-label="Navegacion movil">
              <span className="px-2 text-xs font-semibold tracking-[0.08em] text-primary uppercase">
                Videojuegos
              </span>
              {videojuegosItems.map((item) => (
                <NavLink
                  key={item.href}
                  href={item.href}
                  stacked
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  {item.label}
                </NavLink>
              ))}
              {navigationItems.map((item) => (
                <NavLink
                  key={item.href}
                  href={item.href}
                  stacked
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  {item.label}
                </NavLink>
              ))}
            </div>
            <div className="grid gap-3 border-t border-border pt-4">
              <AuthControl />
            </div>
          </div>
        ) : null}
      </Container>
    </header>
  );
}

export default Header;
