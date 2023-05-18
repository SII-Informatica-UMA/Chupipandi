
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

export interface CorrectorNuevo {
  identificadorUsuario: bigint;
  identificadorConvocatoria?: bigint;
  telefono: string;
  materia: Materia;
  maximasCorrecciones: number;
}

export interface Materia {
  materia: bigint | string;
}