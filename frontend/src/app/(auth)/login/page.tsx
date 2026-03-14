import Link from 'next/link';

export default function Home() {
  return (
    <main className="min-h-screen bg-background text-foreground">
      <div className="mx-auto max-w-7xl px-6 py-10">
        <h1 className="text-3xl font-bold">login</h1>
        <p className="mt-3 text-sm text-muted-foreground">Archivo: login</p>
        <Link href={'/registro'}>Nuevo? registrate</Link>
      </div>
    </main>
  );
}
