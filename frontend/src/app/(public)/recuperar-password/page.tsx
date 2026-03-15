'use client';

import Link from 'next/link';
import { useState } from 'react';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardBody } from '@/shared/components/ui/Card';
import { FormField } from '@/shared/components/ui/FormField';
import { Input } from '@/shared/components/ui/Input';
import { PageContainer } from '@/shared/components/ui/PageContainer';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';

export default function RecuperarPasswordPage() {
  const [email, setEmail] = useState('');
  const [submitted, setSubmitted] = useState(false);

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();
    setSubmitted(true);
  };

  return (
    <PageContainer size="narrow" className="py-10 lg:py-12">
      <div className="grid gap-6">
        <SectionHeader
          eyebrow="Auth"
          title="Recuperar contrasena"
          subtitle="La estructura del formulario ya queda unificada con login y registro para cuando conectemos el flujo real."
        />

        <Card>
          <CardBody className="gap-6">
            <form onSubmit={handleSubmit} className="grid gap-6">
              <FormField
                label="Email"
                htmlFor="recovery-email"
                helpText="Te enviaremos instrucciones cuando el backend de recuperacion este conectado."
                required
              >
                <Input
                  id="recovery-email"
                  type="email"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  required
                  autoComplete="email"
                  placeholder="tu@email.com"
                />
              </FormField>

              <Button type="submit" className="w-full">
                Enviar instrucciones
              </Button>
            </form>

            {submitted ? (
              <Toast
                variant="success"
                title="Solicitud preparada"
                description="El patron de feedback ya esta listo para integrarse con el endpoint real."
              />
            ) : null}

            <div className="flex flex-wrap items-center justify-between gap-3 text-sm text-secondary">
              <Link href="/login" className="font-semibold text-primary hover:text-primary-hover">
                Volver a login
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
