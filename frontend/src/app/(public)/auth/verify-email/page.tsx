import axios from 'axios';
import Link from 'next/link';
import { authApi } from '@/features/auth/api/authApi';
import { Button } from '@/shared/components/ui/Button';
import { Card, CardBody } from '@/shared/components/ui/Card';
import { PageContainer } from '@/shared/components/ui/PageContainer';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';
import { Toast } from '@/shared/components/ui/Toast';

export const dynamic = 'force-dynamic';

type VerifyEmailPageProps = {
  searchParams?:
    | Promise<Record<string, string | string[] | undefined>>
    | Record<string, string | string[] | undefined>;
};

type VerificationResult = {
  status: 'success' | 'error';
  title: string;
  description: string;
};

async function verifyEmailToken(token: string): Promise<VerificationResult> {
  try {
    await authApi.verifyEmail({ token });

    return {
      status: 'success',
      title: 'Email verificado',
      description: 'Tu cuenta ya esta verificada. Ya puedes iniciar sesion.',
    };
  } catch (error) {
    return {
      status: 'error',
      title: 'No se pudo verificar el email',
      description:
        axios.isAxiosError<{ message?: string }>(error)
          ? error.response?.data?.message ?? 'El enlace de verificacion no es valido o ha expirado.'
          : 'No hemos podido conectar con el servicio de verificacion. Intentalo otra vez en unos minutos.',
    };
  }
}

export default async function VerifyEmailPage({ searchParams }: VerifyEmailPageProps) {
  const resolvedSearchParams = (await searchParams) ?? {};
  const tokenParam = resolvedSearchParams.token;
  const token = Array.isArray(tokenParam) ? tokenParam[0]?.trim() ?? '' : tokenParam?.trim() ?? '';

  const verificationResult = token
    ? await verifyEmailToken(token)
    : {
        status: 'error',
        title: 'Enlace invalido',
        description: 'Falta el token de verificacion en el enlace recibido por email.',
      };

  return (
    <PageContainer size="narrow" className="py-10 lg:py-12">
      <div className="grid gap-6">
        <SectionHeader title="Verificar email" />

        <Card>
          <CardBody className="gap-6">
            <Toast
              variant={verificationResult.status === 'success' ? 'success' : 'error'}
              title={verificationResult.title}
              description={verificationResult.description}
            />

            <div className="flex flex-wrap items-center gap-3">
              {verificationResult.status === 'success' ? (
                <Button asChild className="text-white!">
                  <Link href="/login">Ir al login</Link>
                </Button>
              ) : null}

              <Button asChild variant="secondary">
                <Link href="/">Volver al inicio</Link>
              </Button>
            </div>
          </CardBody>
        </Card>
      </div>
    </PageContainer>
  );
}
