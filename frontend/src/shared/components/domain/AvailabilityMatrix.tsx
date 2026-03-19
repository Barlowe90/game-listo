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
  stretch?: boolean;
  abbreviatedLabels?: boolean;
}

export function AvailabilityMatrix({
  availability,
  className,
  compact = false,
  stretch = false,
  abbreviatedLabels = compact,
  ...props
}: AvailabilityMatrixProps) {
  return (
    <div
      className={cn(
        'overflow-x-auto rounded-[calc(var(--radius-xl)+0.25rem)] bg-surface',
        compact ? 'p-3' : 'p-4',
        className,
      )}
      {...props}
    >
      <table
        className={cn(
          'border-separate text-left',
          compact
            ? stretch
              ? 'w-full table-fixed border-spacing-1'
              : 'w-fit table-auto border-spacing-1'
            : 'w-full border-spacing-2',
        )}
      >
        <caption className="sr-only">Disponibilidad semanal</caption>
        <thead>
          <tr>
            <th
              className={cn(
                'text-xs font-semibold tracking-[0.08em] text-secondary uppercase',
                compact ? (abbreviatedLabels ? 'w-8' : 'w-20') : 'w-14',
              )}
            >
              Dia
            </th>
            {PERIOD_LABELS.map((period) => (
              <th
                key={period.key}
                className={cn(
                  'text-center font-semibold tracking-[0.08em] text-secondary uppercase',
                  compact ? (abbreviatedLabels ? 'text-[0.65rem]' : 'text-[0.7rem]') : 'text-xs',
                )}
                scope="col"
              >
                {compact && abbreviatedLabels ? period.label.slice(0, 1) : period.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {DAY_LABELS.map((day) => (
            <tr key={day.key}>
              <th
                scope="row"
                className={cn(
                  'font-semibold tracking-[0.08em] text-foreground uppercase',
                  compact ? (abbreviatedLabels ? 'text-[0.65rem]' : 'text-[0.7rem]') : 'text-xs',
                )}
              >
                {compact && abbreviatedLabels ? day.shortLabel : day.label}
              </th>
              {PERIOD_LABELS.map((period) => {
                const isActive = availability[day.key]?.includes(period.key) ?? false;

                return (
                  <td key={`${day.key}-${period.key}`} className="text-center">
                    <span
                      className={cn(
                        'inline-flex items-center justify-center border font-medium transition-colors',
                        compact
                          ? stretch
                            ? 'h-7 w-full rounded-lg text-[0.65rem]'
                            : 'h-7 w-9 rounded-lg text-[0.65rem]'
                          : 'h-9 w-full max-w-16 rounded-xl text-xs',
                        isActive
                          ? 'border-transparent bg-primary text-primary-foreground'
                          : 'border-border bg-background text-secondary',
                      )}
                    >
                      <span className="sr-only">
                        {isActive ? 'Disponible' : 'No disponible'} el {day.label.toLowerCase()} por
                        la {period.label.toLowerCase()}
                      </span>
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
