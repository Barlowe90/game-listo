import type {UsuarioResponse} from './auth.types';
import {httpClient} from './httpClient';

export interface EditProfileRequest {
    avatar: string;
}

export async function editProfile(data: EditProfileRequest): Promise<UsuarioResponse> {
    const response = await httpClient.patch<UsuarioResponse>('/v1/usuarios', data);

    return response.data;
}
