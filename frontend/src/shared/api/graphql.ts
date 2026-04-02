import { getGraphqlUrl } from '@/shared/config/api';

const DEFAULT_REVALIDATE_SECONDS = 60;

export async function fetchGraphQL<T>(query: string, variables?: Record<string, unknown>) {
  const endpoint = getGraphqlUrl();

  let response: Response;
  try {
    response = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify({
        query,
        variables,
      }),
      next: {
        revalidate: DEFAULT_REVALIDATE_SECONDS,
      },
    });
  } catch (error) {
    const detail = error instanceof Error ? error.message : 'Unknown error';
    throw new Error(`GraphQL fetch failed (${endpoint}): ${detail}`);
  }

  if (!response.ok) {
    throw new Error(`GraphQL request failed with status ${response.status}`);
  }

  const json = await response.json();

  if (json.errors) {
    console.error('GraphQL errors:', json.errors);
    throw new Error('GraphQL query returned errors');
  }

  return json.data as T;
}
