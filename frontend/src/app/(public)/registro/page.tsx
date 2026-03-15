'use client';

import axios from 'axios';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { authApi } from '@/features/auth/api/authApi';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardBody } from '@/shared/components/ui/Card';
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';
import { PageContainer } from '@/shared/components/ui/PageContainer';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';

export default function RegistroPage() {
  const router = useRouter();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = async (event) => {
    event.preventDefault();
    setIsSubmitting(true);
    setSuccessMessage(null);
    setErrorMessage(null);

    try {
      await authApi.register({
        username,
        email,
        password,
      });

      setSuccessMessage('Usuario registrado correctamente.');
      setUsername('');
      setEmail('');
      setPassword('');

      setTimeout(() => {
        router.replace('/login');
      }, 1000);
    } catch (error: unknown) {
      if (axios.isAxiosError<{ message?: string }>(error)) {
        setErrorMessage(
          error.response?.data?.message ??
            'No se pudo completar el registro. Revisa los datos e intentalo otra vez.',
        );
      } else {
        setErrorMessage('No se pudo completar el registro. Revisa los datos e intentalo otra vez.');
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
          title="Crear cuenta"
          subtitle="Registro construido ya como patron oficial del MVP: card, form field wrapper, acciones y feedback reutilizable."
        />

        <Card>
          <CardBody className="gap-6">
          <form onSubmit={handleSubmit} className="grid gap-6">
            <FormField
              label="Usuario"
              htmlFor="username"
              helpText="Este nombre se mostrara en tu perfil y publicaciones."
              required
            >
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

            <FormField
              label="Email"
              htmlFor="email"
              helpText="Usaremos este email para acceso y recuperacion."
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
              helpText="Elige una contrasena segura para tu cuenta."
              required
            >
              <Input
                id="password"
                type="password"
                value={password}
                onChange={(event) => setPassword(event.target.value)}
                required
                autoComplete="new-password"
                placeholder="Crea tu contrasena"
              />
            </FormField>

            <Button type="submit" loading={isSubmitting} className="w-full">
              Registrarse
            </Button>
          </form>

          {successMessage ? (
            <Toast variant="success" title="Cuenta creada" description={successMessage} />
          ) : null}

          {errorMessage ? (
            <Toast
              variant="error"
              title="No se pudo completar el registro"
              description={errorMessage}
            />
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
