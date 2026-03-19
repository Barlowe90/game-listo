import { Container, type ContainerProps } from '@/shared/components/layout/Container';

export type PageContainerProps = ContainerProps;

export function PageContainer(props: PageContainerProps) {
  return <Container {...props} />;
}
