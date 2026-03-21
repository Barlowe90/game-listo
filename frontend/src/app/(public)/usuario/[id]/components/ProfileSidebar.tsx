import type { ReactNode } from 'react';
import type { User } from '@/features/auth/model/session.model';
import { Avatar } from '@/shared/components/ui/Avatar';
import { Toast } from '@/shared/components/ui/Toast';
import { SidebarSectionLink, SurfaceCard, type ProfileSectionKey } from '../profilePage.shared';

interface ProfileSidebarProps {
  availableSections: ReadonlyArray<{
    key: ProfileSectionKey;
    label: string;
  }>;
  resolvedActiveSection: ProfileSectionKey;
  isLoadingProfile: boolean;
  isOwnProfile: boolean;
  profileUsername: string;
  profileAvatar: string | null;
  profileError: string | null;
  visibleProfile: User | null;
  friendActionError: string | null;
  friendActionSuccess: string | null;
  profileAction: ReactNode;
}

function ProfileSummaryCard({
  friendActionError,
  friendActionSuccess,
  isLoadingProfile,
  isOwnProfile,
  profileAction,
  profileAvatar,
  profileError,
  profileUsername,
  visibleProfile,
}: Readonly<
  Pick<
    ProfileSidebarProps,
    | 'friendActionError'
    | 'friendActionSuccess'
    | 'isLoadingProfile'
    | 'isOwnProfile'
    | 'profileAction'
    | 'profileAvatar'
    | 'profileError'
    | 'profileUsername'
    | 'visibleProfile'
  >
>) {
  return (
    <SurfaceCard>
      <div className="grid justify-items-center gap-4 p-6 text-center">
        {isLoadingProfile && !visibleProfile ? (
          <>
            <div className="size-24 animate-pulse rounded-full bg-primary-soft" />
            <div className="h-6 w-32 animate-pulse rounded-full bg-primary-soft" />
          </>
        ) : (
          <>
            <p className="text-xs font-semibold tracking-[0.12em] text-primary uppercase">
              {isOwnProfile ? 'Tu perfil' : 'Perfil de usuario'}
            </p>
            <Avatar
              name={profileUsername}
              src={profileAvatar}
              size="lg"
              className="size-24 text-2xl shadow-[0_20px_50px_rgba(59,99,183,0.18)]"
            />
            <h1 className="text-2xl font-semibold tracking-tight text-foreground">
              {profileUsername}
            </h1>
            {profileAction}
          </>
        )}

        {profileError && !visibleProfile ? <Toast variant="error" title={profileError} /> : null}
        {friendActionError ? <Toast variant="error" title={friendActionError} /> : null}
        {friendActionSuccess ? <Toast title={friendActionSuccess} /> : null}
      </div>
    </SurfaceCard>
  );
}

function ProfileSectionsCard({
  availableSections,
  resolvedActiveSection,
}: Readonly<Pick<ProfileSidebarProps, 'availableSections' | 'resolvedActiveSection'>>) {
  if (!availableSections.length) {
    return null;
  }

  return (
    <SurfaceCard>
      <div className="grid gap-3 p-4">
        <div className="grid gap-1 px-2 pb-1">
          <p className="text-xs font-semibold tracking-[0.12em] text-primary uppercase">
            Secciones
          </p>
        </div>

        <nav className="grid gap-2" aria-label="Secciones del perfil">
          {availableSections.map((section) => (
            <SidebarSectionLink
              key={section.key}
              href={`?seccion=${section.key}`}
              label={section.label}
              active={section.key === resolvedActiveSection}
            />
          ))}
        </nav>
      </div>
    </SurfaceCard>
  );
}

export function ProfileSidebar({
  availableSections,
  resolvedActiveSection,
  isLoadingProfile,
  isOwnProfile,
  profileUsername,
  profileAvatar,
  profileError,
  visibleProfile,
  friendActionError,
  friendActionSuccess,
  profileAction,
}: Readonly<ProfileSidebarProps>) {
  return (
    <div className="grid gap-5 md:grid-cols-[minmax(0,0.72fr)_minmax(0,1.28fr)] xl:sticky xl:top-24 xl:grid-cols-1">
      <ProfileSummaryCard
        friendActionError={friendActionError}
        friendActionSuccess={friendActionSuccess}
        isLoadingProfile={isLoadingProfile}
        isOwnProfile={isOwnProfile}
        profileAction={profileAction}
        profileAvatar={profileAvatar}
        profileError={profileError}
        profileUsername={profileUsername}
        visibleProfile={visibleProfile}
      />

      <ProfileSectionsCard
        availableSections={availableSections}
        resolvedActiveSection={resolvedActiveSection}
      />
    </div>
  );
}
