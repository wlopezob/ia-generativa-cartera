package com.wlopezob.crud.entity;

import com.wlopezob.crud.enums.TipoPersona;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("personas")
public class Persona {
    
    @Id
    private Long id;
    
    private String nombre;
    
    private String apellido;
    
    private Integer edad;
    
    private LocalDate fecha;
    
    private TipoPersona tipoPersona;
    
    private String dni;
}
