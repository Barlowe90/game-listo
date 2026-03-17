import Link from 'next/link';
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
  const apiUrl = process.env.NEXT_PUBLIC_API_URL;

  if (!apiUrl) {
    return {
      status: 'error',
      title: 'Configuracion incompleta',
      description: 'Falta la variable NEXT_PUBLIC_API_URL en el frontend.',
    };
  }

  try {
    const response = await fetch(`${apiUrl}/v1/usuarios/auth/verify-email`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ token }),
      cache: 'no-store',
    });

    if (response.ok) {
      return {
        status: 'success',
        title: 'Email verificado',
        description: 'Tu cuenta ya esta verificada. Ya puedes iniciar sesion.',
      };
    }

    const errorData = (await response.json().catch(() => null)) as { message?: string } | null;

    return {
      status: 'error',
      title: 'No se pudo verificar el email',
      description:
        errorData?.message ?? 'El enlace de verificacion no es valido o ha expirado.',
    };
  } catch {
    return {
      status: 'error',
      title: 'No se pudo verificar el email',
      description:
        'No hemos podido conectar con el servicio de verificacion. Intentalo otra vez en unos minutos.',
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
