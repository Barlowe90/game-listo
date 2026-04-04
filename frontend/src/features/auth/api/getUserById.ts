import type {UsuarioResponse} from './auth.types';
import {httpClient} from './httpClient';

export async function getUserById(userId: string): Promise<UsuarioResponse> {
    const response = await httpClient.get<UsuarioResponse>(`/v1/usuarios/${userId}`);

    return response.data;
}
