# Implementar backend apis

- Implementar el endpoint /api/restaurant/chat/memory que esta en el contrato [api.yaml](mdc:openapi/api.yaml)
- La consulta al endpoint se realiza cuando el usuario envia un mensaje al chat, seleccionando el uuid del chat para el sessionId del request, y el message, enviando estos datos a consultar el endpoint.
- la respuesta del endpoint tiene que mostrarse en el chat con el nombre de cliente con su respectiva imagen de un robot de ia, que use el mismo diseño que actualmente ya existe para los mensajes de chat.
- Crea o actualiza los interfaces, services y todos los componentes organizadamente y de acuerdo a las buenas practicas, no crees variables de tipo any, todo debe estar correctamente tipificado.
- usa signals si es convenientemente necesario.