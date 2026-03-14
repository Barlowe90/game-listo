'use client';

import Image from 'next/image';
import { useAuth } from '@/features/auth/hooks/useAuth';

export default function AvatarUsuario() {
  const { user, logout } = useAuth();
  if (!user) return null;

  const inicialesUsername =
    (user.username || '')
      .split(/\s+/)
      .map((p) => p[0])
      .slice(0, 2)
      .join('')
      .toUpperCase() || '?';

  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
      <Image
        src={user.avatar!}
        alt={user.username}
        width={40}
        height={40}
        style={{ borderRadius: '50%', objectFit: 'cover' }}
      />
      <div style={{ fontWeight: 600 }}>{inicialesUsername}</div>
      <button onClick={() => void logout()}>Logout</button>
    </div>
  );
}
