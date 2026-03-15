import Link from 'next/link';
import { Grid } from '@/shared/components/layout/Grid';
import { PageSection } from '@/shared/components/layout/PageSection';
import { Button } from '@/shared/components/ui/Button';
import { Card } from '@/shared/components/ui/Card';
import { EmptyState } from '@/shared/components/ui/EmptyState';
import { SectionHeader } from '@/shared/components/ui/SectionHeader';

export default function BibliotecaPage() {
  return (
    <>
      <PageSection>
        <div className="grid gap-8">
          <SectionHeader
            eyebrow="Privado"
            title="Tu biblioteca ya tiene patrones listos para crecer"
            subtitle="La vista privada reutiliza cards informativas, secciones y estados vacios sin necesitar estructuras ad hoc."
            action={
              <Button asChild>
                <Link href="/catalogo">Explorar catalogo</Link>
              </Button>
            }
          />

          <Grid variant="stats">
            <Card variant="informative" padding="md" className="grid gap-2">
              <span className="text-sm font-medium text-muted-foreground">Jugando ahora</span>
              <strong className="text-2xl font-bold tracking-tight text-foreground">0</strong>
              <p className="text-sm leading-relaxed text-secondary">
                Cuando conectemos datos reales, esta tarjeta podra resumir tu estado activo.
              </p>
            </Card>

            <Card variant="informative" padding="md" className="grid gap-2">
              <span className="text-sm font-medium text-muted-foreground">Pendientes</span>
              <strong className="text-2xl font-bold tracking-tight text-foreground">0</strong>
              <p className="text-sm leading-relaxed text-secondary">
                Lista preparada para futuras acciones de seguimiento y recomendaciones.
              </p>
            </Card>

            <Card variant="informative" padding="md" className="grid gap-2">
              <span className="text-sm font-medium text-muted-foreground">Completados</span>
              <strong className="text-2xl font-bold tracking-tight text-foreground">0</strong>
              <p className="text-sm leading-relaxed text-secondary">
                El resumen visual ya encaja con las mismas foundations del resto del producto.
              </p>
            </Card>
          </Grid>
        </div>
      </PageSection>

      <PageSection spacing="compact">
        <EmptyState
          title="Todavia no has anadido videojuegos a tu biblioteca"
          description="Cuando guardes una ficha, esta vista podra reutilizar cards y modulos sin rehacer el layout."
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
      </PageSection>
    </>
  );
}
