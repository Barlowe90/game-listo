import { NextRequest, NextResponse } from 'next/server';
import { getApiBaseUrl } from '@/shared/config/api';
import type { SuggestionsResponse } from '@/features/busquedas/model/sugerencias.types';

export async function GET(request: NextRequest) {
  const q = request.nextUrl.searchParams.get('q')?.trim() ?? '';
  const sizeParam = request.nextUrl.searchParams.get('size') ?? '5';

  if (q.length < 2) {
    const emptyResponse: SuggestionsResponse = {
      query: q,
      results: [],
    };

    return NextResponse.json(emptyResponse);
  }

  const upstreamParams = new URLSearchParams({
    q,
    size: sizeParam,
  });

  const upstreamUrl = `${getApiBaseUrl()}/v1/busquedas/sugerencia?${upstreamParams}`;

  try {
    const upstreamResponse = await fetch(upstreamUrl, {
      headers: {
        Accept: 'application/json',
      },
      cache: 'no-store',
    });

    const body = await upstreamResponse.text();

    return new NextResponse(body, {
      status: upstreamResponse.status,
      headers: {
        'content-type': upstreamResponse.headers.get('content-type') ?? 'application/json',
      },
    });
  } catch {
    return NextResponse.json(
      {
        message: 'No se pudo contactar con el servicio de sugerencias.',
      },
      {
        status: 502,
      },
    );
  }
}

