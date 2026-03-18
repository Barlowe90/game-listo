'use client';

import Link from 'next/link';
import { useEffect, useId, useRef, useState, type FocusEvent } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { cn } from '@/lib/cn';
import { Avatar } from '@/shared/components/ui/Avatar';

interface AvatarUsuarioProps {
  integrated?: boolean;
}

const menuItemClassName =
  'flex min-h-[var(--target-min-size)] items-center rounded-xl px-3 text-sm font-medium text-secondary transition-colors duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:bg-surface hover:text-foreground focus-visible:bg-surface focus-visible:text-foreground focus-visible:outline-none';

export default function AvatarUsuario({ integrated = false }: AvatarUsuarioProps) {
  const { user, logout } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const menuId = useId();

  useEffect(() => {
    if (!isOpen) {
      return;
    }

    const handlePointerDown = (event: PointerEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        setIsOpen(false);
        containerRef.current?.querySelector<HTMLButtonElement>('button')?.focus();
      }
    };

    document.addEventListener('pointerdown', handlePointerDown);
    document.addEventListener('keydown', handleEscape);

    return () => {
      document.removeEventListener('pointerdown', handlePointerDown);
      document.removeEventListener('keydown', handleEscape);
    };
  }, [isOpen]);

  const closeMenu = () => {
    setIsOpen(false);
  };

  const handleBlur = (event: FocusEvent<HTMLDivElement>) => {
    const nextFocusedElement = event.relatedTarget as Node | null;

    if (!nextFocusedElement || !event.currentTarget.contains(nextFocusedElement)) {
      closeMenu();
    }
  };

  const handleLogout = async () => {
    closeMenu();
    await logout();
  };

  if (!user) return null;

  return (
    <div
      ref={containerRef}
      className="relative"
      onMouseEnter={() => setIsOpen(true)}
      onMouseLeave={closeMenu}
      onFocusCapture={() => setIsOpen(true)}
      onBlurCapture={handleBlur}
    >
      <button
        type="button"
        className="rounded-pill focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary/30"
        aria-expanded={isOpen}
        aria-controls={menuId}
        aria-haspopup="menu"
        aria-label={`Abrir menu de ${user.username}`}
        onClick={() => setIsOpen(true)}
      >
        <Avatar
          src={user.avatar}
          name={user.username}
          size="sm"
          aria-hidden="true"
          className={cn(
            'transition-[transform,border-color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)]',
            isOpen && 'border-border-strong shadow-overlay',
            integrated && 'border-white/15 bg-white text-primary shadow-none',
          )}
        />
      </button>

      <div
        className={cn(
          'absolute top-full right-0 z-50 pt-3 transition-[opacity,transform] duration-[var(--duration-fast)] ease-[var(--easing-standard)]',
          isOpen
            ? 'pointer-events-auto translate-y-0 opacity-100'
            : 'pointer-events-none -translate-y-1 opacity-0',
        )}
      >
        <div
          id={menuId}
          role="menu"
          aria-label="Menu de usuario"
          className={cn(
            'grid min-w-44 gap-1 rounded-2xl border border-border bg-card p-2 shadow-overlay text-black',
            integrated && 'border-white/15 bg-white/95 backdrop-blur-sm',
          )}
        >
          <Link
            href={`/usuario/${user.id}`}
            role="menuitem"
            className={menuItemClassName}
            onClick={closeMenu}
          >
            Mi perfil
          </Link>
          <button
            type="button"
            role="menuitem"
            className={menuItemClassName}
            onClick={() => void handleLogout()}
          >
            Salir
          </button>
        </div>
      </div>
    </div>
  );
}
