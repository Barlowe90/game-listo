import { ResetPasswordPageClient } from './ResetPasswordPageClient';

type ResetPasswordPageProps = {
  searchParams?:
    | Promise<Record<string, string | string[] | undefined>>
    | Record<string, string | string[] | undefined>;
};

function getSearchValue(value: string | string[] | undefined) {
  if (Array.isArray(value)) {
    return value[0]?.trim() ?? '';
  }

  return value?.trim() ?? '';
}

export default async function ResetPasswordPage({ searchParams }: ResetPasswordPageProps) {
  const resolvedSearchParams = (await searchParams) ?? {};

  return (
    <ResetPasswordPageClient
      token={getSearchValue(resolvedSearchParams.token)}
      initialEmail={getSearchValue(resolvedSearchParams.email)}
    />
  );
}
