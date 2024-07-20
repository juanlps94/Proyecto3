El problema se abordo de tal manera que existe una sola cola general para acceder a los recursos criticos (TAQUILLAS).

Lo primero fue definir que tipo de estructura se usaria de forma intuitiva se escogio una LinkedBlockingQueue, la cual se definieron 2 colas una cola general y otra solo para Magallanes, cabe acotar que estas cola estan ordenadas por orden de llegada quiere decir que si llegaron, Caracas1 Magallanes1 Magallanes2 Caracas2 Caracas3, en ese orden estaran ordenados en la "colaEspera" y en "colaMagallanes" Magallanes1 Magallanes2 Magallanes3.

Los metodos comprar y cancelar del monitor (Taquilla) indiferentemente almacenan el hilo (fanatico) en la colaEspera y si es de Magallanes tambien en colaMagallanes
se inicia un ciclo infinito dentro del cual se comprueban diferentes cosas.

La primera: Que haya prioridad para comprar, el mismo es una bandera que es controlada por un timer que se activa cada cierto tiempo y cambia el valor a true o false dependiendo del caso.

La segunda: Que haya taquilla disponibles

La tercera: Que el cliente que quiera tomar la taquilla sea la cabeza de la cola, si hay prioridad comprueba la cabeza de colaMagallanes.

Luego me apropio de una taquilla disponible y se ejecuta la logica de compra (restar X cantidad de tickets) y si el fanatico va a cancelar de forma analoga con el metodo propio, una vez finalizada la logica se hace un break para salir del ciclo infinito

El metodo liberar controla las taquillas disponibles (suma 1 cuando el fanatico termina su tramite) y notifica a todos los hilos que se libero una taquilla, aunado a lo anterior, el que tomara esa taquilla es el primero que esta en la cola.

La clase <Fan> controla el comportamiento de los fanaticos (hilos) con sus dos constructures asociados, el primero si el fanatico comprara se establecen los parametros: Id, Equipo, Si es compra en grupo, tickets a comprar y el monitor. En caso contrario el fanatico devolvera entrada: Id, equipo establecido en "cancelar" manejado por caso, tickets a devolver y el monitor.

La logica es simple, se maneja el caso de equipos y cancelar, una vez establecido el caso y su contructor, se verifica la bandera accion (la que maneja el caso cancelar), una vez en el caso necesario, se verifica si es una compra en grupo, y se procede a ejecutar el metodo comprar se duerme el hilo (para simular el tiempo en taquilla) y ejecuto el metodo liberar, para notificar que termine el tramite.

En el main, se toma un archivo por consola en caso de que no se pase, toma un valor por defecto "casoprueba.txt" la primera linea contiene la cantidad de tickets, por lo tanto, se guarda en un int, las siguientes contienen la informacion de los hilos, por tanto se usa el metodo split para separar por el caracter ',' y se setean los casos con los constructures de <Fan> para cada caso como se explico anteriormente, se crear los hilos y se lanzan con .start()