'use client';

import axios from 'axios';
import { Eye, EyeOff, Gamepad2 } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { useAuth } from '@/features/auth/hooks/useAuth';
import { Container } from '@/shared/components/layout/Container';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardBody } from '@/shared/components/ui/Card';
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';
import { Toast } from '@/shared/components/ui/Toast';

export default function LoginPage() {
  const router = useRouter();
  const { login } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isPasswordVisible, setIsPasswordVisible] = useState(false);
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
            'No se pudo iniciar sesión. Revisa tus credenciales e inténtalo otra vez.',
        );
      } else {
        setErrorMessage('No se pudo iniciar sesión. Revisa tus credenciales e inténtalo otra vez.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex flex-1">
      <Container size="wide" className="flex flex-1 flex-col py-6 sm:py-8">
        <Link
          href="/"
          className="inline-flex items-center gap-3 self-start rounded-pill px-1 py-2 text-white transition-colors hover:text-white/90"
        >
          <span className="inline-flex size-10 items-center justify-center rounded-pill border border-white/15 bg-white/10 backdrop-blur-sm">
            <Gamepad2 className="size-5" aria-hidden="true" />
          </span>
          <span className="text-sm font-semibold tracking-[0.01em]">GameListo</span>
        </Link>

        <div className="flex flex-1 items-center justify-center py-8 sm:py-12">
          <Card className="w-full max-w-[32rem] rounded-[2rem] border border-white/10 bg-black/80 shadow-overlay backdrop-blur-sm">
            <CardBody className="gap-8 p-8 text-white sm:p-10">
              <div className="grid gap-2 text-center">
                <h1 className="text-3xl font-bold tracking-tight text-white sm:text-4xl">
                  ¡Hola de nuevo!
                </h1>
                <p className="text-sm text-white/70">Inicia sesión para volver a tu biblioteca.</p>
              </div>

              <form onSubmit={handleSubmit} className="grid gap-6">
                <FormField
                  label="Correo electrónico"
                  htmlFor="email"
                  required
                  className="[&_label]:text-white"
                >
                  <Input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    required
                    autoComplete="email"
                    placeholder="tu@email.com"
                    className="border-white/20 bg-white/10 text-white shadow-none placeholder:text-white/60 hover:border-white/30 focus-visible:border-white/45"
                  />
                </FormField>

                <FormField
                  label="Contraseña"
                  htmlFor="password"
                  required
                  className="[&_label]:text-white"
                >
                  <div className="relative">
                    <Input
                      id="password"
                      type={isPasswordVisible ? 'text' : 'password'}
                      value={password}
                      onChange={(event) => setPassword(event.target.value)}
                      required
                      autoComplete="current-password"
                      placeholder="Tu contraseña"
                      className="border-white/20 bg-white/10 pr-12 text-white shadow-none placeholder:text-white/60 hover:border-white/30 focus-visible:border-white/45"
                    />
                    <button
                      type="button"
                      onClick={() => setIsPasswordVisible((currentValue) => !currentValue)}
                      aria-label={isPasswordVisible ? 'Ocultar contraseña' : 'Mostrar contraseña'}
                      aria-pressed={isPasswordVisible}
                      className="absolute top-1/2 right-3 inline-flex size-9 -translate-y-1/2 items-center justify-center rounded-full text-white/60 transition-colors hover:text-white focus-visible:outline-none focus-visible:text-white"
                    >
                      {isPasswordVisible ? (
                        <EyeOff className="size-5" aria-hidden="true" />
                      ) : (
                        <Eye className="size-5" aria-hidden="true" />
                      )}
                    </button>
                  </div>
                </FormField>

                <div className="grid gap-5">
                  <Link
                    href="/recuperar-password"
                    className="text-xs font-medium text-primary! hover:text-primary-hover"
                  >
                    Has olvidado tu contraseña?
                  </Link>

                  <Button
                    type="submit"
                    loading={isSubmitting}
                    className="w-full text-white sm:mx-auto sm:min-w-40 sm:w-auto"
                  >
                    Entrar
                  </Button>
                </div>
              </form>

              {errorMessage ? (
                <Toast
                  variant="error"
                  title="No se pudo iniciar sesión"
                  description={errorMessage}
                  className="border-white/15 bg-white/10 text-white"
                />
              ) : null}

              <div className="text-sm text-white/70">
                Necesitas una cuenta?{' '}
                <Link
                  href="/registro"
                  className="font-semibold text-primary! hover:text-primary-hover"
                >
                  Crear cuenta
                </Link>
              </div>
            </CardBody>
          </Card>
        </div>
      </Container>
    </div>
  );
}
