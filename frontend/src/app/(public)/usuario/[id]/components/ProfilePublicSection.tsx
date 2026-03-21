import type { User } from '@/features/auth/model/session.model';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { Button } from '@/shared/components/ui/Button';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';
import { SimpleStateCard } from '../profilePage.shared';

interface ProfilePublicSectionProps {
  profileError: string | null;
  visibleProfile: User | null;
  onRetry: () => void;
}

export function ProfilePublicSection({
  profileError,
  visibleProfile,
  onRetry,
}: Readonly<ProfilePublicSectionProps>) {
  if (profileError && !visibleProfile) {
    return (
      <div className="grid gap-6">
        <SectionHeader title="Perfil" action={<Button onClick={onRetry}>Reintentar</Button>} />

        <Toast variant="error" title={profileError} />

        <SimpleStateCard title="No pudimos cargar este perfil ahora mismo" />
      </div>
    );
  }

  return (
    <div className="grid gap-6">
      <SectionHeader title="Perfil" />

      <InfoPanelCard
        title="Informacion basica"
        description={
          visibleProfile
            ? `Puedes agregar a ${visibleProfile.username} como amigo desde la cabecera de esta ficha.`
            : 'Puedes agregar a este usuario como amigo desde la cabecera de esta ficha.'
        }
      />
    </div>
  );
}
