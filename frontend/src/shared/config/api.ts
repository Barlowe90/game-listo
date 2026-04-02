function normalize(url?: string) {
    return url?.replace(/\/$/, '');
}

export function getApiBaseUrl() {
    const publicBaseUrl = normalize(process.env.NEXT_PUBLIC_API_URL);

    if (typeof window === 'undefined') {
        const internalBaseUrl = normalize(process.env.API_URL_INTERNAL);
        if (internalBaseUrl) {
            return internalBaseUrl;
        }
    }

    if (!publicBaseUrl) {
        throw new Error('Missing environment variable: NEXT_PUBLIC_API_URL');
    }

    return publicBaseUrl;
}

export function getGraphqlUrl() {
    const publicGraphqlUrl = normalize(process.env.NEXT_PUBLIC_API_GRAPHQL_URL);

    if (typeof window === 'undefined') {
        const internalGraphqlUrl = normalize(process.env.API_GRAPHQL_INTERNAL_URL);
        if (internalGraphqlUrl) {
            return internalGraphqlUrl;
        }
    }

    return publicGraphqlUrl || `${getApiBaseUrl()}/graphql`;
}
