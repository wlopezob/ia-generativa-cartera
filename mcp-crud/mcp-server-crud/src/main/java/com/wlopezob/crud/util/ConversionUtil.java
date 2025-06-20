package com.wlopezob.crud.util;

import com.wlopezob.crud.enums.TipoPersona;
import com.wlopezob.crud.exception.FechaInvalidaException;
import com.wlopezob.crud.exception.TipoPersonaInvalidoException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ConversionUtil {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    /**
     * Convierte String a LocalDate
     */
    public static LocalDate stringToLocalDate(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new FechaInvalidaException("La fecha no puede estar vacía");
        }
        
        try {
            return LocalDate.parse(fecha.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            throw new FechaInvalidaException(fecha, e);
        }
    }
    
    /**
     * Convierte LocalDate a String
     */
    public static String localDateToString(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.format(FORMATTER);
    }
    
    /**
     * Convierte String a LocalDateTime
     */
    public static LocalDateTime stringToLocalDateTime(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new FechaInvalidaException("La fecha y hora no puede estar vacía");
        }
        
        try {
            return LocalDateTime.parse(fecha.trim(), DATETIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new FechaInvalidaException(fecha, e);
        }
    }
    
    /**
     * Convierte LocalDateTime a String
     */
    public static String localDateTimeToString(LocalDateTime fecha) {
        if (fecha == null) {
            return null;
        }
        return fecha.format(DATETIME_FORMATTER);
    }
    
    /**
     * Convierte String a TipoPersona
     */
    public static TipoPersona stringToTipoPersona(String tipoPersona) {
        if (tipoPersona == null || tipoPersona.trim().isEmpty()) {
            throw new TipoPersonaInvalidoException("El tipo de persona no puede estar vacío");
        }
        
        try {
            return TipoPersona.valueOf(tipoPersona.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new TipoPersonaInvalidoException(tipoPersona, e);
        }
    }
    
    /**
     * Convierte TipoPersona a String
     */
    public static String tipoPersonaToString(TipoPersona tipoPersona) {
        if (tipoPersona == null) {
            return null;
        }
        return tipoPersona.name();
    }
}
