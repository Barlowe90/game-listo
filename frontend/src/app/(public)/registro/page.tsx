'use client';

import { authApi } from '@/features/auth/api/authApi';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import axios from 'axios';

export default function RegistroPage() {
  const router = useRouter();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleSubmit: React.SubmitEventHandler<HTMLFormElement> = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    setSuccessMessage(null);
    setErrorMessage(null);

    try {
      await authApi.register({
        username,
        email,
        password,
      });

      setSuccessMessage('Usuario registrado correctamente.');

      setUsername('');
      setEmail('');
      setPassword('');

      setTimeout(() => {
        router.replace('/login');
      }, 1000);
    } catch (error: unknown) {
      if (axios.isAxiosError<{ message?: string }>(error)) {
        setErrorMessage(
          error.response?.data?.message ??
            'No se pudo completar el registro. Revisa los datos e inténtalo otra vez.',
        );
      } else {
        setErrorMessage('No se pudo completar el registro. Revisa los datos e inténtalo otra vez.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <main style={{ maxWidth: 420, margin: '40px auto' }}>
      <h1>Crear cuenta</h1>

      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 12 }}>
          <label htmlFor="username">Usuario</label>
          <input
            id="username"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            autoComplete="username"
            style={{ display: 'block', width: '100%' }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            autoComplete="email"
            style={{ display: 'block', width: '100%' }}
          />
        </div>

        <div style={{ marginBottom: 12 }}>
          <label htmlFor="password">Contraseña</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            autoComplete="new-password"
            style={{ display: 'block', width: '100%' }}
          />
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Registrando...' : 'Registrarse'}
        </button>
      </form>

      {successMessage && <p style={{ marginTop: 16 }}>{successMessage}</p>}
      {errorMessage && <p style={{ marginTop: 16 }}>{errorMessage}</p>}

      <p style={{ marginTop: 16 }}>
        ¿Ya tienes cuenta? <Link href="/login">Ir al login</Link>
      </p>
    </main>
  );
}
