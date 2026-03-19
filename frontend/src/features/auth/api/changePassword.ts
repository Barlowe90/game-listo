import { httpClient } from './httpClient';

export async function changePassword(
  currentPassword: string,
  newPassword: string,
): Promise<void> {
  await httpClient.put('/v1/usuarios/password', {
    contrasenaActual: currentPassword,
    contrasenaNueva: newPassword,
  });
}
