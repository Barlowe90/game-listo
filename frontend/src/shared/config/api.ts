export function getApiBaseUrl() {
  const baseUrl = process.env.NEXT_PUBLIC_API_URL?.replace(/\/$/, '');

  if (!baseUrl) {
    throw new Error('Missing environment variable: NEXT_PUBLIC_API_URL');
  }

  return baseUrl;
}
