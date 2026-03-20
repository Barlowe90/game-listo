import type { ReactNode } from 'react';
import { cn } from '@/lib/cn';
import { Card } from '@/shared/components/ui/Card';
import { Skeleton } from '@/shared/components/ui/Skeleton';
import { VideojuegoRouteState } from './VideojuegoRouteState';

function SurfaceSkeleton({ children, className }: { children: ReactNode; className?: string }) {
  return (
    <Card
      className={cn(
        'rounded-[calc(var(--radius-xl)+0.75rem)] border border-border bg-white/90 shadow-elevated backdrop-blur-sm',
        className,
      )}
    >
      {children}
    </Card>
  );
}

export default function Loading() {
  return (
    <VideojuegoRouteState>
      <div className="grid gap-6">
        <div className="grid gap-6 xl:grid-cols-[minmax(0,1.02fr)_minmax(0,0.98fr)]">
          <SurfaceSkeleton className="p-6">
            <div className="grid gap-6 md:grid-cols-[220px_minmax(0,1fr)]">
              <Skeleton
                variant="block"
                size="lg"
                className="h-80 rounded-[calc(var(--radius-xl)+0.5rem)]"
              />

              <div className="grid content-start gap-4">
                <Skeleton variant="line" size="sm" className="w-28" />
                <Skeleton variant="line" size="lg" className="w-3/4" />
                <Skeleton variant="line" size="md" className="w-1/2" />

                <div className="flex flex-wrap gap-2 pt-2">
                  {Array.from({ length: 4 }, (_, index) => (
                    <Skeleton
                      key={`hero-tag-${index}`}
                      variant="block"
                      size="sm"
                      className="h-9 w-24 rounded-pill"
                    />
                  ))}
                </div>

                <Skeleton variant="block" size="sm" className="mt-2 h-11 w-44 rounded-md" />
              </div>
            </div>
          </SurfaceSkeleton>

          <div className="grid gap-6">
            {Array.from({ length: 2 }, (_, index) => (
              <SurfaceSkeleton key={`summary-${index}`} className="p-6">
                <div className="grid gap-4">
                  <Skeleton variant="line" size="sm" className="w-32" />
                  <Skeleton variant="line" size="md" className="w-full" />
                  <Skeleton variant="line" size="md" className="w-5/6" />
                  <div className="grid gap-3 pt-2 sm:grid-cols-3">
                    {Array.from({ length: 3 }, (_, statIndex) => (
                      <Skeleton
                        key={`summary-${index}-stat-${statIndex}`}
                        variant="block"
                        size="sm"
                        className="h-[4.5rem] rounded-[calc(var(--radius-xl)+0.1rem)]"
                      />
                    ))}
                  </div>
                </div>
              </SurfaceSkeleton>
            ))}
          </div>
        </div>

        <SurfaceSkeleton className="p-4">
          <div className="flex flex-wrap gap-3">
            {Array.from({ length: 4 }, (_, index) => (
              <Skeleton
                key={`tab-${index}`}
                variant="block"
                size="sm"
                className="h-10 w-28 rounded-pill"
              />
            ))}
          </div>
        </SurfaceSkeleton>

        <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
          {Array.from({ length: 6 }, (_, index) => (
            <SurfaceSkeleton key={`panel-${index}`} className="p-6">
              <div className="grid gap-4">
                <Skeleton variant="line" size="sm" className="w-32" />
                <Skeleton variant="line" size="md" className="w-full" />
                <Skeleton variant="line" size="md" className="w-11/12" />
                <Skeleton variant="line" size="md" className="w-4/5" />
              </div>
            </SurfaceSkeleton>
          ))}
        </div>
      </div>
    </VideojuegoRouteState>
  );
}
