import { PlaceholderPage } from '@/shared/components/ui/PlaceholderPage';

export default function CookiesPage() {
  return (
    <PlaceholderPage
      eyebrow="Cookies"
      title="Gestion de cookies con estructura compartida"
      description="Esta vista confirma que las rutas del footer ya reutilizan page sections, contenedor, header y footer sin duplicacion manual."
      actions={[
        { href: '/politica-de-privacidad', label: 'Privacidad', variant: 'primary' },
        { href: '/', label: 'Volver al inicio', variant: 'secondary' },
      ]}
    />
  );
}
