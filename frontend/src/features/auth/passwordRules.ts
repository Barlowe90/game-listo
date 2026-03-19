const MIN_PASSWORD_LENGTH = 8;
const UPPERCASE_REGEX = /[A-Z]/;
const DIGIT_REGEX = /\d/;

export const PASSWORD_RULES_HELP_TEXT =
  'Usa al menos 8 caracteres, una mayuscula y un numero.';

export function getPasswordRuleErrorMessage(
  password: string,
  fieldLabel = 'La contrasena',
): string | null {
  if (password.length < MIN_PASSWORD_LENGTH) {
    return `${fieldLabel} debe tener al menos 8 caracteres.`;
  }

  if (!UPPERCASE_REGEX.test(password)) {
    return `${fieldLabel} debe incluir al menos una mayuscula.`;
  }

  if (!DIGIT_REGEX.test(password)) {
    return `${fieldLabel} debe incluir al menos un numero.`;
  }

  return null;
}
