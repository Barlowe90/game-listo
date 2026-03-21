import { PageSection } from '@/shared/components/layout/PageSection';
import { Card } from '@/shared/components/ui/Card';

export default function NosotrosPage() {
  return (
    <>
      <PageSection spacing="hero" size="narrow">
        <Card
          padding="lg"
          className="rounded-[1.5rem] border border-[#e2e8f0] bg-white shadow-[0_18px_45px_rgba(15,23,42,0.06)]"
        >
          <div className="grid gap-5">
            <div className="grid gap-3">
              <p className="text-sm font-semibold uppercase tracking-[0.18em] text-secondary">
                Nosotros
              </p>

              <h1 className="text-3xl font-bold tracking-tight text-foreground lg:text-4xl">
                TFG GameListo
              </h1>

              <p className="text-base leading-relaxed text-secondary">
                GameListo es una plataforma social para jugadores desarrollada como Trabajo Fin de
                Grado de Ingenieria Informatica de la Universidad de Murcia.
              </p>

              <p className="text-base leading-relaxed text-secondary">
                La propuesta reune en un mismo lugar la organizacion de la biblioteca personal, el
                descubrimiento de videojuegos y una capa social para compartir la experiencia con
                otros jugadores.
              </p>

              <p className="text-base leading-relaxed text-secondary">
                Para información técnica puedes visitar el repositorio en Github. si quieres
                conectar conmigo te dejo mi LinkedIn en la parte inferior derecha.
              </p>
            </div>
          </div>
        </Card>
      </PageSection>
    </>
  );
}
