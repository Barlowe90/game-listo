import { EmptyState } from '@/shared/components/ui/EmptyState';
import { TabsContent } from '@/shared/components/ui/Tabs';
import { ScreenshotCard, VideoEmbedCard } from './GameDetailShared';

export function GameDetailVideosTab({ videos }: Readonly<{ videos: string[] | null | undefined }>) {
  return (
    <TabsContent value="videos">
      {videos?.length ? (
        <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
          {videos.map((videoUrl, index) => (
            <VideoEmbedCard key={`${videoUrl}-${index}`} index={index} videoUrl={videoUrl} />
          ))}
        </div>
      ) : (
        <EmptyState title="No hay videos disponibles por ahora" />
      )}
    </TabsContent>
  );
}

export function GameDetailScreenshotsTab({
  screenshots,
}: Readonly<{
  screenshots: string[] | null | undefined;
}>) {
  return (
    <TabsContent value="screenshots">
      {screenshots?.length ? (
        <div className="grid gap-5 md:grid-cols-2 xl:grid-cols-3">
          {screenshots.map((screenshotUrl, index) => (
            <ScreenshotCard
              key={`${screenshotUrl}-${index}`}
              index={index}
              screenshotUrl={screenshotUrl}
            />
          ))}
        </div>
      ) : (
        <EmptyState title="No hay screenshots disponibles" />
      )}
    </TabsContent>
  );
}
