'use client';

import axios from 'axios';
import Link from 'next/link';
import { useState } from 'react';
import { authApi } from '@/features/auth/api/authApi';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardBody } from '@/shared/components/ui/Card';
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';
import { PageContainer } from '@/shared/components/ui/PageContainer';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';

interface ApiErrorResponse {
  error?: string;
  errors?: Record<string, string>;
  message?: string;
}

interface ResetPasswordPageClientProps {
  initialEmail: string;
  token: string;
}

export function ResetPasswordPageClient({
  initialEmail,
  token,
}: ResetPasswordPageClientProps) {
  const [email, setEmail] = useState(initialEmail);
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    setSuccessMessage(null);
    setErrorMessage(null);

    if (!token) {
      setErrorMessage('Falta el token de restablecimiento.');
      return;
    }

    if (!email.trim()) {
      setErrorMessage('El email es obligatorio.');
      return;
    }

    if (newPassword.length < 8) {
      setErrorMessage('La nueva contrasena debe tener al menos 8 caracteres.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setErrorMessage('Las contrasenas no coinciden.');
      return;
    }

    setIsSubmitting(true);

    try {
      await authApi.resetPassword({
        email: email.trim(),
        nuevaContrasena: newPassword,
        token,
      });
      setNewPassword('');
      setConfirmPassword('');
      setSuccessMessage('Contrasena restablecida correctamente. Ya puedes iniciar sesion.');
    } catch (error) {
      if (axios.isAxiosError<ApiErrorResponse>(error)) {
        const fieldErrors = error.response?.data?.errors;

        setErrorMessage(
          fieldErrors?.email ??
            fieldErrors?.nuevaContrasena ??
            fieldErrors?.token ??
            error.response?.data?.error ??
            error.response?.data?.message ??
            'No se pudo restablecer la contrasena.',
        );
      } else {
        setErrorMessage('No se pudo restablecer la contrasena.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <PageContainer size="narrow" className="py-10 lg:py-12">
      <div className="grid gap-6">
        <SectionHeader title="Restablecer contrasena" />

        <Card>
          <CardBody className="gap-6">
            {!token ? (
              <Toast
                variant="error"
                title="Enlace invalido"
                description="No hemos recibido un token de restablecimiento valido."
              />
            ) : null}

            <form onSubmit={handleSubmit} className="grid gap-6">
              <FormField label="Email" htmlFor="reset-email" required>
                <Input
                  id="reset-email"
                  type="email"
                  value={email}
                  onChange={(event) => {
                    setEmail(event.target.value);
                    setErrorMessage(null);
                    setSuccessMessage(null);
                  }}
                  required
                  autoComplete="email"
                  placeholder="tu@email.com"
                  disabled={!token || isSubmitting}
                />
              </FormField>

              <FormField label="Nueva contrasena" htmlFor="reset-password" required>
                <Input
                  id="reset-password"
                  type="password"
                  value={newPassword}
                  onChange={(event) => {
                    setNewPassword(event.target.value);
                    setErrorMessage(null);
                    setSuccessMessage(null);
                  }}
                  required
                  autoComplete="new-password"
                  disabled={!token || isSubmitting}
                />
              </FormField>

              <FormField label="Confirmar contrasena" htmlFor="reset-password-confirm" required>
                <Input
                  id="reset-password-confirm"
                  type="password"
                  value={confirmPassword}
                  onChange={(event) => {
                    setConfirmPassword(event.target.value);
                    setErrorMessage(null);
                    setSuccessMessage(null);
                  }}
                  required
                  autoComplete="new-password"
                  disabled={!token || isSubmitting}
                />
              </FormField>

              <Button
                type="submit"
                loading={isSubmitting}
                className="w-full text-white!"
                disabled={!token}
              >
                Guardar nueva contrasena
              </Button>
            </form>

            {successMessage ? (
              <Toast variant="success" title="Contrasena actualizada" description={successMessage} />
            ) : null}

            {errorMessage ? <Toast variant="error" title={errorMessage} /> : null}

            <div className="flex flex-wrap items-center justify-between gap-3 text-sm text-secondary">
              <Link
                href="/recuperar-password"
                className="font-semibold text-primary hover:text-primary-hover"
              >
                Solicitar otro enlace
              </Link>

              <Link href="/login" className="font-semibold text-primary hover:text-primary-hover">
                Volver a login
              </Link>
            </div>
          </CardBody>
        </Card>
      </div>
    </PageContainer>
  );
}
