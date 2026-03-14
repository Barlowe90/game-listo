'use client';

import AvatarUsuario from '@/features/auth/components/AvatarUsuario';
import { useAuth } from '@/features/auth/hooks/useAuth';
import Link from 'next/link';

export default function Navbar() {
  const { isAuthenticated } = useAuth();

  return (
    <nav>
      {/* el logo debe de ir a la izquierda */}
      <div>Logo GameListo</div>
      {/* estos componentes deben de ir en el centro */}
      <div>
        <p>Videojuegos</p>
        <p>Mi biblioteca</p>
        <p>Mis publicaciones</p>
        <p>Barra de busqueda</p>
      </div>
      {/* a la derecha debe de ir el boton de iniciar sesion */}
      {/* o el avatar del usuario si esta logeado */}
      {isAuthenticated ? <AvatarUsuario /> : <Link href="/login">Login</Link>}
    </nav>
  );
}
