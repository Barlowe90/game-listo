import type { HTMLAttributes } from 'react';
import { cn } from '@/lib/cn';
import { Avatar } from '@/shared/components/ui/Avatar';

export interface AvatarGroupMember {
  name: string;
  src?: string | null;
}

export interface AvatarGroupProps extends HTMLAttributes<HTMLDivElement> {
  max?: number;
  members: AvatarGroupMember[];
  size?: 'sm' | 'md' | 'lg';
}

export function AvatarGroup({
  className,
  max = 4,
  members,
  size = 'sm',
  ...props
}: AvatarGroupProps) {
  const visibleMembers = members.slice(0, max);
  const overflowCount = Math.max(members.length - visibleMembers.length, 0);

  return (
    <div className={cn('flex items-center gap-3', className)} {...props}>
      <div className="flex items-center">
        {visibleMembers.map((member, index) => (
          <Avatar
            key={`${member.name}-${index}`}
            name={member.name}
            src={member.src}
            size={size}
            className={cn(
              'border-2 border-background shadow-surface',
              index === 0 ? 'ml-0' : '-ml-3',
            )}
            title={member.name}
          />
        ))}
        {overflowCount ? (
          <span className="-ml-3 inline-flex size-10 items-center justify-center rounded-pill border-2 border-background bg-primary text-xs font-semibold text-primary-foreground shadow-surface">
            +{overflowCount}
          </span>
        ) : null}
      </div>
      <span className="text-sm leading-relaxed text-secondary">
        {members.length} jugador{members.length === 1 ? '' : 'es'}
      </span>
    </div>
  );
}
