package sii.ms_evalexamenes.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstadoCorrecionesDTO {
    
    private List<Long> corregidos;
    private List<Long> pendientes;
}
