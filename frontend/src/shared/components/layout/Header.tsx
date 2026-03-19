'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import AvatarUsuario from '@/features/auth/components/AvatarUsuario';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/cn';
import { Container } from '@/shared/components/layout/Container';
import { NavLink } from '@/shared/components/layout/NavLink';
import { SearchBar } from '@/shared/components/layout/SearchBar';
import { Skeleton } from '@/shared/components/ui/Skeleton';

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
    return <AvatarUsuario integrated={integrated} />;
  }

  return (
    <Link
      href="/login"
      className={cn(
        'inline-flex min-h-[var(--target-min-size)] items-center justify-center gap-2 rounded-md border px-4 text-sm font-semibold whitespace-nowrap transition-[background-color,border-color,color,box-shadow,opacity,transform] duration-[var(--duration-fast)] ease-[var(--easing-standard)] focus-visible:outline-none',
        integrated
          ? 'border-transparent bg-white text-primary! shadow-none hover:border-transparent hover:bg-white/90 active:bg-white/80 hover:!text-black focus-visible:!text-black'
          : 'border-transparent bg-black text-primary-foreground! shadow-none hover:bg-transparent hover:!text-black focus-visible:!text-black active:bg-transparent',
      )}
    >
      Iniciar sesion
    </Link>
  );
}

export interface HeaderProps {
  integrated?: boolean;
}

export function Header({ integrated = false }: HeaderProps) {
  const { isAuthenticated, isLoading, user } = useAuth();
  const pathname = usePathname();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const isVideojuegosActive = pathname === '/catalogo' || pathname.startsWith('/videojuego/');
  const userProfilePath = user ? `/usuario/${user.id}` : null;
  const bibliotecaHref =
    isAuthenticated && user
      ? `/usuario/${user.id}?seccion=biblioteca`
      : isLoading
        ? '/biblioteca'
        : '/login';
  const misPublicacionesHref =
    isAuthenticated && user ? '/mis-publicaciones' : isLoading ? '/mis-publicaciones' : '/login';
  const isBibliotecaActive =
    pathname === '/biblioteca' || (userProfilePath !== null && pathname === userProfilePath);
  const bibliotecaLinkClassName = cn(
    'inline-flex min-h-[var(--target-min-size)] items-center rounded-pill px-4 text-sm font-medium transition-colors focus-visible:outline-none',
    integrated
      ? isBibliotecaActive
        ? 'bg-primary-soft text-primary'
        : 'text-inverse hover:bg-white/10 hover:text-inverse'
      : isBibliotecaActive
        ? 'bg-primary-soft text-primary'
        : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground',
  );
  const bibliotecaMobileLinkClassName = cn(
    'inline-flex min-h-[var(--target-min-size)] w-full items-center justify-between rounded-pill px-4 text-sm font-medium transition-colors focus-visible:outline-none',
    integrated
      ? isBibliotecaActive
        ? 'bg-primary-soft text-primary'
        : 'text-inverse hover:bg-white/10 hover:text-inverse'
      : isBibliotecaActive
        ? 'bg-primary-soft text-primary'
        : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground',
  );

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
            <Image
              src="/logo-gamelist.svg"
              alt=""
              width={40}
              height={40}
              className={cn('h-10 w-10 shrink-0 object-contain', integrated && 'invert')}
            />
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
              <Link
                href="/catalogo"
                aria-current={isVideojuegosActive ? 'page' : undefined}
                className={cn(
                  'inline-flex min-h-[var(--target-min-size)] items-center rounded-pill px-4 text-sm font-medium transition-colors focus-visible:outline-none',
                  integrated
                    ? isVideojuegosActive
                      ? 'bg-primary-soft text-primary'
                      : 'text-inverse hover:bg-white/10 hover:text-inverse'
                    : isVideojuegosActive
                      ? 'bg-primary-soft text-primary'
                      : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground',
                )}
              >
                Videojuegos
              </Link>

              <Link
                href={bibliotecaHref}
                aria-current={isBibliotecaActive ? 'page' : undefined}
                className={bibliotecaLinkClassName}
              >
                Mi biblioteca
              </Link>
              <NavLink
                href={misPublicacionesHref}
                className={
                  integrated
                    ? 'text-inverse hover:bg-white/10 hover:text-inverse'
                    : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground'
                }
              >
                Mis publicaciones
              </NavLink>
            </nav>

            <div className="w-72 xl:w-80">
              <SearchBar
                size="sm"
                enableSuggestions
                resetOnPathnameChange
                inputClassName={cn(
                  'rounded-full',
                  integrated
                    ? 'border-white/15 bg-transparent text-inverse placeholder:text-white/70 shadow-none hover:border-white/25 hover:bg-white/14 focus-visible:border-white/35'
                    : undefined,
                )}
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
              enableSuggestions
              resetOnPathnameChange
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
              <Link
                href="/catalogo"
                aria-current={isVideojuegosActive ? 'page' : undefined}
                className={cn(
                  'inline-flex min-h-[var(--target-min-size)] w-full items-center justify-between rounded-pill px-4 text-sm font-medium transition-colors focus-visible:outline-none',
                  integrated
                    ? isVideojuegosActive
                      ? 'bg-primary-soft text-primary'
                      : 'text-inverse hover:bg-white/10 hover:text-inverse'
                    : isVideojuegosActive
                      ? 'bg-primary-soft text-primary'
                      : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground',
                )}
                onClick={() => setIsMobileMenuOpen(false)}
              >
                Videojuegos
              </Link>
              <Link
                href={bibliotecaHref}
                aria-current={isBibliotecaActive ? 'page' : undefined}
                className={bibliotecaMobileLinkClassName}
                onClick={() => setIsMobileMenuOpen(false)}
              >
                Mi biblioteca
              </Link>
              <NavLink
                href={misPublicacionesHref}
                stacked
                className={
                  integrated
                    ? 'text-inverse hover:bg-white/10 hover:text-inverse'
                    : 'bg-transparent text-foreground hover:bg-transparent hover:text-foreground'
                }
                onClick={() => setIsMobileMenuOpen(false)}
              >
                Mis publicaciones
              </NavLink>
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
