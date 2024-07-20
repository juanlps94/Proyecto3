## INFORME PROYECTO #3 Lenguajes de Programación (6204)
Integrantes: Daniel Hernandez, CI: 27795163. Juan Pérez, CI: 21407463.

## ANÁLISIS DEL PROBLEMA
El problema se abordó de tal manera que exista una sola cola general para acceder a las taquillas para comprar las entradas (recursos críticos).

Lo primero que se hizo fue definir que tipo de estructura se usaría para representar esta cola, se escogió usar una LinkedBlockingQueue, la cual es una implementación de BlockingQueue que es una cola con métodos Thread-safe. Posteriormente se definió una segunda cola solo para fanáticos del Magallanes, cabe acotar que estas cola están ordenadas por orden de llegada, es decir, si llegaron: Caracas1, Magallanes1, Magallanes2, Caracas2, Caracas3. El orden que tendrán al momento de almacenarse será:

colaEspera = <Caracas1, Magallanes1, Magallanes2, Caracas2, Caracas3>
colaMagallanes = <Magallanes1, Magallanes2, Magallanes3>

Los métodos definidos para el monitor: public synchronized void comprar() y public synchronized void cancelar(). Almacenan el fanático (hilo) en la colaEspera y si es fanático del Magallanes también en colaMagallanes, al momento de ejecutarse cualquier de estos métodos se inicia un bucle infinito dentro del cual se comprueban diferentes cosas.

Primera comprobación: Que haya prioridad para comprar, para esto se usa la bandera el método private void iniciarVIP(), el cual se activa siempre al inicio de cada ejecución y posteriormente se repite cada cierto tiempo (15 segundos), su función es cambiar el valor de la bandera "boolean timer" a true y luego a false pasado un periodo de tiempo (10 segundos).

Segunda comprobación: Que haya taquillas disponibles para realizar compras o devoluciones.

Tercera comprobación: si un fanático desea tomar la taquilla, este debe estar ubicado al frente de la cola correspondiente, si hay prioridad comprueba colaMagallanes. Cabe mencionar que, en este caso dado que los hilos se encuentran en colaEspera y colaMagallanes, se deben eliminar también de colaEspera.

## COMPORTAMIENTO GENERAL
Cuando un fanático se apropia de una taquilla disponible, se ejecuta la lógica correspondiente a la compra (restar una cantidad válidas de tickets) o a la devolución (sumar una cantidad válida de tickets), una vez finalizado se hace un break para salir del bucle.
El método liberar controla las taquillas disponibles (suma 1 cuando el fanático termina su tramite) y notifica a todos los hilos que se liberó una taquilla, aunado a lo anterior, el que tomara esa taquilla es el primero que esta en la cola correspondiente al momento.

La clase <Fan> controla el comportamiento de los fanáticos (hilos) con sus dos constructores asociados, el primero si el fanático comprará tickets se establecen los parámetros: Id, Equipo, Si es compra en grupo, tickets a comprar y el monitor. En caso contrario, el fanático devolverá tickets, se inicializan los parámetros: Id, equipo (establecido en "cancelar" por control), tickets a devolver y el monitor.

La lógica general es simple, se maneja el caso de equipos o cancelar, una vez establecido el caso y su constructor, se verifica la bandera "boolean action", la cual maneja el caso cancelar (true). Por otro lado, para las compras, se verifica si es una compra en grupo, y se procede a ejecutar el método comprar, una vez finalizado se duerme el hilo (para simular el tiempo en taquilla) y se ejecuta el método liberar, para notificar que el tramite ha finalizado.

En la clase main, se toma un archivo por consola, en caso de que no se pase, toma un valor por defecto "casoprueba.txt", el cual contiene un caso de prueba por defecto ubicado en la misma en la misma ruta que el código fuente. La primera línea de este archivo contiene la cantidad máxima de tickets disponibles, por lo tanto, se guarda en una variable de tipo int, las siguientes líneas contienen la información de los fanáticos, para capturar las misma se usa el método split() para separar por comas (",") y se llaman a los constructores de <Fan> para cada caso, luego se crean los hilos y se lanzan con una llamada a start().