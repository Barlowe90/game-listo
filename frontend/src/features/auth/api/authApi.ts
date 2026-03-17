import { login } from './login';
import { logout } from './logout';
import { refresh } from './refresh';
import { me } from './me';
import { register } from './register';
import { resendVerification } from './resendVerification';

export const authApi = {
  login,
  logout,
  refresh,
  me,
  register,
  resendVerification,
};
