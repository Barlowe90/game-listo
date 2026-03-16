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
  DropdownTrigger,
} from '@/shared/components/ui/Dropdown';
import { Skeleton } from '@/shared/components/ui/Skeleton';

const navigationItems = [
  { href: '/biblioteca', label: 'Mi biblioteca' },
  { href: '/publicaciones', label: 'Mis publicaciones' },
] as const;

const videojuegosItems = [
  {
    href: '/catalogo',
    label: 'Explorar catalogo',
  },
  {
    href: '/videojuego/demo',
    label: 'Ficha demo',
  },
] as const;

interface AuthControlProps {
  integrated?: boolean;
}

function AuthControl({ integrated = false }: AuthControlProps) {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div
        className={cn(
          'flex items-center gap-3 rounded-pill border border-border bg-surface p-2 shadow-surface',
          integrated && 'border-white/15 bg-white/10 text-inverse shadow-none backdrop-blur-sm',
        )}
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
    <Button
      asChild
      className={cn(
        integrated
          ? 'border-transparent bg-white text-primary! shadow-none hover:bg-white/90 active:bg-white/80'
          : 'border-transparent bg-black text-white! shadow-none hover:bg-transparent hover:!text-foreground active:bg-transparent',
      )}
    >
      <Link href="/login">Iniciar sesion</Link>
    </Button>
  );
}

export interface HeaderProps {
  integrated?: boolean;
}

export function Header({ integrated = false }: HeaderProps) {
  const pathname = usePathname();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const isVideojuegosActive = pathname === '/catalogo' || pathname.startsWith('/videojuego/');

  return (
    <header className={cn(integrated ? 'bg-transparent' : 'border-b border-border bg-background')}>
      <Container size="wide" className="py-4">
        <div className="flex items-center justify-between gap-4 lg:grid lg:grid-cols-[auto_1fr_auto]">
          <Link
            href="/"
            className={cn(
              'inline-flex min-h-[var(--target-min-size)] items-center gap-3 rounded-pill px-3 py-2 transition-colors',
              integrated ? 'hover:bg-white/10' : 'hover:bg-surface',
            )}
          >
            <span className="flex size-10 items-center justify-center rounded-pill bg-primary text-sm font-bold text-primary-foreground shadow-surface">
              G
            </span>
            <span className="grid">
              <span
                className={cn(
                  'text-sm font-semibold',
                  integrated ? 'text-inverse' : 'text-foreground',
                )}
              >
                GameListo
              </span>
            </span>
          </Link>

          <div className="hidden min-w-0 items-center justify-center gap-3 lg:flex">
            <nav className="flex items-center gap-2" aria-label="Navegacion principal">
              <Dropdown>
                <DropdownTrigger
                  className={cn(
                    integrated
                      ? isVideojuegosActive
                        ? 'bg-primary-soft text-primary hover:bg-primary-soft hover:text-primary'
                        : 'text-inverse hover:bg-white/10 hover:text-inverse data-[state=open]:border-white/15 data-[state=open]:bg-white/10 data-[state=open]:text-inverse'
                      : 'border-transparent bg-transparent text-foreground hover:bg-transparent hover:text-foreground data-[state=open]:border-transparent data-[state=open]:bg-transparent data-[state=open]:text-foreground',
                  )}
                >
                  Videojuegos
                </DropdownTrigger>
                <DropdownContent align="start">
                  {videojuegosItems.map((item) => (
                    <DropdownItem key={item.href} asChild>
                      <Link href={item.href}>
                        <span className="grid gap-1 py-2">
                          <span className="font-medium text-foreground">{item.label}</span>
                        </span>
                      </Link>
                    </DropdownItem>
                  ))}
                </DropdownContent>
              </Dropdown>

              {navigationItems.map((item) => (
                <NavLink
                  key={item.href}
                  href={item.href}
                  className={
                    integrated
                      ? 'text-inverse hover:bg-white/10 hover:text-inverse'
                      : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground'
                  }
                >
                  {item.label}
                </NavLink>
              ))}
            </nav>

            <div className="w-72 xl:w-80">
              <SearchBar
                size="sm"
                inputClassName={
                  integrated
                    ? 'border-white/15 bg-white/10 text-inverse placeholder:text-white/70 shadow-none hover:border-white/25 hover:bg-white/14 focus-visible:border-white/35'
                    : undefined
                }
              />
            </div>
          </div>

          <div className="hidden justify-self-end lg:flex">
            <AuthControl integrated={integrated} />
          </div>

          <button
            type="button"
            className={cn(
              'inline-flex min-h-[var(--target-min-size)] items-center rounded-pill px-4 text-sm font-semibold transition-colors lg:hidden',
              integrated
                ? 'border border-white/15 bg-white/10 text-inverse shadow-none hover:border-white/25 hover:bg-white/14'
                : 'border border-border bg-transparent text-foreground shadow-none hover:border-border-strong hover:bg-transparent',
            )}
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
            className={cn(
              'mt-4 grid gap-4 lg:hidden',
              integrated
                ? 'rounded-2xl bg-white/6 p-4 text-inverse backdrop-blur-sm'
                : 'border-t border-border pt-4',
            )}
          >
            <SearchBar
              onSearch={() => setIsMobileMenuOpen(false)}
              inputClassName={
                integrated
                  ? 'border-white/15 bg-white/10 text-inverse placeholder:text-white/70 shadow-none hover:border-white/25 hover:bg-white/14 focus-visible:border-white/35'
                  : undefined
              }
            />

            <div className="grid gap-2" role="navigation" aria-label="Navegacion movil">
              <span
                className={cn(
                  'px-2 text-xs font-semibold tracking-[0.08em] uppercase',
                  integrated ? 'text-white/70' : 'text-foreground',
                )}
              >
                Videojuegos
              </span>
              {videojuegosItems.map((item) => (
                <NavLink
                  key={item.href}
                  href={item.href}
                  stacked
                  className={
                    integrated
                      ? 'text-inverse hover:bg-white/10 hover:text-inverse'
                      : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground'
                  }
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
                  className={
                    integrated
                      ? 'text-inverse hover:bg-white/10 hover:text-inverse'
                      : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground'
                  }
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  {item.label}
                </NavLink>
              ))}
            </div>
            <div className={cn('grid gap-3', integrated ? 'pt-1' : 'border-t border-border pt-4')}>
              <AuthControl integrated={integrated} />
            </div>
          </div>
        ) : null}
      </Container>
    </header>
  );
}

export default Header;
