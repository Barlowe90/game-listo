import type { ComponentProps } from 'react';
import { FileText } from 'lucide-react';
import { EmptyState } from '@/shared/components/ui/EmptyState';

type EmptyPublicationsStateProps = Omit<ComponentProps<typeof EmptyState>, 'icon'>;

export function EmptyPublicationsState(props: EmptyPublicationsStateProps) {
  return <EmptyState icon={<FileText aria-hidden="true" className="size-7" />} {...props} />;
}
