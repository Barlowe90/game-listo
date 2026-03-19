export const PUBLICACION_IDIOMA_OPTIONS = [
  { value: 'ESP', label: 'Espanol' },
  { value: 'ENG', label: 'Ingles' },
] as const;

export type PublicacionIdioma = (typeof PUBLICACION_IDIOMA_OPTIONS)[number]['value'];

export const PUBLICACION_EXPERIENCIA_OPTIONS = [
  { value: 'NOOB', label: 'Noob' },
  { value: 'NOVATO', label: 'Novato' },
  { value: 'MEDIO', label: 'Medio' },
  { value: 'EXPERIMENTADO', label: 'Experimentado' },
  { value: 'PRO', label: 'Pro' },
] as const;

export type PublicacionExperiencia = (typeof PUBLICACION_EXPERIENCIA_OPTIONS)[number]['value'];

export const PUBLICACION_ESTILO_JUEGO_OPTIONS = [
  { value: 'DISFRUTAR_DEL_JUEGO', label: 'Disfrutar del juego' },
  { value: 'LOGROS', label: 'Logros' },
] as const;

export type PublicacionEstiloJuego = (typeof PUBLICACION_ESTILO_JUEGO_OPTIONS)[number]['value'];

export const PUBLICACION_DIAS = [
  { value: 'LUNES', label: 'Lunes', shortLabel: 'L' },
  { value: 'MARTES', label: 'Martes', shortLabel: 'M' },
  { value: 'MIERCOLES', label: 'Miercoles', shortLabel: 'X' },
  { value: 'JUEVES', label: 'Jueves', shortLabel: 'J' },
  { value: 'VIERNES', label: 'Viernes', shortLabel: 'V' },
  { value: 'SABADO', label: 'Sabado', shortLabel: 'S' },
  { value: 'DOMINGO', label: 'Domingo', shortLabel: 'D' },
] as const;

export type PublicacionDiaSemana = (typeof PUBLICACION_DIAS)[number]['value'];

export const PUBLICACION_FRANJAS = [
  { value: 'DIA', label: 'Manana', shortLabel: 'M' },
  { value: 'TARDE', label: 'Tarde', shortLabel: 'T' },
  { value: 'NOCHE', label: 'Noche', shortLabel: 'N' },
] as const;

export type PublicacionFranjaHoraria = (typeof PUBLICACION_FRANJAS)[number]['value'];

export type PublicacionDisponibilidad = Partial<
  Record<PublicacionDiaSemana, PublicacionFranjaHoraria[]>
>;

export interface Publicacion {
  id: string;
  autorId: string;
  gameId: string;
  titulo: string;
  idioma: PublicacionIdioma;
  experiencia: PublicacionExperiencia;
  estiloJuego: PublicacionEstiloJuego;
  jugadoresMaximos: number;
  grupoId: string | null;
  disponibilidad: PublicacionDisponibilidad | null;
}

export interface CrearPublicacionPayload {
  gameId: number;
  titulo: string;
  idioma: PublicacionIdioma;
  experiencia: PublicacionExperiencia;
  estiloJuego: PublicacionEstiloJuego;
  jugadoresMaximos: number;
  disponibilidad: PublicacionDisponibilidad;
}

export interface EditarPublicacionPayload {
  titulo: string;
  idioma: PublicacionIdioma;
  experiencia: PublicacionExperiencia;
  estiloJuego: PublicacionEstiloJuego;
  jugadoresMaximos: number;
  disponibilidad: PublicacionDisponibilidad;
}
