package com.wlopezob.crud.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonaListResponse {
    private List<PersonaResponse> personas;
    private Integer total;
    private LocalDateTime timestamp;
    private String mensaje;
}
