import Link from 'next/link';
import { Button } from '@/shared/components/ui/Button';
import { VideojuegoRouteState } from './VideojuegoRouteState';

export default function NotFound() {
  return (
    <VideojuegoRouteState
      title="No encontramos este videojuego"
      description="Puede que el identificador no exista, se haya retirado del catalogo o el enlace ya no sea valido."
      action={
        <>
          <Button asChild>
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
