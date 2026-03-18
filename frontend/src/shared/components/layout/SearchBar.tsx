'use client';

import { startTransition, useDeferredValue, useEffect, useId, useRef, useState } from 'react';
import type { FocusEventHandler, KeyboardEventHandler } from 'react';
import { usePathname, useRouter } from 'next/navigation';
import { getGameSuggestions } from '@/features/busquedas/api/sugerenciasApi';
import type { GameSuggestion } from '@/features/busquedas/model/sugerencias.types';
import { cn } from '@/lib/cn';
import { Input } from '@/shared/components/ui/Input';

const MIN_SUGGESTION_QUERY_LENGTH = 2;
const SUGGESTIONS_DEBOUNCE_MS = 180;

export interface SearchBarProps {
  className?: string;
  inputClassName?: string;
  label?: string;
  placeholder?: string;
  persistentParams?: Record<string, string | undefined>;
  targetPath?: string;
  queryParam?: string;
  size?: 'sm' | 'md';
  onSearch?: () => void;
  defaultValue?: string;
  enableSuggestions?: boolean;
  suggestionSize?: number;
  resetOnPathnameChange?: boolean;
}

export function SearchBar({
  className,
  inputClassName,
  label = 'Buscar videojuegos',
  placeholder = 'Buscar videojuegos',
  persistentParams,
  targetPath = '/catalogo',
  queryParam = 'q',
  size = 'md',
  onSearch,
  defaultValue = '',
  enableSuggestions = false,
  suggestionSize = 5,
  resetOnPathnameChange = false,
}: SearchBarProps) {
  const inputId = useId();
  const pathname = usePathname();
  const router = useRouter();
  const inputRef = useRef<HTMLInputElement>(null);
  const [query, setQuery] = useState(defaultValue);
  const deferredQuery = useDeferredValue(query.trim());
  const [suggestions, setSuggestions] = useState<GameSuggestion[]>([]);
  const [isLoadingSuggestions, setIsLoadingSuggestions] = useState(false);
  const [hasFetchedSuggestions, setHasFetchedSuggestions] = useState(false);
  const [hasFocusWithin, setHasFocusWithin] = useState(false);
  const [activeSuggestionIndex, setActiveSuggestionIndex] = useState(-1);
  const normalizedQuery = query.trim();
  const shouldFetchSuggestions =
    enableSuggestions && deferredQuery.length >= MIN_SUGGESTION_QUERY_LENGTH;
  const shouldShowSuggestions =
    enableSuggestions &&
    hasFocusWithin &&
    normalizedQuery.length >= MIN_SUGGESTION_QUERY_LENGTH &&
    (isLoadingSuggestions || hasFetchedSuggestions);
  const activeSuggestion =
    activeSuggestionIndex >= 0 ? suggestions[activeSuggestionIndex] : undefined;
  const listboxId = `${inputId}-suggestions`;

  useEffect(() => {
    setQuery(defaultValue);
  }, [defaultValue]);

  useEffect(() => {
    if (!resetOnPathnameChange) {
      return;
    }

    setQuery(defaultValue);
    setSuggestions([]);
    setIsLoadingSuggestions(false);
    setHasFetchedSuggestions(false);
    setHasFocusWithin(false);
    setActiveSuggestionIndex(-1);
    inputRef.current?.blur();
  }, [defaultValue, pathname, resetOnPathnameChange]);

  useEffect(() => {
    if (!enableSuggestions) {
      setSuggestions([]);
      setIsLoadingSuggestions(false);
      setHasFetchedSuggestions(false);
      setActiveSuggestionIndex(-1);
      return;
    }

    if (!shouldFetchSuggestions) {
      setSuggestions([]);
      setIsLoadingSuggestions(false);
      setHasFetchedSuggestions(false);
      setActiveSuggestionIndex(-1);
      return;
    }

    const abortController = new AbortController();
    const timeoutId = window.setTimeout(async () => {
      setSuggestions([]);
      setHasFetchedSuggestions(false);
      setIsLoadingSuggestions(true);
      setActiveSuggestionIndex(-1);

      try {
        const nextSuggestions = await getGameSuggestions(deferredQuery, {
          signal: abortController.signal,
          size: suggestionSize,
        });

        if (!abortController.signal.aborted) {
          setSuggestions(nextSuggestions);
        }
      } catch {
        if (!abortController.signal.aborted) {
          setSuggestions([]);
        }
      } finally {
        if (!abortController.signal.aborted) {
          setIsLoadingSuggestions(false);
          setHasFetchedSuggestions(true);
        }
      }
    }, SUGGESTIONS_DEBOUNCE_MS);

    return () => {
      abortController.abort();
      window.clearTimeout(timeoutId);
    };
  }, [deferredQuery, enableSuggestions, shouldFetchSuggestions, suggestionSize]);

  const navigateTo = (href: string) => {
    startTransition(() => {
      router.push(href);
    });

    onSearch?.();
  };

  const handleSuggestionSelect = (suggestion: GameSuggestion) => {
    setQuery(suggestion.title);
    setHasFocusWithin(false);
    setActiveSuggestionIndex(-1);
    inputRef.current?.blur();
    navigateTo(`/videojuego/${suggestion.gameId}`);
  };

  const handleSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
    event.preventDefault();

    if (activeSuggestion) {
      handleSuggestionSelect(activeSuggestion);
      return;
    }

    const params = new URLSearchParams();

    Object.entries(persistentParams ?? {}).forEach(([paramKey, paramValue]) => {
      if (paramValue?.trim()) {
        params.set(paramKey, paramValue.trim());
      }
    });

    if (normalizedQuery) {
      params.set(queryParam, normalizedQuery);
    }

    const serializedParams = params.toString();
    const href = serializedParams ? `${targetPath}?${serializedParams}` : targetPath;

    setHasFocusWithin(false);
    setActiveSuggestionIndex(-1);
    navigateTo(href);
  };

  const handleBlurCapture: FocusEventHandler<HTMLDivElement> = (event) => {
    const nextFocusedElement = event.relatedTarget;

    if (nextFocusedElement instanceof Node && event.currentTarget.contains(nextFocusedElement)) {
      return;
    }

    setHasFocusWithin(false);
    setActiveSuggestionIndex(-1);
  };

  const handleInputKeyDown: KeyboardEventHandler<HTMLInputElement> = (event) => {
    if (!shouldShowSuggestions || !suggestions.length) {
      if (event.key === 'Escape') {
        setHasFocusWithin(false);
        setActiveSuggestionIndex(-1);
      }

      return;
    }

    if (event.key === 'ArrowDown') {
      event.preventDefault();
      setActiveSuggestionIndex((currentIndex) =>
        currentIndex < suggestions.length - 1 ? currentIndex + 1 : 0,
      );
    }

    if (event.key === 'ArrowUp') {
      event.preventDefault();
      setActiveSuggestionIndex((currentIndex) =>
        currentIndex > 0 ? currentIndex - 1 : suggestions.length - 1,
      );
    }

    if (event.key === 'Escape') {
      event.preventDefault();
      setHasFocusWithin(false);
      setActiveSuggestionIndex(-1);
    }
  };

  return (
    <div
      className={cn('relative w-full', className)}
      onBlurCapture={handleBlurCapture}
      onFocusCapture={() => setHasFocusWithin(true)}
    >
      <form role="search" aria-label={label} onSubmit={handleSubmit} className="flex w-full">
        <label htmlFor={inputId} className="sr-only">
          {label}
        </label>
        <Input
          ref={inputRef}
          id={inputId}
          name={queryParam}
          type="search"
          size={size}
          value={query}
          placeholder={placeholder}
          autoComplete="off"
          role={enableSuggestions ? 'combobox' : undefined}
          aria-autocomplete={enableSuggestions ? 'list' : undefined}
          aria-controls={shouldShowSuggestions ? listboxId : undefined}
          aria-expanded={enableSuggestions ? shouldShowSuggestions : undefined}
          aria-activedescendant={
            shouldShowSuggestions && activeSuggestion
              ? `${listboxId}-option-${activeSuggestion.gameId}`
              : undefined
          }
          className={cn('w-full', inputClassName)}
          onChange={(event) => {
            setQuery(event.target.value);
            setActiveSuggestionIndex(-1);
            setHasFocusWithin(true);
          }}
          onKeyDown={handleInputKeyDown}
        />
      </form>

      {shouldShowSuggestions ? (
        <div className="absolute inset-x-0 top-[calc(100%+0.5rem)] z-50">
          <div className="overflow-hidden rounded-2xl border border-border bg-card p-2 shadow-overlay">
            <div className="px-3 py-2 text-xs font-semibold tracking-[0.08em] text-muted-foreground uppercase">
              Sugerencias
            </div>

            {isLoadingSuggestions ? (
              <div className="px-3 py-3 text-sm text-secondary">Buscando videojuegos...</div>
            ) : suggestions.length ? (
              <ul id={listboxId} role="listbox" className="grid gap-1">
                {suggestions.map((suggestion, index) => {
                  const isActive = index === activeSuggestionIndex;

                  return (
                    <li key={suggestion.gameId} role="presentation">
                      <button
                        id={`${listboxId}-option-${suggestion.gameId}`}
                        type="button"
                        role="option"
                        aria-selected={isActive}
                        className={cn(
                          'flex w-full items-center justify-between gap-3 rounded-xl px-3 py-3 text-left text-sm text-secondary transition-colors duration-[var(--duration-fast)] ease-[var(--easing-standard)] hover:bg-surface hover:text-foreground focus-visible:bg-surface focus-visible:text-foreground',
                          isActive && 'bg-surface text-foreground',
                        )}
                        onMouseEnter={() => setActiveSuggestionIndex(index)}
                        onClick={() => handleSuggestionSelect(suggestion)}
                      >
                        <span className="font-medium text-black">{suggestion.title}</span>
                      </button>
                    </li>
                  );
                })}
              </ul>
            ) : (
              <div className="px-3 py-3 text-sm text-secondary">
                No hemos encontrado videojuegos para{' '}
                <span className="font-medium">{normalizedQuery}</span>.
              </div>
            )}
          </div>
        </div>
      ) : null}
    </div>
  );
}
