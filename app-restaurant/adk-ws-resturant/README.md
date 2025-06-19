```bash
uvicorn app.main:app --host 0.0.0.0 --port 8081 --reload
```

## Notas importantes

Si experimentas problemas con la conexión WebSocket (se queda pegado o se cierra inesperadamente):

1. Asegúrate de usar el comando correcto como se muestra arriba.
2. Verifica que tu archivo `.env` tenga todas las variables de entorno necesarias.
3. Para entornos de producción, considera usar:
   ```bash
   uvicorn app.main:app --host 0.0.0.0 --port 8081
   ```
   (sin el parámetro `--reload`).
