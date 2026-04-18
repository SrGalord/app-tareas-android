## Taller 3

### Qué se implementó

Inicialmente, el proyecto consistía en una aplicación que mostraba una lista estática de personas previamente definidas. Cada persona podía ser seleccionada para visualizar su información detallada, pero no era posible agregar, editar ni eliminar registros.

Para el desarrollo del Taller 3, se realizaron los siguientes cambios:

- Se eliminó la estructura anterior relacionada con la lista de personas.
- Se implementó una nueva funcionalidad de lista de tareas.
- Se creó una interfaz que permite agregar nuevas tareas mediante un formulario.
- Se incluyeron campos como título, descripción y hora de la tarea.
- Se implementó un sistema de recordatorios mediante notificaciones.

### Opción de recordatorio

El recordatorio se implementó mediante notificaciones utilizando un BroadcastReceiver (ReminderReceiver).

Al seleccionar la hora del recordatorio, se muestra un selector de tiempo (TimePicker), el cual permite elegir la hora en formato de 24 horas (hora militar).
