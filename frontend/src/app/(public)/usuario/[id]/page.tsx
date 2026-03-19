import { ProfilePageClient } from './ProfilePageClient';

type ProfileSectionKey = 'biblioteca' | 'amigos' | 'ajustes';

interface SearchParams {
  seccion?: string | string[];
}

interface RouteParams {
  id: string;
}

function getSearchValue(value: string | string[] | undefined) {
  if (Array.isArray(value)) {
    return value[0] ?? '';
  }

  return value ?? '';
}

function normalizeSection(value: string | string[] | undefined): ProfileSectionKey {
  const normalizedValue = getSearchValue(value).trim().toLowerCase();

  if (normalizedValue === 'amigos' || normalizedValue === 'ajustes') {
    return normalizedValue;
  }

  return 'biblioteca';
}

export default async function UsuarioPage({
  params,
  searchParams,
}: Readonly<{
  params: Promise<RouteParams>;
  searchParams: Promise<SearchParams>;
}>) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;

  return (
    <ProfilePageClient
      activeSection={normalizeSection(resolvedSearchParams.seccion)}
      profileUserId={resolvedParams.id}
    />
  );
}
