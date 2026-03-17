'use client';

import { useAuth } from '@/features/auth/hooks/useAuth';
import { Avatar } from '@/shared/components/ui/Avatar';
import { Button } from '@/shared/components/ui/Button';

export default function AvatarUsuario() {
  const { user, logout } = useAuth();
  if (!user) return null;

  return (
    <div className="flex items-center gap-3 rounded-pill border border-border bg-surface p-2 shadow-surface">
      <Avatar src={user.avatar} name={user.username} size="sm" />
      <div className="hidden sm:grid">
        <span className="text-sm font-semibold text-foreground">{user.username}</span>
      </div>
      <Button variant="ghost" size="sm" onClick={() => void logout()}>
        Salir
      </Button>
    </div>
  );
}
