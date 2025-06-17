# Configuración CORS - Implementación Simplificada

Este documento explica cómo está configurado CORS en el proyecto sin usar `@CrossOrigin` en los controladores.

## Configuración Actual: WebFluxConfigurer

El proyecto usa una **única configuración CORS** en `WebFluxConfig.java`:

```java
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // Permite todos los orígenes
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(true)  // Permite cookies y credenciales
                .maxAge(3600);  // Cache preflight por 1 hora
    }
}
```

### Ventajas de esta implementación:
- ✅ **Una sola configuración** - No hay duplicación
- ✅ **Configuración centralizada** - Todo en un lugar
- ✅ **Fácil de mantener** - Cambios en un solo archivo
- ✅ **Integrada con WebFlux** - Usa la configuración nativa

## Opción 3: Para Producción (RECOMENDADO)

Para producción, modifica los orígenes permitidos:

```java
// En lugar de:
.allowedOriginPatterns("*")

// Usa orígenes específicos:
.allowedOrigins("https://mi-frontend.com", "https://admin.mi-app.com")
```

## Configuración Actual

El proyecto está configurado con:

- ✅ **Sin `@CrossOrigin`** en controladores
- ✅ **Configuración global** en `WebFluxConfig`
- ✅ **Todos los orígenes** permitidos (desarrollo)
- ✅ **Credenciales** habilitadas
- ✅ **Cache preflight** de 1 hora

## Verificación CORS

Para probar que CORS funciona:

```bash
# Test preflight (OPTIONS)
curl -X OPTIONS http://localhost:8085/api/personas \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: POST" \
  -v

# Test GET con origen
curl -X GET http://localhost:8085/api/personas \
  -H "Origin: http://localhost:3000" \
  -v
```

## Seguridad

⚠️ **Importante para Producción:**

1. **No uses `"*"` en producción** - especifica dominios exactos
2. **Revisa `allowCredentials(true)`** - solo si necesitas cookies/auth
3. **Limita métodos HTTP** - solo los que realmente uses
4. **Configura headers específicos** - evita `"*"` si es posible

## Troubleshooting

Si CORS no funciona:

1. ✓ Verifica que no hay `@CrossOrigin` en controladores
2. ✓ Asegúrate que `WebFluxConfig` esté siendo cargado
3. ✓ Revisa logs para errores de CORS
4. ✓ Usa herramientas del navegador para verificar headers
