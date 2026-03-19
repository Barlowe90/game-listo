/* igual que globals pero para que lo entienda TS */
const colors = {
  primary: 'var(--color-primary)',
  primaryHover: 'var(--color-primary-hover)',
  primaryActive: 'var(--color-primary-active)',
  primaryForeground: 'var(--color-primary-foreground)',
  primarySoft: 'var(--color-primary-soft)',

  secondary: 'var(--color-secondary)',
  secondaryHover: 'var(--color-secondary-hover)',
  secondaryActive: 'var(--color-secondary-active)',
  secondaryForeground: 'var(--color-secondary-foreground)',
  secondarySoft: 'var(--color-secondary-soft)',

  background: 'var(--color-background)',
  surface: 'var(--color-surface)',
  card: 'var(--color-card)',
  overlay: 'var(--color-overlay)',

  border: 'var(--color-border)',
  borderStrong: 'var(--color-border-strong)',

  textPrimary: 'var(--color-text-primary)',
  textSecondary: 'var(--color-text-secondary)',
  textMuted: 'var(--color-text-muted)',
  textInverse: 'var(--color-text-inverse)',

  success: 'var(--color-success)',
  successSoft: 'var(--color-success-soft)',
  successForeground: 'var(--color-success-foreground)',

  warning: 'var(--color-warning)',
  warningSoft: 'var(--color-warning-soft)',
  warningForeground: 'var(--color-warning-foreground)',

  error: 'var(--color-error)',
  errorSoft: 'var(--color-error-soft)',
  errorForeground: 'var(--color-error-foreground)',

  info: 'var(--color-info)',
  infoSoft: 'var(--color-info-soft)',
  infoForeground: 'var(--color-info-foreground)',
} as const;

const spacing = {
  1: 'var(--space-1)',
  2: 'var(--space-2)',
  3: 'var(--space-3)',
  4: 'var(--space-4)',
  6: 'var(--space-6)',
  8: 'var(--space-8)',
  10: 'var(--space-10)',
  12: 'var(--space-12)',
  16: 'var(--space-16)',
} as const;

const radius = {
  sm: 'var(--radius-sm)',
  md: 'var(--radius-md)',
  lg: 'var(--radius-lg)',
  xl: 'var(--radius-xl)',
  pill: 'var(--radius-pill)',
} as const;

const shadows = {
  surface: 'var(--shadow-surface)',
  elevated: 'var(--shadow-elevated)',
  overlay: 'var(--shadow-overlay)',
} as const;

const typography = {
  fontFamilies: {
    sans: 'var(--font-sans)',
    mono: 'var(--font-mono)',
  },
  fontSizes: {
    xs: 'var(--font-size-xs)',
    sm: 'var(--font-size-sm)',
    md: 'var(--font-size-md)',
    lg: 'var(--font-size-lg)',
    xl: 'var(--font-size-xl)',
    '2xl': 'var(--font-size-2xl)',
    '3xl': 'var(--font-size-3xl)',
  },
  fontWeights: {
    regular: 'var(--font-weight-regular)',
    medium: 'var(--font-weight-medium)',
    semibold: 'var(--font-weight-semibold)',
    bold: 'var(--font-weight-bold)',
  },
  lineHeights: {
    tight: 'var(--line-height-tight)',
    normal: 'var(--line-height-normal)',
    relaxed: 'var(--line-height-relaxed)',
  },
} as const;

const opacity = {
  disabled: 'var(--opacity-disabled)',
  loading: 'var(--opacity-loading)',
} as const;

const focusRing = {
  color: 'var(--focus-ring-color)',
  width: 'var(--focus-ring-width)',
  offset: 'var(--focus-ring-offset)',
} as const;

const motion = {
  durations: {
    fast: 'var(--duration-fast)',
    normal: 'var(--duration-normal)',
    slow: 'var(--duration-slow)',
  },
  easing: {
    standard: 'var(--easing-standard)',
    emphasized: 'var(--easing-emphasized)',
  },
} as const;

const layout = {
  containerWidths: {
    narrow: 'var(--container-width-narrow)',
    default: 'var(--container-width-default)',
    wide: 'var(--container-width-wide)',
  },
  containerGutters: {
    mobile: 'var(--container-gutter-mobile)',
    tablet: 'var(--container-gutter-tablet)',
    desktop: 'var(--container-gutter-desktop)',
  },
  containerMaxWidth: 'var(--container-max-width)',
  targetMinSize: 'var(--target-min-size)',
} as const;

const breakpoints = {
  mobile: 0,
  tablet: 768,
  desktop: 1024,
  wide: 1280,
} as const;

export const tokens = {
  colors,
  spacing,
  radius,
  shadows,
  typography,
  opacity,
  focusRing,
  motion,
  layout,
  breakpoints,
} as const;

export type ColorToken = keyof typeof colors;
export type SpacingToken = keyof typeof spacing;
export type RadiusToken = keyof typeof radius;
export type ShadowToken = keyof typeof shadows;
export type FontFamilyToken = keyof typeof typography.fontFamilies;
export type FontSizeToken = keyof typeof typography.fontSizes;
export type FontWeightToken = keyof typeof typography.fontWeights;
export type LineHeightToken = keyof typeof typography.lineHeights;
export type OpacityToken = keyof typeof opacity;
export type DurationToken = keyof typeof motion.durations;
export type EasingToken = keyof typeof motion.easing;
export type ContainerWidthToken = keyof typeof layout.containerWidths;
export type ContainerGutterToken = keyof typeof layout.containerGutters;
export type BreakpointToken = keyof typeof breakpoints;
