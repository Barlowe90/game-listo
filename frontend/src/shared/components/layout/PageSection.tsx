import type { HTMLAttributes, ReactNode } from 'react';
import { cva, type VariantProps } from 'class-variance-authority';
import { cn } from '@/lib/cn';
import { Container, type ContainerProps } from '@/shared/components/layout/Container';

const pageSectionVariants = cva('w-full', {
  variants: {
    spacing: {
      compact: 'py-8 lg:py-10',
      default: 'py-10 lg:py-12',
      hero: 'py-12 lg:py-16',
    },
    tone: {
      transparent: '',
      surface: 'bg-surface',
    },
  },
  defaultVariants: {
    spacing: 'default',
    tone: 'transparent',
  },
});

type PageSectionElement = 'section' | 'div';

export interface PageSectionProps
  extends VariantProps<typeof pageSectionVariants>,
    Omit<HTMLAttributes<HTMLElement>, 'children' | 'className'> {
  as?: PageSectionElement;
  children: ReactNode;
  className?: string;
  containerClassName?: string;
  size?: ContainerProps['size'];
  contained?: boolean;
}

export function PageSection({
  as,
  children,
  className,
  containerClassName,
  size = 'default',
  contained = true,
  spacing,
  tone,
  ...props
}: PageSectionProps) {
  const Component = as ?? 'section';

  return (
    <Component className={cn(pageSectionVariants({ spacing, tone }), className)} {...props}>
      {contained ? (
        <Container size={size} className={containerClassName}>
          {children}
        </Container>
      ) : (
        children
      )}
    </Component>
  );
}

