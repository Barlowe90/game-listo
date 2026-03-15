'use client';

import axios from 'axios';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardBody } from '@/shared/components/ui/Card';
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';
import { PageContainer } from '@/shared/components/ui/PageContainer';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      await login(email, password);
      router.replace('/biblioteca');
    } catch (error: unknown) {
      if (axios.isAxiosError<{ message?: string }>(error)) {
        setErrorMessage(
          error.response?.data?.message ??
            'No se pudo iniciar sesion. Revisa tus credenciales e intentalo otra vez.',
        );
      } else {
        setErrorMessage('No se pudo iniciar sesion. Revisa tus credenciales e intentalo otra vez.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <PageContainer size="narrow" className="py-10 lg:py-12">
      <div className="grid gap-6">
        <SectionHeader
          eyebrow="Auth"
          title="Iniciar sesion"
          subtitle="El flujo de acceso ya reutiliza el wrapper oficial de formulario, las actions del sistema y feedback consistente."
        />

        <Card>
          <CardBody className="gap-6">
            <form onSubmit={handleSubmit} className="grid gap-6">
              <FormField
                label="Email"
                htmlFor="email"
                helpText="Usa el mismo email con el que te registraste."
                required
              >
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
                label="Contrasena"
                htmlFor="password"
                helpText="El foco visible y los estados ya salen de la capa atomica."
                required
              >
                <Input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(event) => setPassword(event.target.value)}
                  required
                  autoComplete="current-password"
                  placeholder="Tu contrasena"
                />
              </FormField>

              <Button type="submit" loading={isSubmitting} className="w-full">
                Entrar
              </Button>
            </form>

            {errorMessage ? (
              <Toast
                variant="error"
                title="No se pudo iniciar sesion"
                description={errorMessage}
              />
            ) : null}

            <div className="flex flex-wrap items-center justify-between gap-3 text-sm text-secondary">
              <Link
                href="/recuperar-password"
                className="font-semibold text-primary hover:text-primary-hover"
              >
                Has olvidado tu contrasena?
              </Link>
              <Link href="/registro" className="font-semibold text-primary hover:text-primary-hover">
                Crear cuenta
              </Link>
            </div>
          </CardBody>
        </Card>
      </div>
    </PageContainer>
  );
}
