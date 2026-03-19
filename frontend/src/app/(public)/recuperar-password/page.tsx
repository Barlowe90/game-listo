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

export default function RecuperarPasswordPage() {
  const [email, setEmail] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    setIsSubmitting(true);
    setSuccessMessage(null);
    setErrorMessage(null);

    try {
      await authApi.forgotPassword({ email: email.trim() });
      setSuccessMessage(
        'Si existe una cuenta con ese email, te hemos enviado un enlace para restablecer la contrasena.',
      );
    } catch (error) {
      if (axios.isAxiosError<ApiErrorResponse>(error)) {
        setErrorMessage(
          error.response?.data?.errors?.email ??
            error.response?.data?.error ??
            error.response?.data?.message ??
            'No se pudo iniciar la recuperacion de contrasena.',
        );
      } else {
        setErrorMessage('No se pudo iniciar la recuperacion de contrasena.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <PageContainer size="narrow" className="py-10 lg:py-12">
      <div className="grid gap-6">
        <SectionHeader title="Recuperar contrasena" />

        <Card>
          <CardBody className="gap-6">
            <form onSubmit={handleSubmit} className="grid gap-6">
              <FormField label="Email" htmlFor="recovery-email" required>
                <Input
                  id="recovery-email"
                  type="email"
                  value={email}
                  onChange={(event) => {
                    setEmail(event.target.value);
                    setSuccessMessage(null);
                    setErrorMessage(null);
                  }}
                  required
                  autoComplete="email"
                  placeholder="tu@email.com"
                />
              </FormField>

              <Button type="submit" loading={isSubmitting} className="w-full text-white!">
                Enviar instrucciones
              </Button>
            </form>

            {successMessage ? <Toast title={successMessage} /> : null}

            {errorMessage ? <Toast variant="error" title={errorMessage} /> : null}

            <div className="flex flex-wrap items-center justify-between gap-3 text-sm text-secondary">
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
