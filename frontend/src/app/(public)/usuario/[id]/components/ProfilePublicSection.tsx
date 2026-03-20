import type { User } from '@/features/auth/model/session.model';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { Button } from '@/shared/components/ui/Button';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';
import { SimpleStateCard, getSectionLabel, type ProfileSectionKey } from '../profilePage.shared';

interface ProfilePublicSectionProps {
  profileError: string | null;
  resolvedActiveSection: ProfileSectionKey;
  visibleProfile: User | null;
  onRetry: () => void;
}

export function ProfilePublicSection({
  profileError,
  resolvedActiveSection,
  visibleProfile,
  onRetry,
}: Readonly<ProfilePublicSectionProps>) {
  const sectionTitle = getSectionLabel(resolvedActiveSection);

  if (profileError && !visibleProfile) {
    return (
      <div className="grid gap-6">
        <SectionHeader
          title={sectionTitle}
          action={<Button onClick={onRetry}>Reintentar</Button>}
        />

        <Toast variant="error" title={profileError} />

        <SimpleStateCard title="No pudimos cargar este perfil ahora mismo" />
      </div>
    );
  }

  return (
    <div className="grid gap-6">
      <SectionHeader title={sectionTitle} />

      <InfoPanelCard
        title="Perfil publico"
        description={
          visibleProfile
            ? `Puedes gestionar la amistad con ${visibleProfile.username} desde la cabecera del perfil.`
            : 'Puedes gestionar la amistad desde la cabecera del perfil.'
        }
      />
    </div>
  );
}
