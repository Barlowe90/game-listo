import type { HTMLAttributes } from 'react';
import { cn } from '@/lib/cn';

export type AvailabilityPeriod = 'manana' | 'tarde' | 'noche';
export type AvailabilityDay =
  | 'lunes'
  | 'martes'
  | 'miercoles'
  | 'jueves'
  | 'viernes'
  | 'sabado'
  | 'domingo';

export type AvailabilityMatrixValue = Partial<Record<AvailabilityDay, AvailabilityPeriod[]>>;

const DAY_LABELS: Array<{ key: AvailabilityDay; shortLabel: string; label: string }> = [
  { key: 'lunes', shortLabel: 'L', label: 'Lunes' },
  { key: 'martes', shortLabel: 'M', label: 'Martes' },
  { key: 'miercoles', shortLabel: 'X', label: 'Miercoles' },
  { key: 'jueves', shortLabel: 'J', label: 'Jueves' },
  { key: 'viernes', shortLabel: 'V', label: 'Viernes' },
  { key: 'sabado', shortLabel: 'S', label: 'Sabado' },
  { key: 'domingo', shortLabel: 'D', label: 'Domingo' },
] as const;

const PERIOD_LABELS: Array<{ key: AvailabilityPeriod; label: string }> = [
  { key: 'manana', label: 'Manana' },
  { key: 'tarde', label: 'Tarde' },
  { key: 'noche', label: 'Noche' },
] as const;

export interface AvailabilityMatrixProps extends HTMLAttributes<HTMLDivElement> {
  availability: AvailabilityMatrixValue;
  compact?: boolean;
}

export function AvailabilityMatrix({
  availability,
  className,
  compact = false,
  ...props
}: AvailabilityMatrixProps) {
  return (
    <div
      className={cn(
        'rounded-[calc(var(--radius-xl)+0.25rem)] bg-surface p-4',
        className,
      )}
      {...props}
    >
      <table className="w-full border-separate border-spacing-2 text-left">
        <caption className="sr-only">Disponibilidad semanal</caption>
        <thead>
          <tr>
            <th className="w-14 text-xs font-semibold tracking-[0.08em] text-secondary uppercase">
              Dia
            </th>
            {PERIOD_LABELS.map((period) => (
              <th
                key={period.key}
                className="text-center text-xs font-semibold tracking-[0.08em] text-secondary uppercase"
                scope="col"
              >
                {compact ? period.label.slice(0, 1) : period.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {DAY_LABELS.map((day) => (
            <tr key={day.key}>
              <th
                scope="row"
                className="text-xs font-semibold tracking-[0.08em] text-foreground uppercase"
              >
                {compact ? day.shortLabel : day.label}
              </th>
              {PERIOD_LABELS.map((period) => {
                const isActive = availability[day.key]?.includes(period.key) ?? false;

                return (
                  <td key={`${day.key}-${period.key}`} className="text-center">
                    <span
                      className={cn(
                        'inline-flex h-9 w-full max-w-16 items-center justify-center rounded-xl border text-xs font-medium transition-colors',
                        isActive
                          ? 'border-transparent bg-primary text-primary-foreground'
                          : 'border-border bg-background text-secondary',
                      )}
                    >
                      <span className="sr-only">
                        {isActive ? 'Disponible' : 'No disponible'} el {day.label.toLowerCase()} por
                        la {period.label.toLowerCase()}
                      </span>
                      <span aria-hidden="true">{isActive ? 'Si' : 'No'}</span>
                    </span>
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
