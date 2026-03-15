import Link from 'next/link';
import { Container } from '@/shared/components/layout/Container';
import { Grid } from '@/shared/components/layout/Grid';

const productLinks = [
  { href: '/', label: 'Inicio' },
  { href: '/catalogo', label: 'Catalogo' },
  { href: '/biblioteca', label: 'Mi biblioteca' },
  { href: '/contacto', label: 'Contacto' },
] as const;

const legalLinks = [
  { href: '/nosotros', label: 'Nosotros' },
  { href: '/politica-de-privacidad', label: 'Politica de privacidad' },
  { href: '/legal', label: 'Legal' },
  { href: '/cookies', label: 'Cookies' },
] as const;

export function Footer() {
  return (
    <footer className="border-t border-border bg-surface">
      <Container size="wide" className="py-8 lg:py-10">
        <Grid variant="twoColumn" className="gap-8">
          <div className="grid gap-3">
            <div className="flex items-center gap-3">
              <span className="flex size-10 items-center justify-center rounded-pill bg-primary text-sm font-bold text-primary-foreground shadow-surface">
                G
              </span>
              <div className="grid gap-1">
                <strong className="text-sm font-semibold text-foreground">GameListo</strong>
                <span className="text-sm leading-relaxed text-secondary">
                  Shell global, navegacion y layout compartido para el MVP.
                </span>
              </div>
            </div>
            <p className="max-w-xl text-sm leading-relaxed text-secondary">
              La aplicacion ya reutiliza header, footer, contenedores, busqueda y secciones
              verticales sobre foundations comunes.
            </p>
          </div>

          <div className="grid gap-6 sm:grid-cols-2">
            <div className="grid gap-3">
              <h2 className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                Explorar
              </h2>
              <div className="grid gap-2">
                {productLinks.map((link) => (
                  <Link
                    key={link.href}
                    href={link.href}
                    className="text-sm text-secondary transition-colors hover:text-foreground"
                  >
                    {link.label}
                  </Link>
                ))}
              </div>
            </div>

            <div className="grid gap-3">
              <h2 className="text-sm font-semibold tracking-[0.08em] text-primary uppercase">
                Legal
              </h2>
              <div className="grid gap-2">
                {legalLinks.map((link) => (
                  <Link
                    key={link.href}
                    href={link.href}
                    className="text-sm text-secondary transition-colors hover:text-foreground"
                  >
                    {link.label}
                  </Link>
                ))}
              </div>
            </div>
          </div>
        </Grid>

        <div className="mt-8 flex flex-col gap-3 border-t border-border pt-6 text-sm text-muted-foreground sm:flex-row sm:items-center sm:justify-between">
          <span>GameListo MVP - fase 4.4 completada sobre el sistema visual.</span>
          <span>Responsive basico y navegacion global compartida.</span>
        </div>
      </Container>
    </footer>
  );
}
