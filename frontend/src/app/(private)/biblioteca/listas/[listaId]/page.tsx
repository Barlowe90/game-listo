import { BibliotecaListDetailPage } from '@/features/biblioteca/components/BibliotecaListDetailPage';

export default async function BibliotecaListaPage({
  params,
}: {
  params: Promise<{ listaId: string }>;
}) {
  const { listaId } = await params;

  return <BibliotecaListDetailPage listaId={listaId} />;
}
