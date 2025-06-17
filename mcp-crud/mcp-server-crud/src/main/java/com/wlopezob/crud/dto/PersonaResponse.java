package com.wlopezob.crud.dto;

import lombok.Data;

@Data
public class PersonaResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private Integer edad;
    private String fecha; // Formato: dd/MM/yyyy
    private String tipoPersona; // String del enum
    private String dni;
}
