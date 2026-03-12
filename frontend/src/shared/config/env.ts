const requiredEnv = {
  NEXT_PUBLIC_API_GRAPHQL_URL: process.env.NEXT_PUBLIC_API_GRAPHQL_URL,
  NEXT_PUBLIC_DEFAULT_LOCALE: process.env.NEXT_PUBLIC_DEFAULT_LOCALE ?? 'es',
};

for (const [key, value] of Object.entries(requiredEnv)) {
  if (!value) {
    throw new Error(`Missing environment variable: ${key}`);
  }
}

export const env = {
  graphqlUrl: requiredEnv.NEXT_PUBLIC_API_GRAPHQL_URL,
  defaultLocale: requiredEnv.NEXT_PUBLIC_DEFAULT_LOCALE,
};