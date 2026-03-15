'use client';

import * as React from 'react';
import * as DropdownMenu from '@radix-ui/react-dropdown-menu';
import { cn } from '@/lib/cn';

export const Dropdown = DropdownMenu.Root;
export const DropdownGroup = DropdownMenu.Group;
export const DropdownPortal = DropdownMenu.Portal;
export const DropdownSeparator = React.forwardRef<
  React.ElementRef<typeof DropdownMenu.Separator>,
  React.ComponentPropsWithoutRef<typeof DropdownMenu.Separator>
>(({ className, ...props }, ref) => (
  <DropdownMenu.Separator ref={ref} className={cn('my-2 h-px bg-border', className)} {...props} />
));

DropdownSeparator.displayName = DropdownMenu.Separator.displayName;

export const DropdownTrigger = React.forwardRef<
  React.ElementRef<typeof DropdownMenu.Trigger>,
  React.ComponentPropsWithoutRef<typeof DropdownMenu.Trigger>
>(({ className, children, ...props }, ref) => (
  <DropdownMenu.Trigger
    ref={ref}
    className={cn(
      'inline-flex min-h-[var(--target-min-size)] items-center gap-2 rounded-pill border border-transparent px-4 text-sm font-medium text-secondary transition-[background-color,border-color,color,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:bg-surface hover:text-foreground data-[state=open]:border-border data-[state=open]:bg-surface data-[state=open]:text-foreground',
      className,
    )}
    {...props}
  >
    {children}
    <svg aria-hidden="true" viewBox="0 0 24 24" className="size-4 fill-current">
      <path d="M12 15.5a1 1 0 0 1-.71-.29l-5-5a1 1 0 1 1 1.42-1.42L12 13.09l4.29-4.3a1 1 0 1 1 1.42 1.42l-5 5A1 1 0 0 1 12 15.5Z" />
    </svg>
  </DropdownMenu.Trigger>
));

DropdownTrigger.displayName = DropdownMenu.Trigger.displayName;

export const DropdownContent = React.forwardRef<
  React.ElementRef<typeof DropdownMenu.Content>,
  React.ComponentPropsWithoutRef<typeof DropdownMenu.Content>
>(({ className, sideOffset = 10, ...props }, ref) => (
  <DropdownPortal>
    <DropdownMenu.Content
      ref={ref}
      sideOffset={sideOffset}
      className={cn(
        'z-50 min-w-56 rounded-xl border border-border bg-card p-2 shadow-overlay outline-none',
        className,
      )}
      {...props}
    />
  </DropdownPortal>
));

DropdownContent.displayName = DropdownMenu.Content.displayName;

export const DropdownLabel = React.forwardRef<
  React.ElementRef<typeof DropdownMenu.Label>,
  React.ComponentPropsWithoutRef<typeof DropdownMenu.Label>
>(({ className, ...props }, ref) => (
  <DropdownMenu.Label
    ref={ref}
    className={cn('px-3 py-2 text-xs font-semibold tracking-[0.08em] text-muted-foreground uppercase', className)}
    {...props}
  />
));

DropdownLabel.displayName = DropdownMenu.Label.displayName;

export const DropdownItem = React.forwardRef<
  React.ElementRef<typeof DropdownMenu.Item>,
  React.ComponentPropsWithoutRef<typeof DropdownMenu.Item>
>(({ className, ...props }, ref) => (
  <DropdownMenu.Item
    ref={ref}
    className={cn(
      'flex min-h-[var(--target-min-size)] items-center gap-3 rounded-lg px-3 text-sm text-secondary outline-none transition-colors duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:bg-surface hover:text-foreground focus:bg-surface focus:text-foreground data-[disabled]:pointer-events-none data-[disabled]:opacity-[var(--opacity-disabled)]',
      className,
    )}
    {...props}
  />
));

DropdownItem.displayName = DropdownMenu.Item.displayName;
