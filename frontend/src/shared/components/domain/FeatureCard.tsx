import type { HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { Card } from '@/shared/components/ui/Card';

export interface FeatureCardProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  description: ReactNode;
  icon: ReactNode;
  title: ReactNode;
}

export function FeatureCard({
  className,
  description,
  icon,
  title,
  ...props
}: FeatureCardProps) {
  return (
    <Card
      variant="home"
      padding="md"
      className={cn('h-full rounded-[calc(var(--radius-xl)+0.5rem)]', className)}
      {...props}
    >
      <div className="grid h-full gap-4">
        <div className="flex items-start gap-4">
          <span className="inline-flex size-12 shrink-0 items-center justify-center rounded-2xl bg-white/10 text-white">
            {icon}
          </span>
          <h2 className="text-lg font-semibold tracking-tight text-white">{title}</h2>
        </div>
        <p className="text-sm leading-relaxed text-white">{description}</p>
      </div>
    </Card>
  );
}
