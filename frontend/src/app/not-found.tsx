import Link from 'next/link';
import { AppRouteState } from '@/app/AppRouteState';
import { Button } from '@/shared/components/ui/Button';

export default function NotFound() {
  return (
    <AppRouteState
      title="No encontramos esta pagina"
      description="La URL puede ser incorrecta, la pagina haberse movido o el recurso ya no estar disponible."
      action={
        <>
          <Button asChild>
            <Link href="/catalogo">Explorar videojuegos</Link>
          </Button>
          <Button asChild variant="secondary">
            <Link href="/">Volver al inicio</Link>
          </Button>
        </>
      }
    />
  );
}
