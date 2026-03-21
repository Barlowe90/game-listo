'use client';

import axios from 'axios';
import { Eye, EyeOff } from 'lucide-react';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { authApi } from '@/features/auth/api/authApi';
import {
  PASSWORD_RULES_HELP_TEXT,
  getPasswordRuleErrorMessage,
} from '@/features/auth/passwordRules';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardBody } from '@/shared/components/ui/Card';
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';
import { PageContainer } from '@/shared/components/ui/PageContainer';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';

const RESEND_VERIFICATION_COOLDOWN_MS = 3 * 60 * 1000;

type ToastState = {
  title: string;
  description: string;
};

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

function getApiErrorMessage(error: unknown, fallbackMessage: string) {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    const responseData = error.response?.data;

    return responseData?.error ?? responseData?.message ?? fallbackMessage;
  }

  return fallbackMessage;
}

function formatCountdown(totalSeconds: number) {
  const minutes = Math.floor(totalSeconds / 60)
    .toString()
    .padStart(2, '0');
  const seconds = (totalSeconds % 60).toString().padStart(2, '0');

  return `${minutes}:${seconds}`;
}

export default function RegistroPage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isPasswordVisible, setIsPasswordVisible] = useState(false);
  const [passwordError, setPasswordError] = useState<string | null>(null);

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isResendingVerification, setIsResendingVerification] = useState(false);
  const [successToast, setSuccessToast] = useState<ToastState | null>(null);
  const [errorToast, setErrorToast] = useState<ToastState | null>(null);
  const [verificationEmail, setVerificationEmail] = useState<string | null>(null);
  const [resendAvailableAt, setResendAvailableAt] = useState<number | null>(null);
  const [currentTime, setCurrentTime] = useState(() => Date.now());

  useEffect(() => {
    if (!resendAvailableAt) {
      return;
    }

    setCurrentTime(Date.now());

    const intervalId = window.setInterval(() => {
      const nextCurrentTime = Date.now();
      setCurrentTime(nextCurrentTime);

      if (nextCurrentTime >= resendAvailableAt) {
        window.clearInterval(intervalId);
      }
    }, 1000);

    return () => {
      window.clearInterval(intervalId);
    };
  }, [resendAvailableAt]);

  const remainingSeconds =
    resendAvailableAt === null
      ? 0
      : Math.max(0, Math.ceil((resendAvailableAt - currentTime) / 1000));
  const canResendVerification = verificationEmail !== null && remainingSeconds === 0;

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    setSuccessToast(null);
    setErrorToast(null);
    setPasswordError(null);

    const nextPasswordError = getPasswordRuleErrorMessage(password);

    if (nextPasswordError) {
      setPasswordError(nextPasswordError);
      return;
    }

    setIsSubmitting(true);

    try {
      const registrationEmail = email.trim();

      await authApi.register({
        username,
        email: registrationEmail,
        password,
      });

      const now = Date.now();

      setSuccessToast({
        title: 'Cuenta creada',
        description: `Te hemos enviado un correo de verificación a ${registrationEmail}.`,
      });
      setVerificationEmail(registrationEmail);
      setResendAvailableAt(now + RESEND_VERIFICATION_COOLDOWN_MS);
      setCurrentTime(now);
      setUsername('');
      setEmail('');
      setPassword('');
      setPasswordError(null);
      setIsPasswordVisible(false);
    } catch (error: unknown) {
      if (axios.isAxiosError<ApiErrorResponse>(error)) {
        const passwordFieldError = error.response?.data?.errors?.password;

        if (passwordFieldError) {
          setPasswordError(passwordFieldError);
          return;
        }
      }

      setErrorToast({
        title: 'No se pudo completar el registro',
        description: getApiErrorMessage(
          error,
          'No se pudo completar el registro. Revisa los datos e inténtalo otra vez.',
        ),
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleResendVerification = async () => {
    if (!verificationEmail || !canResendVerification) {
      return;
    }

    setIsResendingVerification(true);
    setErrorToast(null);

    try {
      await authApi.resendVerification({
        email: verificationEmail,
      });

      const now = Date.now();

      setSuccessToast({
        title: 'Correo reenviado',
        description: `Te hemos reenviado el correo de verificación a ${verificationEmail}.`,
      });
      setResendAvailableAt(now + RESEND_VERIFICATION_COOLDOWN_MS);
      setCurrentTime(now);
    } catch (error: unknown) {
      setErrorToast({
        title: 'No se pudo reenviar el correo',
        description: getApiErrorMessage(
          error,
          'No se pudo reenviar el correo de verificación. Inténtalo otra vez.',
        ),
      });
    } finally {
      setIsResendingVerification(false);
    }
  };

  return (
    <PageContainer size="narrow" className="py-10 lg:py-12">
      <div className="grid gap-6">
        <SectionHeader title="Crear cuenta" />

        <Card>
          <CardBody className="gap-6 ">
            <form onSubmit={handleSubmit} className="grid gap-6">
              <FormField label="Usuario" htmlFor="username" required>
                <Input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(event) => setUsername(event.target.value)}
                  required
                  autoComplete="username"
                  placeholder="Tu usuario"
                />
              </FormField>

              <FormField label="Email" htmlFor="email" required>
                <Input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  required
                  autoComplete="email"
                  placeholder="tu@email.com"
                />
              </FormField>

              <FormField
                label="Contraseña"
                htmlFor="password"
                required
                helpText={PASSWORD_RULES_HELP_TEXT}
                errorMessage={passwordError}
              >
                <div className="relative">
                  <Input
                    id="password"
                    type={isPasswordVisible ? 'text' : 'password'}
                    value={password}
                    onChange={(event) => {
                      setPassword(event.target.value);
                      setPasswordError(null);
                      setErrorToast(null);
                    }}
                    required
                    autoComplete="new-password"
                    placeholder="Crea tu contraseña"
                    className="pr-12"
                    minLength={8}
                    state={passwordError ? 'error' : 'default'}
                  />
                  <button
                    type="button"
                    onClick={() => setIsPasswordVisible((currentValue) => !currentValue)}
                    aria-label={isPasswordVisible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                    aria-pressed={isPasswordVisible}
                    className="absolute top-1/2 right-3 inline-flex size-9 -translate-y-1/2 items-center justify-center rounded-full text-muted-foreground transition-colors hover:text-foreground focus-visible:outline-none focus-visible:text-foreground"
                  >
                    {isPasswordVisible ? (
                      <EyeOff className="size-5" aria-hidden="true" />
                    ) : (
                      <Eye className="size-5" aria-hidden="true" />
                    )}
                  </button>
                </div>
              </FormField>

              <Button type="submit" loading={isSubmitting} className="w-full text-white!">
                Registrarse
              </Button>
            </form>

            {successToast ? (
              <Toast
                variant="success"
                title={successToast.title}
                description={successToast.description}
              />
            ) : null}

            {errorToast ? (
              <Toast
                variant="error"
                title={errorToast.title}
                description={errorToast.description}
              />
            ) : null}

            {verificationEmail ? (
              <div className="grid gap-4 rounded-lg border border-border bg-surface p-4">
                <div className="grid gap-2">
                  <p className="text-sm font-semibold text-foreground">
                    Reenviar correo electrónico
                  </p>
                  <p className="text-sm leading-relaxed text-secondary">
                    Si no encuentras el email de verificación para{' '}
                    <span className="font-semibold text-foreground">{verificationEmail}</span>,
                    puedes solicitar uno nuevo desde aquí.
                  </p>
                  <p className="text-sm leading-relaxed text-secondary">
                    {canResendVerification
                      ? 'Ya puedes volver a enviarlo.'
                      : `Disponible de nuevo en ${formatCountdown(remainingSeconds)}.`}
                  </p>
                </div>

                <Button
                  type="button"
                  variant="secondary"
                  loading={isResendingVerification}
                  disabled={!canResendVerification}
                  onClick={handleResendVerification}
                  className="w-full sm:w-fit"
                >
                  {canResendVerification
                    ? 'Reenviar correo electrónico'
                    : `Reenviar correo electrónico (${formatCountdown(remainingSeconds)})`}
                </Button>
              </div>
            ) : null}

            <p className="text-sm text-secondary">
              Ya tienes cuenta?{' '}
              <Link href="/login" className="font-semibold text-primary hover:text-primary-hover">
                Ir al login
              </Link>
            </p>
          </CardBody>
        </Card>
      </div>
    </PageContainer>
  );
}
