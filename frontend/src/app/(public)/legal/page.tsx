import { PlaceholderPage } from '@/shared/components/ui/PlaceholderPage';

export default function LegalPage() {
  return (
    <PlaceholderPage
      eyebrow="Legal"
      title="Informacion legal con shell consistente"
      description="La fase 4.4 deja resuelta tambien la navegacion hacia vistas informativas, sin romper el sistema visual."
      actions={[
        { href: '/cookies', label: 'Ver cookies', variant: 'primary' },
        { href: '/', label: 'Volver al inicio', variant: 'secondary' },
      ]}
    />
  );
}

