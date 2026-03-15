import { PlaceholderPage } from '@/shared/components/ui/PlaceholderPage';

export default function PoliticaPrivacidadPage() {
  return (
    <PlaceholderPage
      eyebrow="Privacidad"
      title="Politica de privacidad dentro del layout compartido"
      description="El footer global ya enlaza esta ruta informativa con el mismo header, footer, spacing y contenedor del resto del MVP."
      actions={[
        { href: '/contacto', label: 'Contactar', variant: 'primary' },
        { href: '/', label: 'Volver al inicio', variant: 'secondary' },
      ]}
    />
  );
}

