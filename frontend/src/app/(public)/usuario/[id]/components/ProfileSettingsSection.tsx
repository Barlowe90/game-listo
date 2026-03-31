import Link from 'next/link';
import type { FormEventHandler } from 'react';
import type { SessionStatus, User } from '@/features/auth/model/session.model';
import { PASSWORD_RULES_HELP_TEXT } from '@/features/auth/passwordRules';
import { cn } from '@/lib/cn';
import { InfoPanelCard } from '@/shared/components/domain/InfoPanelCard';
import { Button } from '@/shared/components/ui/Button';
import { FormField } from '@/shared/components/ui/FormField';
import { Input, inputVariants } from '@/shared/components/ui/Input';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';
import { SimpleStateCard, type LanguageCode } from '../profilePage.shared';

interface ProfileSettingsSectionProps {
  status: SessionStatus;
  visibleProfile: User | null;
  profileError: string | null;
  isLoadingProfile: boolean;
  avatarDraft: string;
  avatarError: string | null;
  selectedLanguage: LanguageCode;
  languageError: string | null;
  isSavingProfileSettings: boolean;
  isProfileSettingsDirty: boolean;
  profileSettingsError: string | null;
  profileSettingsSuccess: string | null;
  onAvatarChange: (value: string) => void;
  onLanguageChange: (value: LanguageCode) => void;
  onProfileSettingsSubmit: FormEventHandler<HTMLFormElement>;
  emailDraft: string;
  emailError: string | null;
  emailSuccess: string | null;
  isSavingEmail: boolean;
  isEmailDirty: boolean;
  onEmailChange: (value: string) => void;
  onEmailSubmit: FormEventHandler<HTMLFormElement>;
  currentPassword: string;
  newPassword: string;
  newPasswordError: string | null;
  passwordError: string | null;
  passwordSuccess: string | null;
  isSavingPassword: boolean;
  onCurrentPasswordChange: (value: string) => void;
  onNewPasswordChange: (value: string) => void;
  onPasswordSubmit: FormEventHandler<HTMLFormElement>;
  discordUserIdDraft: string;
  discordUserIdError: string | null;
  discordError: string | null;
  discordSuccess: string | null;
  isSavingDiscord: boolean;
  isRemovingDiscord: boolean;
  hasDiscordLinked: boolean;
  isDiscordDirty: boolean;
  onDiscordUserIdChange: (value: string) => void;
  onDiscordSubmit: FormEventHandler<HTMLFormElement>;
  onDiscordDelete: () => void;
}

function ProfileDetailsCard({
  avatarDraft,
  avatarError,
  isLoadingProfile,
  isProfileSettingsDirty,
  isSavingProfileSettings,
  languageError,
  onAvatarChange,
  onLanguageChange,
  onProfileSettingsSubmit,
  profileSettingsError,
  profileSettingsSuccess,
  selectedLanguage,
}: Readonly<
  Pick<
    ProfileSettingsSectionProps,
    | 'avatarDraft'
    | 'avatarError'
    | 'isLoadingProfile'
    | 'isProfileSettingsDirty'
    | 'isSavingProfileSettings'
    | 'languageError'
    | 'onAvatarChange'
    | 'onLanguageChange'
    | 'onProfileSettingsSubmit'
    | 'profileSettingsError'
    | 'profileSettingsSuccess'
    | 'selectedLanguage'
  >
>) {
  return (
    <InfoPanelCard title="Perfil" className="xl:col-span-2">
      <form className="grid gap-4" onSubmit={onProfileSettingsSubmit}>
        <div className="grid gap-4 xl:grid-cols-2">
          <FormField label="Avatar" htmlFor="profile-avatar" errorMessage={avatarError}>
            <Input
              id="profile-avatar"
              type="url"
              value={avatarDraft}
              onChange={(event) => onAvatarChange(event.target.value)}
              state={avatarError ? 'error' : 'default'}
            />
          </FormField>

          <FormField label="Idioma" htmlFor="profile-language" errorMessage={languageError}>
            <select
              id="profile-language"
              onChange={(event) => onLanguageChange(event.target.value as LanguageCode)}
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
  );
}

function ProfileEmailCard({
  emailDraft,
  emailError,
  emailSuccess,
  isEmailDirty,
  isLoadingProfile,
  isSavingEmail,
  onEmailChange,
  onEmailSubmit,
}: Readonly<
  Pick<
    ProfileSettingsSectionProps,
    | 'emailDraft'
    | 'emailError'
    | 'emailSuccess'
    | 'isEmailDirty'
    | 'isLoadingProfile'
    | 'isSavingEmail'
    | 'onEmailChange'
    | 'onEmailSubmit'
  >
>) {
  return (
    <InfoPanelCard title="Email">
      <form className="grid gap-4" onSubmit={onEmailSubmit}>
        <FormField label="Email" htmlFor="profile-email" required errorMessage={emailError}>
          <Input
            id="profile-email"
            type="email"
            value={emailDraft}
            onChange={(event) => onEmailChange(event.target.value)}
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
  );
}

function ProfilePasswordCard({
  currentPassword,
  isSavingPassword,
  newPassword,
  newPasswordError,
  onCurrentPasswordChange,
  onNewPasswordChange,
  onPasswordSubmit,
  passwordError,
  passwordSuccess,
}: Readonly<
  Pick<
    ProfileSettingsSectionProps,
    | 'currentPassword'
    | 'isSavingPassword'
    | 'newPassword'
    | 'newPasswordError'
    | 'onCurrentPasswordChange'
    | 'onNewPasswordChange'
    | 'onPasswordSubmit'
    | 'passwordError'
    | 'passwordSuccess'
  >
>) {
  return (
    <InfoPanelCard title="Contrasena">
      <form className="grid gap-4" onSubmit={onPasswordSubmit}>
        <FormField label="Contrasena actual" htmlFor="current-password" required>
          <Input
            id="current-password"
            type="password"
            value={currentPassword}
            onChange={(event) => onCurrentPasswordChange(event.target.value)}
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
            onChange={(event) => onNewPasswordChange(event.target.value)}
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
  );
}

function ProfileDiscordCard({
  discordError,
  discordSuccess,
  discordUserIdDraft,
  discordUserIdError,
  hasDiscordLinked,
  isDiscordDirty,
  isRemovingDiscord,
  isSavingDiscord,
  onDiscordDelete,
  onDiscordSubmit,
  onDiscordUserIdChange,
}: Readonly<
  Pick<
    ProfileSettingsSectionProps,
    | 'discordError'
    | 'discordSuccess'
    | 'discordUserIdDraft'
    | 'discordUserIdError'
    | 'hasDiscordLinked'
    | 'isDiscordDirty'
    | 'isRemovingDiscord'
    | 'isSavingDiscord'
    | 'onDiscordDelete'
    | 'onDiscordSubmit'
    | 'onDiscordUserIdChange'
  >
>) {
  return (
    <InfoPanelCard title="Discord" className="xl:col-span-2">
      <form className="grid gap-4" onSubmit={onDiscordSubmit}>
        <div className="grid gap-4 xl:grid-cols-2">

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
              onChange={(event) => onDiscordUserIdChange(event.target.value)}
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
            onClick={onDiscordDelete}
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
  );
}

export function ProfileSettingsSection({
  status,
  visibleProfile,
  profileError,
  isLoadingProfile,
  avatarDraft,
  avatarError,
  selectedLanguage,
  languageError,
  isSavingProfileSettings,
  isProfileSettingsDirty,
  profileSettingsError,
  profileSettingsSuccess,
  onAvatarChange,
  onLanguageChange,
  onProfileSettingsSubmit,
  emailDraft,
  emailError,
  emailSuccess,
  isSavingEmail,
  isEmailDirty,
  onEmailChange,
  onEmailSubmit,
  currentPassword,
  newPassword,
  newPasswordError,
  passwordError,
  passwordSuccess,
  isSavingPassword,
  onCurrentPasswordChange,
  onNewPasswordChange,
  onPasswordSubmit,
  discordUserIdDraft,
  discordUserIdError,
  discordError,
  discordSuccess,
  isSavingDiscord,
  isRemovingDiscord,
  hasDiscordLinked,
  isDiscordDirty,
  onDiscordUserIdChange,
  onDiscordSubmit,
  onDiscordDelete,
}: Readonly<ProfileSettingsSectionProps>) {
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
        <ProfileDetailsCard
          avatarDraft={avatarDraft}
          avatarError={avatarError}
          isLoadingProfile={isLoadingProfile}
          isProfileSettingsDirty={isProfileSettingsDirty}
          isSavingProfileSettings={isSavingProfileSettings}
          languageError={languageError}
          onAvatarChange={onAvatarChange}
          onLanguageChange={onLanguageChange}
          onProfileSettingsSubmit={onProfileSettingsSubmit}
          profileSettingsError={profileSettingsError}
          profileSettingsSuccess={profileSettingsSuccess}
          selectedLanguage={selectedLanguage}
        />

        <ProfileEmailCard
          emailDraft={emailDraft}
          emailError={emailError}
          emailSuccess={emailSuccess}
          isEmailDirty={isEmailDirty}
          isLoadingProfile={isLoadingProfile}
          isSavingEmail={isSavingEmail}
          onEmailChange={onEmailChange}
          onEmailSubmit={onEmailSubmit}
        />

        <ProfilePasswordCard
          currentPassword={currentPassword}
          isSavingPassword={isSavingPassword}
          newPassword={newPassword}
          newPasswordError={newPasswordError}
          onCurrentPasswordChange={onCurrentPasswordChange}
          onNewPasswordChange={onNewPasswordChange}
          onPasswordSubmit={onPasswordSubmit}
          passwordError={passwordError}
          passwordSuccess={passwordSuccess}
        />

        <ProfileDiscordCard
          discordError={discordError}
          discordSuccess={discordSuccess}
          discordUserIdDraft={discordUserIdDraft}
          discordUserIdError={discordUserIdError}
          hasDiscordLinked={hasDiscordLinked}
          isDiscordDirty={isDiscordDirty}
          isRemovingDiscord={isRemovingDiscord}
          isSavingDiscord={isSavingDiscord}
          onDiscordDelete={onDiscordDelete}
          onDiscordSubmit={onDiscordSubmit}
          onDiscordUserIdChange={onDiscordUserIdChange}
        />
      </div>
    </div>
  );
}
