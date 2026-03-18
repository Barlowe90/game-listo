import type { HTMLAttributes, SVGProps } from 'react';
import Link from 'next/link';
import { cva } from 'class-variance-authority';
import { cn } from '@/lib/cn';
import { Button } from '@/shared/components/ui/Button';

export type GameActionKey = 'quiero' | 'tengo' | 'jugando' | 'jugado';

export interface GameActionItem {
  active?: boolean;
  href?: string;
  key: GameActionKey;
  label?: string;
}

export interface GameActionBarProps extends HTMLAttributes<HTMLDivElement> {
  actions: GameActionItem[];
  listAction?: {
    href: string;
    label?: string;
  };
}

const actionChipVariants = cva(
  'inline-flex min-h-[84px] min-w-[92px] flex-col justify-center gap-2 rounded-[calc(var(--radius-xl)+0.25rem)] border px-4 py-3 text-left text-sm font-semibold transition-colors',
  {
    variants: {
      active: {
        true: 'border-transparent bg-primary text-primary-foreground shadow-surface',
        false:
          'border-border bg-primary-soft/70 text-foreground hover:border-border-strong hover:bg-surface',
      },
    },
    defaultVariants: {
      active: false,
    },
  },
);

function HeartIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M12 20.5l-1.3-1.18C5.4 14.53 2 11.46 2 7.7A4.7 4.7 0 016.72 3 5.1 5.1 0 0112 6.09 5.1 5.1 0 0117.28 3 4.7 4.7 0 0122 7.7c0 3.76-3.4 6.83-8.7 11.62L12 20.5z"
      />
    </svg>
  );
}

function LibraryIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M6 5h4v14H6z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M14 5h4v14h-4z" />
      <path strokeLinecap="round" strokeLinejoin="round" d="M10 7.5h4" />
    </svg>
  );
}

function GamepadIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M7 9h10a4 4 0 013.9 4.9l-.56 2.47A2 2 0 0118.4 18H16a2 2 0 01-1.79-1.11l-.42-.84a2 2 0 00-1.79-1.11 2 2 0 00-1.79 1.11l-.42.84A2 2 0 018 18H5.6a2 2 0 01-1.95-1.54l-.55-2.47A4 4 0 017 9z"
      />
      <path strokeLinecap="round" strokeLinejoin="round" d="M8 12v3M6.5 13.5h3" />
      <circle cx="16.5" cy="12.5" r=".75" fill="currentColor" stroke="none" />
      <circle cx="18.5" cy="14.5" r=".75" fill="currentColor" stroke="none" />
    </svg>
  );
}

function CheckIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M5 12.5l4.2 4.2L19 7" />
    </svg>
  );
}

function PlusIcon(props: SVGProps<SVGSVGElement>) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" {...props}>
      <path strokeLinecap="round" strokeLinejoin="round" d="M12 5v14M5 12h14" />
    </svg>
  );
}

function getActionDefinition(action: GameActionItem) {
  switch (action.key) {
    case 'quiero':
      return {
        icon: HeartIcon,
        label: action.label ?? 'Quiero',
      };
    case 'tengo':
      return {
        icon: LibraryIcon,
        label: action.label ?? 'Tengo',
      };
    case 'jugando':
      return {
        icon: GamepadIcon,
        label: action.label ?? 'Jugando',
      };
    case 'jugado':
      return {
        icon: CheckIcon,
        label: action.label ?? 'Jugado',
      };
    default:
      return {
        icon: HeartIcon,
        label: action.label ?? 'Quiero',
      };
  }
}

export function GameActionBar({ actions, className, listAction, ...props }: GameActionBarProps) {
  return (
    <div className={cn('flex flex-wrap gap-3', className)} {...props}>
      {actions.map((action) => {
        const definition = getActionDefinition(action);
        const content = (
          <>
            <definition.icon className="size-5" aria-hidden="true" />
            <span>{definition.label}</span>
          </>
        );

        if (action.href) {
          return (
            <Link
              key={action.key}
              href={action.href}
              className={actionChipVariants({ active: action.active })}
            >
              {content}
            </Link>
          );
        }

        return (
          <button
            key={action.key}
            type="button"
            className={actionChipVariants({ active: action.active })}
            aria-pressed={action.active}
          >
            {content}
          </button>
        );
      })}

      {listAction ? (
        <Button
          asChild
          variant="secondary"
          className="rounded-[calc(var(--radius-xl)+0.25rem)] border-border bg-primary-soft/70 px-5 text-foreground shadow-none hover:border-border-strong hover:bg-surface"
        >
          <Link href={listAction.href}>
            <PlusIcon className="size-4" aria-hidden="true" />
            {listAction.label ?? 'Anadir a lista'}
          </Link>
        </Button>
      ) : null}
    </div>
  );
}
