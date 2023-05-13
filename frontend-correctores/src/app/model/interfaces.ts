
export interface Corrector {
  id: bigint;
  identificadorUsuario: bigint;
  telefono: string;
  maximasCorrecciones: number;
  materias: MateriaEnConvocatoria[];
}

export interface MateriaEnConvocatoria {
  idMateria: bigint;
  idConvocatoria: bigint;
}

// TODO: De cara al formulario, ver que campos son obligatorios y cuales no
export interface CorrectorNuevo {
  identificadorUsuario: bigint;
  identificadorConvocatoria?: bigint;
  telefono: string;
  materia: bigint | string;
  maximasCorrecciones: number;
}