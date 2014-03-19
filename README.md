APP-Android
===========
1. Varias actividades con intercambio de datos entre ellas
Tenemos varias actividades y entre algunas de ellas se intercambian datos.
- Splash, que es la pantalla de inicialización
- Home, donde se produce la mayoría de acciones sobre la aplicación
- Date, que retorna la fecha y hora por medio del DatePicker y del TimePicker
- ListNotes, otra actividad donde creamos un adaptador para mostrar la lista de notas con su correspondiente imagen. Además este tiene la opción de poder eliminar las notas y la lista se actualiza inmediatamente

2. Manejo y gestión de imágenes
Está opción la manejamos en la actividad Home por medio del acceso a la cámara del teléfono y el acceso al almacén de imágenes del teléfono, obtenemos la dirección del Path que luego se incluirá como dato en la nota.

3. Acceso a datos
Utilizamos para el acceso a la información, una base de datos, para almacenar en ella las notas con sus respectivos campos, para ello utilizamos SQLite, donde tenemos todas las opciones disponibles habilitadas.

4. Inclusión del ARS (Action Recognize Speech)
El ARS, está incluido también en el Home, se utiliza para poder agregar por medio del botón habilitado para ello, las notas por voz.

5. TTS (Text to Speech)
Está opción se encuentra en Home y en ListNotes, en la primera se utiliza cuando se almacenan los datos se escucha por voz que los datos estánalmacenados y en la segunda actividad, informa al usuario que se encuentra en la lista de notas.

6. La combinación y manejo de los distintos sensores
Utilización el sensor de la cámara, para utilizar la fotografía como dato a adjuntar en la nota.
Utiliza el WiFi, para la obtención de la triangulación de la posición del teléfono.
Utilización GPS del móvil para recoger la situación del punto del móvil.

7. Geolocalización
Se utiliza la geolocalización, del usuario para almacenar la dirección desde donde este creo la nota, informando al usuario de la posición por latitud y longitud y guardando como dato la dirección de esa posición en la nota creada.

8. Empaquetado de la aplicación para su posible subida al market

9. Aporte
- Utilización DialogFragment, para mostrar un dialogo, para la activación del GPS.
- Utilización Thread, para mostrar durante un tiempo una actividad y luego cerrarla (Splash).
- Utilización JavaBenas, para encapsular los datos de la nota y poder utilizarlos con SQLite en la aplicación.
- Menú de opciones con dos ítems, uno para salir de la aplicación y otro con mi nombre.

