import Link from 'next/link';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

interface PlaceholderAction {
  href: string;
  label: string;
  variant?: 'primary' | 'secondary' | 'ghost';
}

export interface PlaceholderPageProps {
  eyebrow: string;
  title: string;
  description: string;
  actions?: PlaceholderAction[];
}

export function PlaceholderPage({
  eyebrow,
  title,
  description,
  actions = [],
}: PlaceholderPageProps) {
  return (
    <PageSection>
      <Card variant="informative" padding="lg" className="grid gap-6">
        <SectionHeader
          eyebrow={eyebrow}
          title={title}
          subtitle={description}
          action={
            actions.length ? (
              <>
                {actions.map((action) => (
                  <Button key={action.href} asChild variant={action.variant ?? 'secondary'}>
                    <Link href={action.href}>{action.label}</Link>
                  </Button>
                ))}
              </>
            ) : null
          }
        />
      </Card>
    </PageSection>
  );
}
