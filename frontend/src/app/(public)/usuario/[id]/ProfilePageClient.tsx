'use client';

import axios from 'axios';
import Link from 'next/link';
import { useCallback, useEffect, useState, type FormEvent } from 'react';
import { authApi } from '@/features/auth/api/authApi';
import { getAccessToken } from '@/features/auth/api/authSessionBridge';
import { getUserById } from '@/features/auth/api/getUserById';
import { useAuth } from '@/features/auth/hooks/useAuth';
import type { User } from '@/features/auth/model/session.model';
import { getPasswordRuleErrorMessage } from '@/features/auth/passwordRules';
import { ProfileBibliotecaSection } from '@/features/biblioteca/components/ProfileBibliotecaSection';
import { socialApi } from '@/features/social/api/socialApi';
import { ProfileFriendsSection } from '@/features/social/components/ProfileFriendsSection';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { ProfilePublicSection } from './components/ProfilePublicSection';
import { ProfileSettingsSection } from './components/ProfileSettingsSection';
import { ProfileSidebar } from './components/ProfileSidebar';
import {
  PROFILE_SECTIONS,
  normalizeLanguage,
  type LanguageCode,
  type ProfileSectionKey,
} from './profilePage.shared';

interface ProfilePageClientProps {
  activeSection: ProfileSectionKey;
  profileUserId: string;
}

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
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

export function ProfilePageClient({ activeSection, profileUserId }: ProfilePageClientProps) {
  const { accessToken, setSession, status, user } = useAuth();
  const isOwnProfile = user?.id === profileUserId;
  const [profile, setProfile] = useState<User | null>(isOwnProfile ? user : null);
  const [avatarDraft, setAvatarDraft] = useState(user?.avatar ?? '');
  const [languageDraft, setLanguageDraft] = useState<LanguageCode | ''>(
    user?.language === 'ENG' ? 'ENG' : user?.language === 'ESP' ? 'ESP' : '',
  );
  const [emailDraft, setEmailDraft] = useState(user?.email ?? '');
  const [discordUserIdDraft, setDiscordUserIdDraft] = useState(user?.discordUserId ?? '');
  
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [avatarError, setAvatarError] = useState<string | null>(null);
  const [languageError, setLanguageError] = useState<string | null>(null);
  const [emailError, setEmailError] = useState<string | null>(null);
  const [emailSuccess, setEmailSuccess] = useState<string | null>(null);
  const [discordUserIdError, setDiscordUserIdError] = useState<string | null>(null);
  
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
  const [friendActionError, setFriendActionError] = useState<string | null>(null);
  const [friendActionSuccess, setFriendActionSuccess] = useState<string | null>(null);
  const [isFriend, setIsFriend] = useState(false);
  const [isLoadingFriendship, setIsLoadingFriendship] = useState(false);
  const [isUpdatingFriendship, setIsUpdatingFriendship] = useState(false);

  const applyProfileUpdate = useCallback(
    (nextProfile: User) => {
      setProfile(nextProfile);
      setAvatarDraft(nextProfile.avatar ?? '');
      setLanguageDraft(normalizeLanguage(nextProfile.language));
      setEmailDraft(nextProfile.email);
      setDiscordUserIdDraft(nextProfile.discordUserId ?? '');
      

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
    setProfile((currentProfile) => {
      if (currentProfile?.id === profileUserId) {
        return currentProfile;
      }

      return isOwnProfile ? (user ?? null) : null;
    });
  }, [isOwnProfile, profileUserId, user]);

  useEffect(() => {
    if (!isOwnProfile || !user) {
      return;
    }

    setProfile((currentProfile) => currentProfile ?? user);
    setAvatarDraft((currentValue) => currentValue || user.avatar || '');
    setLanguageDraft((currentValue) => currentValue || normalizeLanguage(user.language));
    setEmailDraft((currentEmail) => currentEmail || user.email);
    setDiscordUserIdDraft((currentValue) => currentValue || user.discordUserId || '');
  }, [isOwnProfile, user]);

  useEffect(() => {
    let ignore = false;

    async function loadProfile() {
      setIsLoadingProfile(true);
      setProfileError(null);

      try {
        const nextProfile =
          isOwnProfile && status === 'authenticated' && accessToken
            ? await authApi.me()
            : await getUserById(profileUserId);

        if (ignore) {
          return;
        }

        if (isOwnProfile && status === 'authenticated' && accessToken) {
          applyProfileUpdate(nextProfile);
        } else {
          setProfile(nextProfile);
        }
      } catch (error) {
        if (ignore) {
          return;
        }

        setProfileError(
          getApiErrorMessage(
            error,
            isOwnProfile ? 'No se pudo cargar tu perfil.' : 'No se pudo cargar este perfil.',
          ),
        );
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
  }, [accessToken, applyProfileUpdate, isOwnProfile, profileRefreshKey, profileUserId, status]);

  useEffect(() => {
    setFriendActionError(null);
    setFriendActionSuccess(null);

    if (status !== 'authenticated' || isOwnProfile) {
      setIsFriend(false);
      setIsLoadingFriendship(false);
      return;
    }

    let ignore = false;

    async function loadFriendship() {
      setIsLoadingFriendship(true);

      try {
        const friends = await socialApi.listFriends();

        if (ignore) {
          return;
        }

        setIsFriend(friends.some((friend) => friend.id === profileUserId));
      } catch (error) {
        if (ignore) {
          return;
        }

        setFriendActionError(
          getApiErrorMessage(error, 'No se pudo comprobar si este usuario ya es tu amigo.'),
        );
      } finally {
        if (!ignore) {
          setIsLoadingFriendship(false);
        }
      }
    }

    void loadFriendship();

    return () => {
      ignore = true;
    };
  }, [isOwnProfile, profileUserId, status]);

  const visibleProfile = profile ?? (isOwnProfile ? user : null);
  const availableSections = isOwnProfile ? PROFILE_SECTIONS : [];
  const resolvedActiveSection =
    !isOwnProfile && activeSection === 'ajustes' ? 'biblioteca' : activeSection;
  const profileUsername = visibleProfile?.username ?? (isOwnProfile ? 'Tu perfil' : 'Perfil');
  const profileAvatar = visibleProfile?.avatar ?? null;
  const currentAvatar = profile?.avatar ?? user?.avatar ?? '';
  const currentLanguage = normalizeLanguage(profile?.language ?? user?.language);
  const currentEmail = profile?.email ?? user?.email ?? '';
  const currentDiscordUserId = profile?.discordUserId ?? user?.discordUserId ?? '';
  const hasDiscordLinked = Boolean(currentDiscordUserId);
  const selectedLanguage = languageDraft || currentLanguage;
  const isProfileSettingsDirty =
    avatarDraft.trim() !== currentAvatar || selectedLanguage !== currentLanguage;
  const isEmailDirty = emailDraft.trim() !== '' && emailDraft.trim() !== currentEmail;
  const isDiscordDirty = discordUserIdDraft.trim() !== currentDiscordUserId;

  async function handleFriendAction() {
    if (status !== 'authenticated' || !visibleProfile || isOwnProfile) {
      return;
    }

    setFriendActionError(null);
    setFriendActionSuccess(null);
    setIsUpdatingFriendship(true);

    try {
      if (isFriend) {
        await socialApi.removeFriend(visibleProfile.id);
        setIsFriend(false);
        setFriendActionSuccess(`${visibleProfile.username} ya no forma parte de tus amigos.`);
      } else {
        await socialApi.addFriend(visibleProfile.id);
        setIsFriend(true);
        setFriendActionSuccess(`${visibleProfile.username} se ha agregado a tus amigos.`);
      }
    } catch (error) {
      setFriendActionError(
        getApiErrorMessage(
          error,
          isFriend ? 'No se pudo eliminar este amigo.' : 'No se pudo agregar este amigo.',
        ),
      );
    } finally {
      setIsUpdatingFriendship(false);
    }
  }

  async function handleProfileSettingsSubmit(event: FormEvent<HTMLFormElement>) {
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

  async function handleEmailSubmit(event: FormEvent<HTMLFormElement>) {
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

  async function handleDiscordSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setDiscordError(null);
    setDiscordSuccess(null);
    setDiscordUserIdError(null);

    const nextDiscordUserId = discordUserIdDraft.trim();

    if (!nextDiscordUserId) {
      setDiscordUserIdError('Introduce el ID de Discord.');
      return;
    }

    if (!isDiscordDirty) {
      setDiscordSuccess('No hay cambios pendientes en Discord.');
      return;
    }

    setIsSavingDiscord(true);

    try {
      const updatedProfile = await authApi.linkDiscord(nextDiscordUserId);
      applyProfileUpdate(updatedProfile);
      setDiscordSuccess(hasDiscordLinked ? 'Discord actualizado correctamente.' : 'Discord vinculado correctamente.');
    } catch (error) {
      if (axios.isAxiosError<ApiErrorResponse>(error)) {
        const fieldErrors = error.response?.data?.errors;

        setDiscordUserIdError(fieldErrors?.discordUserId ?? null);

        if (!fieldErrors?.discordUserId) {
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

  async function handlePasswordSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setPasswordError(null);
    setNewPasswordError(null);
    setPasswordSuccess(null);

    if (!currentPassword) {
      setPasswordError('Introduce tu contrasena actual.');
      return;
    }

    const nextPasswordError = getPasswordRuleErrorMessage(newPassword, 'La nueva contrasena');

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

  function handleProfileRetry() {
    setProfileRefreshKey((currentValue) => currentValue + 1);
  }

  function handleAvatarChange(value: string) {
    setAvatarDraft(value);
    setAvatarError(null);
    setProfileSettingsError(null);
    setProfileSettingsSuccess(null);
  }

  function handleLanguageChange(value: LanguageCode) {
    setLanguageDraft(value);
    setLanguageError(null);
    setProfileSettingsError(null);
    setProfileSettingsSuccess(null);
  }

  function handleEmailChange(value: string) {
    setEmailDraft(value);
    setEmailError(null);
    setEmailSuccess(null);
  }

  function handleCurrentPasswordChange(value: string) {
    setCurrentPassword(value);
    setPasswordError(null);
    setPasswordSuccess(null);
  }

  function handleNewPasswordChange(value: string) {
    setNewPassword(value);
    setNewPasswordError(null);
    setPasswordError(null);
    setPasswordSuccess(null);
  }



  function handleDiscordUserIdChange(value: string) {
    setDiscordUserIdDraft(value);
    setDiscordUserIdError(null);
    setDiscordError(null);
    setDiscordSuccess(null);
  }

  function renderProfileAction() {
    if (isOwnProfile) {
      return null;
    }

    if (status === 'loading') {
      return (
        <Button type="button" disabled loading>
          Comprobando amistad
        </Button>
      );
    }

    if (status !== 'authenticated') {
      return (
        <Button asChild>
          <Link href="/login">Iniciar sesion para agregar amigo</Link>
        </Button>
      );
    }

    if (!visibleProfile) {
      return null;
    }

    return (
      <Button
        type="button"
        variant={isFriend ? 'destructive' : 'primary'}
        loading={isLoadingFriendship || isUpdatingFriendship}
        disabled={isLoadingFriendship}
        onClick={() => void handleFriendAction()}
      >
        {isFriend ? 'Eliminar amigo' : 'Agregar amigo'}
      </Button>
    );
  }

  function renderSectionContent() {
    if (!isOwnProfile) {
      return (
        <ProfilePublicSection
          profileError={profileError}
          visibleProfile={visibleProfile}
          onRetry={handleProfileRetry}
        />
      );
    }

    switch (resolvedActiveSection) {
      case 'amigos':
        return <ProfileFriendsSection />;
      case 'ajustes':
        return (
          <ProfileSettingsSection
            status={status}
            visibleProfile={visibleProfile}
            profileError={profileError}
            isLoadingProfile={isLoadingProfile}
            avatarDraft={avatarDraft}
            avatarError={avatarError}
            selectedLanguage={selectedLanguage}
            languageError={languageError}
            isSavingProfileSettings={isSavingProfileSettings}
            isProfileSettingsDirty={isProfileSettingsDirty}
            profileSettingsError={profileSettingsError}
            profileSettingsSuccess={profileSettingsSuccess}
            onAvatarChange={handleAvatarChange}
            onLanguageChange={handleLanguageChange}
            onProfileSettingsSubmit={handleProfileSettingsSubmit}
            emailDraft={emailDraft}
            emailError={emailError}
            emailSuccess={emailSuccess}
            isSavingEmail={isSavingEmail}
            isEmailDirty={isEmailDirty}
            onEmailChange={handleEmailChange}
            onEmailSubmit={handleEmailSubmit}
            currentPassword={currentPassword}
            newPassword={newPassword}
            newPasswordError={newPasswordError}
            passwordError={passwordError}
            passwordSuccess={passwordSuccess}
            isSavingPassword={isSavingPassword}
            onCurrentPasswordChange={handleCurrentPasswordChange}
            onNewPasswordChange={handleNewPasswordChange}
            onPasswordSubmit={handlePasswordSubmit}
            discordUserIdDraft={discordUserIdDraft}
            discordUserIdError={discordUserIdError}
            discordError={discordError}
            discordSuccess={discordSuccess}
            isSavingDiscord={isSavingDiscord}
            isRemovingDiscord={isRemovingDiscord}
            hasDiscordLinked={hasDiscordLinked}
            isDiscordDirty={isDiscordDirty}
            onDiscordUserIdChange={handleDiscordUserIdChange}
            onDiscordSubmit={handleDiscordSubmit}
            onDiscordDelete={() => {
              void handleDiscordDelete();
            }}
          />
        );
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
          <ProfileSidebar
            availableSections={availableSections}
            resolvedActiveSection={resolvedActiveSection}
            isLoadingProfile={isLoadingProfile}
            isOwnProfile={isOwnProfile}
            profileUsername={profileUsername}
            profileAvatar={profileAvatar}
            profileError={profileError}
            visibleProfile={visibleProfile}
            friendActionError={friendActionError}
            friendActionSuccess={friendActionSuccess}
            profileAction={renderProfileAction()}
          />

          <div className="grid gap-6">{renderSectionContent()}</div>
        </div>
      </PageSection>
    </div>
  );
}
