package com.wlopezob.crud.dto;

import lombok.Data;

@Data
public class PersonaRequest {
    private String nombre;
    private String apellido;
    private Integer edad;
    private String fecha; // Formato: dd/MM/yyyy
    private String tipoPersona; // String que se convierte a enum
    private String dni;
    private Double saldo;
}
