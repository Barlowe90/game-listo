import { PlaceholderPage } from '@/shared/components/ui/PlaceholderPage';

export default function Home() {
  return (
    <PlaceholderPage
      eyebrow="Perfil"
      title="Perfil de usuario con base visual compartida"
      description="El shell mantiene consistencia con el resto de GameListo y deja espacio para introducir actividad, biblioteca y metadatos sin romper foundations."
      actions={[
        { href: '/catalogo', label: 'Explorar catalogo', variant: 'primary' },
        { href: '/', label: 'Volver al inicio', variant: 'secondary' },
      ]}
    />
  );
}
