import { ExternalLink, MessageCircleMore } from 'lucide-react';
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

function buildDiscordProfileUrl(discordUserId: string) {
  return `https://discord.com/users/${discordUserId}`;
}

export function ProfilePublicSection({
  profileError,
  visibleProfile,
  onRetry,
}: Readonly<ProfilePublicSectionProps>) {
  const discordUserId = visibleProfile?.discordUserId?.trim() ?? '';
  const hasDiscordContact = discordUserId !== '';

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
            ? `Puedes agregar a ${visibleProfile.username} como amigo desde la cabecera de esta ficha. Si ha compartido su Discord, tambien podras abrirlo desde aqui.`
            : 'Puedes agregar a este usuario como amigo desde la cabecera de esta ficha y, si ha compartido su Discord, abrirlo desde aqui.'
        }
      />

      <InfoPanelCard
        title="Contacto por Discord"
        description={
          hasDiscordContact
            ? `Game Listo no incluye mensajeria interna, asi que puedes usar Discord para hablar con ${visibleProfile?.username ?? 'este usuario'}.`
            : `${visibleProfile?.username ?? 'Este usuario'} todavia no ha compartido un contacto de Discord publico.`
        }
      >
        {hasDiscordContact ? (
          <div className="flex flex-col gap-4 rounded-[calc(var(--radius-xl)+0.1rem)] bg-background p-4 sm:flex-row sm:items-center sm:justify-between">
            <div className="grid gap-1">
              <div className="flex items-center gap-2 text-sm font-semibold text-foreground">
                <MessageCircleMore className="size-4 text-primary" aria-hidden="true" />
                <span>Discord</span>
              </div>
              <p className="text-sm leading-relaxed text-secondary">
                Se abrira su perfil de Discord en una pestaña nueva.
              </p>
            </div>

            <Button asChild>
              <a
                href={buildDiscordProfileUrl(discordUserId)}
                target="_blank"
                rel="noreferrer"
              >
                <ExternalLink className="size-4" aria-hidden="true" />
                Abrir Discord
              </a>
            </Button>
          </div>
        ) : null}
      </InfoPanelCard>
    </div>
  );
}
