'use client';

import Link from 'next/link';
import { AppRouteState } from '@/app/AppRouteState';
import { Button } from '@/shared/components/ui/Button';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  const description = error.message.includes('Catalog API request failed')
    ? 'Hemos tenido un problema temporal al recuperar el contenido desde el catalogo. Puedes volver a intentarlo o regresar a una ruta segura.'
    : 'Se ha producido un error inesperado mientras cargabamos esta pagina. Puedes reintentar la carga o volver al inicio.';

  return (
    <AppRouteState
      title="No hemos podido completar esta pagina"
      description={description}
      action={
        <>
          <Button onClick={() => reset()}>Reintentar</Button>
          <Button asChild variant="secondary">
            <Link href="/catalogo">Ir al catalogo</Link>
          </Button>
          <Button asChild variant="secondary">
            <Link href="/">Volver al inicio</Link>
          </Button>
        </>
      }
    />
  );
}
