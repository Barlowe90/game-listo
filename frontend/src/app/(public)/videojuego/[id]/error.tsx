'use client';

import Link from 'next/link';
import { Button } from '@/shared/components/ui/Button';
import { VideojuegoRouteState } from './VideojuegoRouteState';

export default function Error({
  error,
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  const description = error.message.includes('Catalog API request failed')
    ? 'Hubo un problema temporal al recuperar la ficha desde el catalogo. Puedes volver a intentarlo o regresar al listado.'
    : 'Se ha producido un error inesperado al cargar este videojuego. Puedes reintentar la carga o volver al catalogo.';

  return (
    <VideojuegoRouteState
      title="No hemos podido cargar este videojuego"
      description={description}
      action={
        <>
          <Button onClick={() => reset()}>Reintentar</Button>
          <Button asChild variant="secondary">
            <Link href="/catalogo">Volver al catalogo</Link>
          </Button>
        </>
      }
    />
  );
}
