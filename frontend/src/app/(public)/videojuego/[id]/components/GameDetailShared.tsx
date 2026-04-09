import type {ReactNode} from 'react';
import Image from 'next/image';
import type {Game} from '@/features/catalogo/model/catalog.types';
import {TagList} from '@/shared/components/domain/TagList';
import {cn} from '@/lib/cn';
import {Card} from '@/shared/components/ui/Card';
import {
    buildRelatedEntries,
    buildSingleRelatedEntry,
    getYouTubeEmbedUrl,
} from '../gameDetail.utils';

const MAX_RELATED_LINKS = 6;

export function SurfaceCard({
                                children,
                                className,
                            }: Readonly<{
    children: ReactNode;
    className?: string;
}>) {
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

function EmptyCopy({children}: Readonly<{ children: ReactNode }>) {
    return <p className="text-sm leading-relaxed text-secondary">{children}</p>;
}

export function MetaStat({label, value}: Readonly<{ label: string; value: string }>) {
    return (
        <div className="grid gap-1 rounded-[calc(var(--radius-xl)+0.1rem)] bg-background p-4">
      <span className="text-xs font-semibold tracking-[0.08em] text-primary uppercase">
        {label}
      </span>
            <span className="text-sm font-medium text-foreground">{value}</span>
        </div>
    );
}

export function TextList({
                             emptyLabel,
                             items,
                         }: Readonly<{
    emptyLabel: string;
    items: string[];
}>) {
    if (!items.length) {
        return <EmptyCopy>{emptyLabel}</EmptyCopy>;
    }

    // Deduplicate textual items to avoid duplicate React keys and preserve order
    const uniqueItems = Array.from(new Set(items));

    return (
        <ul className="grid gap-2 text-sm leading-relaxed text-secondary">
            {uniqueItems.map((item, idx) => (
                <li key={`${item}-${idx}`}>{item}</li>
            ))}
        </ul>
    );
}

export function ExternalLinksList({links}: Readonly<{ links: string[] }>) {
    if (!links.length) {
        return <EmptyCopy>No hay enlaces externos registrados para este titulo.</EmptyCopy>;
    }

    const uniqueLinks = Array.from(new Set(links));
    const displayed = uniqueLinks.slice(0, MAX_RELATED_LINKS);

    return (
        <ul className="grid gap-2 text-sm leading-relaxed text-secondary">
            {displayed.map((link, idx) => (
                <li key={`${link}-${idx}`}>
                    <a
                        href={link}
                        target="_blank"
                        rel="noreferrer"
                        className="break-all text-primary underline-offset-4 hover:underline"
                    >
                        {link}
                    </a>
                </li>
            ))}
            {uniqueLinks.length > MAX_RELATED_LINKS ? (
                <li className="text-secondary">+{uniqueLinks.length - MAX_RELATED_LINKS} enlaces mas</li>
            ) : null}
        </ul>
    );
}

export function RelatedGameList({
                                    emptyLabel,
                                    gameIds,
                                    relatedGames,
                                }: Readonly<{
    emptyLabel: string;
    gameIds: number[];
    relatedGames: Map<number, Game>;
}>) {
    const entries = buildRelatedEntries(gameIds, relatedGames);

    if (!entries.length) {
        return <EmptyCopy>{emptyLabel}</EmptyCopy>;
    }

    return (
        <div className="grid gap-3">
            <TagList
                items={entries.map((entry) => entry.label)}
                getHref={(label) => entries.find((entry) => entry.label === label)?.href}
                tone="tag"
            />
        </div>
    );
}

export function ParentGameLink({
                                   gameId,
                                   relatedGames,
                               }: Readonly<{
    gameId: number | null;
    relatedGames: Map<number, Game>;
}>) {
    if (!gameId) {
        return <EmptyCopy>Este juego no depende de un parent game registrado.</EmptyCopy>;
    }

    const entry = buildSingleRelatedEntry(gameId, relatedGames);

    return <TagList items={[entry.label]} getHref={() => entry.href} tone="tag" className="w-fit"/>;
}

export function VideoEmbedCard({
                                   index,
                                   videoUrl,
                               }: Readonly<{
    index: number;
    videoUrl: string;
}>) {
    const embedUrl = getYouTubeEmbedUrl(videoUrl);

    return (
        <SurfaceCard>
            <div className="grid gap-4 p-4">
                <div className="overflow-hidden rounded-[calc(var(--radius-xl)+0.25rem)] border border-border">
                    <div className="aspect-video">
                        <iframe
                            title={`Video ${index + 1}`}
                            src={embedUrl}
                            className="h-full w-full"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                            allowFullScreen
                        />
                    </div>
                </div>
                <a
                    href={videoUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="text-sm font-medium text-primary underline-offset-4 hover:underline"
                >
                    Abrir en YouTube
                </a>
            </div>
        </SurfaceCard>
    );
}

export function ScreenshotCard({
                                   index,
                                   screenshotUrl,
                               }: Readonly<{
    index: number;
    screenshotUrl: string;
}>) {
    return (
        <SurfaceCard>
            <div className="relative aspect-video overflow-hidden rounded-[calc(var(--radius-xl)+0.5rem)]">
                <Image
                    src={screenshotUrl}
                    alt={`Screenshot ${index + 1}`}
                    fill
                    sizes="(max-width: 768px) 100vw, (max-width: 1280px) 50vw, 33vw"
                    className="object-cover"
                />
            </div>
        </SurfaceCard>
    );
}
