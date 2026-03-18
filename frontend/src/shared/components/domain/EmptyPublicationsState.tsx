import type { ComponentProps } from 'react';
import { EmptyState } from '@/shared/components/ui/EmptyState';

type EmptyPublicationsStateProps = Omit<ComponentProps<typeof EmptyState>, 'icon'>;

function PublicationsIcon() {
  return (
    <svg aria-hidden="true" viewBox="0 0 24 24" className="size-7 fill-current">
      <path d="M6 4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9.5a2 2 0 0 0-.59-1.41l-3.5-3.5A2 2 0 0 0 14.5 4Zm0 2h8v3a1 1 0 0 0 1 1h3v8H6Zm2 5a1 1 0 0 0 0 2h8a1 1 0 1 0 0-2Zm0 4a1 1 0 0 0 0 2h5a1 1 0 1 0 0-2Z" />
    </svg>
  );
}

export function EmptyPublicationsState(props: EmptyPublicationsStateProps) {
  return <EmptyState icon={<PublicationsIcon />} {...props} />;
}
