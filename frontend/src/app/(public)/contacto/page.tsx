import { PlaceholderPage } from '@/shared/components/ui/PlaceholderPage';

export default function Home() {
  return (
    <PlaceholderPage
      eyebrow="Contacto"
      title="Zona de contacto con shell consistente"
      description="La pagina hereda el mismo lenguaje visual que el resto del producto: color semantico, espaciado oficial y foco visible."
      actions={[
        { href: '/', label: 'Volver al inicio', variant: 'primary' },
        { href: '/registro', label: 'Crear cuenta', variant: 'secondary' },
      ]}
    />
  );
}
