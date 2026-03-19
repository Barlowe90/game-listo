'use client';

import axios from 'axios';
import Link from 'next/link';
import { useCallback, useEffect, useState } from 'react';
import { authApi } from '@/features/auth/api/authApi';
import { getAccessToken } from '@/features/auth/api/authSessionBridge';
import { useAuth } from '@/features/auth/hooks/useAuth';
import type { User } from '@/features/auth/model/session.model';
import {
  PASSWORD_RULES_HELP_TEXT,
  getPasswordRuleErrorMessage,
} from '@/features/auth/passwordRules';
import { ProfileBibliotecaSection } from '@/features/biblioteca/components/ProfileBibliotecaSection';
import { ProfileFriendsSection } from '@/features/social/components/ProfileFriendsSection';
import { cn } from '@/lib/cn';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Avatar } from '@/shared/components/ui/Avatar';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { FormField } from '@/shared/components/ui/FormField';
import { Input, inputVariants } from '@/shared/components/ui/Input';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';

const PROFILE_SECTIONS = [
  {
    key: 'biblioteca',
    label: 'Biblioteca',
  },
  {
    key: 'amigos',
    label: 'Amigos',
  },
  {
    key: 'ajustes',
    label: 'Ajustes',
  },
] as const;

type ProfileSectionKey = (typeof PROFILE_SECTIONS)[number]['key'];
type LanguageCode = 'ESP' | 'ENG';

interface ProfilePageClientProps {
  activeSection: ProfileSectionKey;
}

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

function SurfaceCard({
  children,
  className,
}: Readonly<{
  children: React.ReactNode;
  className?: string;
}>) {
  return (
    <Card
      className={cn(
        'rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
        className,
      )}
    >
      {children}
    </Card>
  );
}

function SidebarSectionLink({
  active,
  href,
  label,
}: Readonly<{
  active: boolean;
  href: string;
  label: string;
}>) {
  return (
    <Link
      href={href}
      aria-current={active ? 'page' : undefined}
      className={cn(
        'grid rounded-[calc(var(--radius-xl)+0.1rem)] border px-4 py-3 transition-[background-color,border-color,transform,box-shadow] duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:-translate-y-px',
        active
          ? 'border-primary/30 bg-primary-soft text-foreground shadow-surface'
          : 'border-border bg-white/70 text-secondary hover:border-border-strong hover:bg-white hover:text-foreground',
      )}
    >
      <span className="text-sm font-semibold">{label}</span>
    </Link>
  );
}

function getApiErrorMessage(error: unknown, fallback: string, field?: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    const responseData = error.response?.data;

    if (field && responseData?.errors?.[field]) {
      return responseData.errors[field];
    }

    return responseData?.error ?? responseData?.message ?? fallback;
  }

  return fallback;
}

function normalizeLanguage(value: string | null | undefined): LanguageCode {
  return value === 'ENG' ? 'ENG' : 'ESP';
}

function SimpleStateCard({
  action,
  title,
}: Readonly<{
  action?: React.ReactNode;
  title: string;
}>) {
  return (
    <SurfaceCard>
      <div className="grid justify-items-center gap-4 p-8 text-center">
        <h2 className="text-xl font-semibold tracking-tight text-foreground">{title}</h2>
        {action ? <div className="flex flex-wrap justify-center gap-3">{action}</div> : null}
      </div>
    </SurfaceCard>
  );
}

export function ProfilePageClient({ activeSection }: ProfilePageClientProps) {
  const { accessToken, setSession, status, user } = useAuth();
  const [profile, setProfile] = useState<User | null>(user);
  const [avatarDraft, setAvatarDraft] = useState(user?.avatar ?? '');
  const [languageDraft, setLanguageDraft] = useState<LanguageCode | ''>(
    user?.language === 'ENG' ? 'ENG' : user?.language === 'ESP' ? 'ESP' : '',
  );
  const [emailDraft, setEmailDraft] = useState(user?.email ?? '');
  const [discordUserIdDraft, setDiscordUserIdDraft] = useState(user?.discordUserId ?? '');
  const [discordUsernameDraft, setDiscordUsernameDraft] = useState(user?.discordUsername ?? '');
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [avatarError, setAvatarError] = useState<string | null>(null);
  const [languageError, setLanguageError] = useState<string | null>(null);
  const [emailError, setEmailError] = useState<string | null>(null);
  const [emailSuccess, setEmailSuccess] = useState<string | null>(null);
  const [discordUserIdError, setDiscordUserIdError] = useState<string | null>(null);
  const [discordUsernameError, setDiscordUsernameError] = useState<string | null>(null);
  const [discordError, setDiscordError] = useState<string | null>(null);
  const [discordSuccess, setDiscordSuccess] = useState<string | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [newPasswordError, setNewPasswordError] = useState<string | null>(null);
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [profileSettingsError, setProfileSettingsError] = useState<string | null>(null);
  const [profileSettingsSuccess, setProfileSettingsSuccess] = useState<string | null>(null);
  const [profileError, setProfileError] = useState<string | null>(null);
  const [isLoadingProfile, setIsLoadingProfile] = useState(false);
  const [isSavingProfileSettings, setIsSavingProfileSettings] = useState(false);
  const [isSavingDiscord, setIsSavingDiscord] = useState(false);
  const [isRemovingDiscord, setIsRemovingDiscord] = useState(false);
  const [isSavingEmail, setIsSavingEmail] = useState(false);
  const [isSavingPassword, setIsSavingPassword] = useState(false);
  const [profileRefreshKey, setProfileRefreshKey] = useState(0);

  const applyProfileUpdate = useCallback(
    (nextProfile: User) => {
      setProfile(nextProfile);
      setAvatarDraft(nextProfile.avatar ?? '');
      setLanguageDraft(normalizeLanguage(nextProfile.language));
      setEmailDraft(nextProfile.email);
      setDiscordUserIdDraft(nextProfile.discordUserId ?? '');
      setDiscordUsernameDraft(nextProfile.discordUsername ?? '');

      const latestAccessToken = getAccessToken();

      if (latestAccessToken) {
        setSession({
          status: 'authenticated',
          user: nextProfile,
          accessToken: latestAccessToken,
        });
      }
    },
    [setSession],
  );

  useEffect(() => {
    if (!user) {
      return;
    }

    setProfile((currentProfile) => currentProfile ?? user);
    setAvatarDraft((currentValue) => currentValue || user.avatar || '');
    setLanguageDraft((currentValue) => currentValue || normalizeLanguage(user.language));
    setEmailDraft((currentEmail) => currentEmail || user.email);
    setDiscordUserIdDraft((currentValue) => currentValue || user.discordUserId || '');
    setDiscordUsernameDraft((currentValue) => currentValue || user.discordUsername || '');
  }, [user]);

  useEffect(() => {
    if (status === 'anonymous') {
      setProfile(null);
      setEmailDraft('');
      setProfileError(null);
      setIsLoadingProfile(false);
      return;
    }

    if (status !== 'authenticated' || !accessToken) {
      setIsLoadingProfile(status === 'loading');
      return;
    }

    let ignore = false;

    async function loadProfile() {
      setIsLoadingProfile(true);
      setProfileError(null);

      try {
        const authenticatedUser = await authApi.me();

        if (ignore) {
          return;
        }

        applyProfileUpdate(authenticatedUser);
      } catch (error) {
        if (ignore) {
          return;
        }

        setProfileError(getApiErrorMessage(error, 'No se pudo cargar tu perfil.'));
      } finally {
        if (!ignore) {
          setIsLoadingProfile(false);
        }
      }
    }

    void loadProfile();

    return () => {
      ignore = true;
    };
  }, [accessToken, applyProfileUpdate, profileRefreshKey, status]);

  const visibleProfile = profile ?? user;
  const profileUsername = visibleProfile?.username ?? 'Tu perfil';
  const profileAvatar = visibleProfile?.avatar ?? null;
  const currentAvatar = profile?.avatar ?? user?.avatar ?? '';
  const currentLanguage = normalizeLanguage(profile?.language ?? user?.language);
  const currentEmail = profile?.email ?? user?.email ?? '';
  const currentDiscordUserId = profile?.discordUserId ?? user?.discordUserId ?? '';
  const currentDiscordUsername = profile?.discordUsername ?? user?.discordUsername ?? '';
  const hasDiscordLinked = Boolean(currentDiscordUserId || currentDiscordUsername);
  const selectedLanguage = languageDraft || currentLanguage;
  const isProfileSettingsDirty =
    avatarDraft.trim() !== currentAvatar || selectedLanguage !== currentLanguage;
  const isEmailDirty = emailDraft.trim() !== '' && emailDraft.trim() !== currentEmail;
  const isDiscordDirty =
    discordUserIdDraft.trim() !== currentDiscordUserId ||
    discordUsernameDraft.trim() !== currentDiscordUsername;

  async function handleProfileSettingsSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setAvatarError(null);
    setLanguageError(null);
    setProfileSettingsError(null);
    setProfileSettingsSuccess(null);

    const nextAvatar = avatarDraft.trim();
    const nextLanguage = selectedLanguage;

    if (nextAvatar.length > 500) {
      setAvatarError('La URL del avatar no puede exceder 500 caracteres.');
      return;
    }

    if (!isProfileSettingsDirty) {
      setProfileSettingsSuccess('No hay cambios pendientes en el perfil.');
      return;
    }

    setIsSavingProfileSettings(true);

    try {
      const updatedProfile = await authApi.editProfile({
        avatar: nextAvatar,
        language: nextLanguage,
      });
      applyProfileUpdate(updatedProfile);
      setProfileSettingsSuccess('Perfil actualizado correctamente.');
    } catch (error) {
      if (axios.isAxiosError<ApiErrorResponse>(error)) {
        const fieldErrors = error.response?.data?.errors;

        setAvatarError(fieldErrors?.avatar ?? null);
        setLanguageError(fieldErrors?.language ?? null);

        if (!fieldErrors?.avatar && !fieldErrors?.language) {
          setProfileSettingsError(getApiErrorMessage(error, 'No se pudo actualizar el perfil.'));
        }
      } else {
        setProfileSettingsError('No se pudo actualizar el perfil.');
      }
    } finally {
      setIsSavingProfileSettings(false);
    }
  }

  async function handleEmailSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const nextEmail = emailDraft.trim();
    setEmailError(null);
    setEmailSuccess(null);

    if (!nextEmail) {
      setEmailError('El email es obligatorio.');
      return;
    }

    if (!isEmailDirty) {
      setEmailSuccess('No hay cambios pendientes en el email.');
      return;
    }

    setIsSavingEmail(true);

    try {
      await authApi.changeEmail(nextEmail);

      const nextProfile = visibleProfile ? { ...visibleProfile, email: nextEmail } : null;

      if (nextProfile) {
        applyProfileUpdate(nextProfile);
      }

      setEmailSuccess('Email actualizado correctamente.');
      setProfileRefreshKey((currentValue) => currentValue + 1);
    } catch (error) {
      setEmailError(getApiErrorMessage(error, 'No se pudo actualizar el email.', 'email'));
    } finally {
      setIsSavingEmail(false);
    }
  }

  async function handleDiscordSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setDiscordError(null);
    setDiscordSuccess(null);
    setDiscordUserIdError(null);
    setDiscordUsernameError(null);

    const nextDiscordUserId = discordUserIdDraft.trim();
    const nextDiscordUsername = discordUsernameDraft.trim();

    if (!nextDiscordUserId) {
      setDiscordUserIdError('Introduce el ID de Discord.');
    }

    if (!nextDiscordUsername) {
      setDiscordUsernameError('Introduce el username de Discord.');
    }

    if (!nextDiscordUserId || !nextDiscordUsername) {
      return;
    }

    if (!isDiscordDirty) {
      setDiscordSuccess('No hay cambios pendientes en Discord.');
      return;
    }

    setIsSavingDiscord(true);

    try {
      const updatedProfile = await authApi.linkDiscord(nextDiscordUserId, nextDiscordUsername);
      applyProfileUpdate(updatedProfile);
      setDiscordSuccess(
        hasDiscordLinked
          ? 'Discord actualizado correctamente.'
          : 'Discord vinculado correctamente.',
      );
    } catch (error) {
      if (axios.isAxiosError<ApiErrorResponse>(error)) {
        const fieldErrors = error.response?.data?.errors;

        setDiscordUserIdError(fieldErrors?.discordUserId ?? null);
        setDiscordUsernameError(fieldErrors?.discordUsername ?? null);

        if (!fieldErrors?.discordUserId && !fieldErrors?.discordUsername) {
          setDiscordError(getApiErrorMessage(error, 'No se pudo guardar Discord.'));
        }
      } else {
        setDiscordError('No se pudo guardar Discord.');
      }
    } finally {
      setIsSavingDiscord(false);
    }
  }

  async function handleDiscordDelete() {
    setDiscordError(null);
    setDiscordSuccess(null);
    setDiscordUserIdError(null);
    setDiscordUsernameError(null);

    if (!hasDiscordLinked) {
      setDiscordSuccess('No hay ninguna cuenta de Discord vinculada.');
      return;
    }

    setIsRemovingDiscord(true);

    try {
      const updatedProfile = await authApi.unlinkDiscord();
      applyProfileUpdate(updatedProfile);
      setDiscordSuccess('Discord desvinculado correctamente.');
    } catch (error) {
      setDiscordError(getApiErrorMessage(error, 'No se pudo desvincular Discord.'));
    } finally {
      setIsRemovingDiscord(false);
    }
  }

  async function handlePasswordSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setPasswordError(null);
    setNewPasswordError(null);
    setPasswordSuccess(null);

    if (!currentPassword) {
      setPasswordError('Introduce tu contrasena actual.');
      return;
    }

    const nextPasswordError = getPasswordRuleErrorMessage(
      newPassword,
      'La nueva contrasena',
    );

    if (nextPasswordError) {
      setNewPasswordError(nextPasswordError);
      return;
    }

    setIsSavingPassword(true);

    try {
      await authApi.changePassword(currentPassword, newPassword);
      setCurrentPassword('');
      setNewPassword('');
      setNewPasswordError(null);
      setPasswordSuccess('Contrasena actualizada correctamente.');
    } catch (error) {
      if (axios.isAxiosError<ApiErrorResponse>(error)) {
        const fieldError = error.response?.data?.errors?.contrasenaNueva;

        if (fieldError) {
          setNewPasswordError(fieldError);
          return;
        }
      }

      setPasswordError(getApiErrorMessage(error, 'No se pudo actualizar la contrasena.'));
    } finally {
      setIsSavingPassword(false);
    }
  }

  function renderAjustesSection() {
    if (status !== 'authenticated' || !visibleProfile) {
      return (
        <div className="grid gap-6">
          <SectionHeader title="Ajustes" />
          <SimpleStateCard
            title="Necesitas iniciar sesion"
            action={
              <Button asChild>
                <Link href="/login">Iniciar sesion</Link>
              </Button>
            }
          />
        </div>
      );
    }

    return (
      <div className="grid gap-6">
        <SectionHeader title="Ajustes" />

        {profileError ? <Toast variant="error" title={profileError} /> : null}

        <div className="grid gap-5 xl:grid-cols-2">
          <InfoPanelCard title="Perfil" className="xl:col-span-2">
            <form className="grid gap-4" onSubmit={handleProfileSettingsSubmit}>
              <div className="grid gap-4 xl:grid-cols-2">
                <FormField
                  label="Avatar"
                  htmlFor="profile-avatar"
                  errorMessage={avatarError}
                >
                  <Input
                    id="profile-avatar"
                    type="url"
                    value={avatarDraft}
                    onChange={(event) => {
                      setAvatarDraft(event.target.value);
                      setAvatarError(null);
                      setProfileSettingsError(null);
                      setProfileSettingsSuccess(null);
                    }}
                    placeholder="https://..."
                    autoComplete="url"
                    disabled={isLoadingProfile || isSavingProfileSettings}
                    state={avatarError ? 'error' : 'default'}
                  />
                </FormField>

                <FormField
                  label="Idioma"
                  htmlFor="profile-language"
                  errorMessage={languageError}
                >
                  <select
                    id="profile-language"
                    value={selectedLanguage}
                    onChange={(event) => {
                      setLanguageDraft(event.target.value as LanguageCode);
                      setLanguageError(null);
                      setProfileSettingsError(null);
                      setProfileSettingsSuccess(null);
                    }}
                    className={cn(
                      inputVariants({ state: languageError ? 'error' : 'default' }),
                      'appearance-none',
                    )}
                    disabled={isLoadingProfile || isSavingProfileSettings}
                  >
                    <option value="ESP">Espanol</option>
                    <option value="ENG">English</option>
                  </select>
                </FormField>
              </div>

              <div className="flex justify-end">
                <Button
                  type="submit"
                  loading={isSavingProfileSettings}
                  disabled={!isProfileSettingsDirty}
                >
                  Guardar perfil
                </Button>
              </div>
            </form>

            {profileSettingsError ? <Toast variant="error" title={profileSettingsError} /> : null}

            {profileSettingsSuccess ? <Toast title={profileSettingsSuccess} /> : null}
          </InfoPanelCard>

          <InfoPanelCard title="Email">
            <form className="grid gap-4" onSubmit={handleEmailSubmit}>
              <FormField label="Email" htmlFor="profile-email" required errorMessage={emailError}>
                <Input
                  id="profile-email"
                  type="email"
                  value={emailDraft}
                  onChange={(event) => {
                    setEmailDraft(event.target.value);
                    setEmailError(null);
                    setEmailSuccess(null);
                  }}
                  placeholder="tu@email.com"
                  autoComplete="email"
                  state={emailError ? 'error' : 'default'}
                  disabled={isLoadingProfile || isSavingEmail}
                />
              </FormField>

              <div className="flex justify-end">
                <Button type="submit" loading={isSavingEmail} disabled={!isEmailDirty}>
                  Guardar email
                </Button>
              </div>
            </form>

            {emailSuccess ? <Toast title={emailSuccess} /> : null}
          </InfoPanelCard>

          <InfoPanelCard title="Contrasena">
            <form className="grid gap-4" onSubmit={handlePasswordSubmit}>
              <FormField label="Contrasena actual" htmlFor="current-password" required>
                <Input
                  id="current-password"
                  type="password"
                  value={currentPassword}
                  onChange={(event) => {
                    setCurrentPassword(event.target.value);
                    setPasswordError(null);
                    setPasswordSuccess(null);
                  }}
                  autoComplete="current-password"
                  disabled={isSavingPassword}
                />
              </FormField>

              <FormField
                label="Nueva contrasena"
                htmlFor="new-password"
                required
                helpText={PASSWORD_RULES_HELP_TEXT}
                errorMessage={newPasswordError}
              >
                <Input
                  id="new-password"
                  type="password"
                  value={newPassword}
                  onChange={(event) => {
                    setNewPassword(event.target.value);
                    setNewPasswordError(null);
                    setPasswordError(null);
                    setPasswordSuccess(null);
                  }}
                  autoComplete="new-password"
                  disabled={isSavingPassword}
                  minLength={8}
                  state={newPasswordError ? 'error' : 'default'}
                />
              </FormField>

              <div className="flex justify-end">
                <Button type="submit" loading={isSavingPassword}>
                  Guardar contrasena
                </Button>
              </div>
            </form>

            {passwordError ? <Toast variant="error" title={passwordError} /> : null}

            {passwordSuccess ? <Toast title={passwordSuccess} /> : null}
          </InfoPanelCard>

          <InfoPanelCard title="Discord" className="xl:col-span-2">
            <form className="grid gap-4" onSubmit={handleDiscordSubmit}>
              <div className="grid gap-4 xl:grid-cols-2">
                <FormField
                  label="Discord username"
                  htmlFor="discord-username"
                  required
                  errorMessage={discordUsernameError}
                >
                  <Input
                    id="discord-username"
                    type="text"
                    value={discordUsernameDraft}
                    onChange={(event) => {
                      setDiscordUsernameDraft(event.target.value);
                      setDiscordUsernameError(null);
                      setDiscordError(null);
                      setDiscordSuccess(null);
                    }}
                    placeholder="tu_usuario"
                    disabled={isSavingDiscord || isRemovingDiscord}
                    state={discordUsernameError ? 'error' : 'default'}
                  />
                </FormField>

                <FormField
                  label="Discord ID"
                  htmlFor="discord-user-id"
                  required
                  errorMessage={discordUserIdError}
                >
                  <Input
                    id="discord-user-id"
                    type="text"
                    value={discordUserIdDraft}
                    onChange={(event) => {
                      setDiscordUserIdDraft(event.target.value);
                      setDiscordUserIdError(null);
                      setDiscordError(null);
                      setDiscordSuccess(null);
                    }}
                    placeholder="123456789012345678"
                    disabled={isSavingDiscord || isRemovingDiscord}
                    state={discordUserIdError ? 'error' : 'default'}
                  />
                </FormField>
              </div>

              <div className="flex flex-wrap justify-end gap-3">
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => void handleDiscordDelete()}
                  loading={isRemovingDiscord}
                  disabled={!hasDiscordLinked || isSavingDiscord}
                >
                  Eliminar Discord
                </Button>
                <Button type="submit" loading={isSavingDiscord} disabled={!isDiscordDirty}>
                  Guardar Discord
                </Button>
              </div>
            </form>

            {discordError ? <Toast variant="error" title={discordError} /> : null}

            {discordSuccess ? <Toast title={discordSuccess} /> : null}
          </InfoPanelCard>
        </div>
      </div>
    );
  }

  function renderSectionContent() {
    switch (activeSection) {
      case 'amigos':
        return <ProfileFriendsSection />;
      case 'ajustes':
        return renderAjustesSection();
      case 'biblioteca':
      default:
        return <ProfileBibliotecaSection />;
    }
  }

  return (
    <div className="relative overflow-hidden bg-[radial-gradient(circle_at_top_left,#f8f9ff_0%,#eef0ff_42%,#e7e7fb_100%)]">
      <div className="pointer-events-none absolute left-[-8rem] top-14 h-72 w-72 rounded-full bg-white/40 blur-3xl" />
      <div className="pointer-events-none absolute right-[-6rem] top-40 h-80 w-80 rounded-full bg-primary-soft blur-3xl" />

      <PageSection size="wide" className="relative z-10 py-10 lg:py-14">
        <div className="grid gap-6 xl:grid-cols-[18rem_minmax(0,1fr)] xl:items-start">
          <div className="grid gap-5 md:grid-cols-[minmax(0,0.72fr)_minmax(0,1.28fr)] xl:sticky xl:top-24 xl:grid-cols-1">
            <SurfaceCard>
              <div className="grid justify-items-center gap-4 p-6 text-center">
                {isLoadingProfile && !visibleProfile ? (
                  <>
                    <div className="size-24 animate-pulse rounded-full bg-primary-soft" />
                    <div className="h-6 w-32 animate-pulse rounded-full bg-primary-soft" />
                  </>
                ) : (
                  <>
                    <Avatar
                      name={profileUsername}
                      src={profileAvatar}
                      size="lg"
                      className="size-24 text-2xl shadow-[0_20px_50px_rgba(59,99,183,0.18)]"
                    />
                    <h1 className="text-2xl font-semibold tracking-tight text-foreground">
                      {profileUsername}
                    </h1>
                  </>
                )}
              </div>
            </SurfaceCard>

            <SurfaceCard>
              <div className="grid gap-3 p-4">
                <div className="grid gap-1 px-2 pb-1">
                  <p className="text-xs font-semibold tracking-[0.12em] text-primary uppercase">
                    Secciones
                  </p>
                </div>

                <nav className="grid gap-2" aria-label="Secciones del perfil">
                  {PROFILE_SECTIONS.map((section) => (
                    <SidebarSectionLink
                      key={section.key}
                      href={`?seccion=${section.key}`}
                      label={section.label}
                      active={section.key === activeSection}
                    />
                  ))}
                </nav>
              </div>
            </SurfaceCard>
          </div>

          <div className="grid gap-6">{renderSectionContent()}</div>
        </div>
      </PageSection>
    </div>
  );
}
