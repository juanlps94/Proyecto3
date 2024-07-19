import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
 ? Documentación para las BlockingQueue:
 *  https://www.baeldung.com/java-blocking-queue
 */

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Taquillas {
    // * Cantidad máxima de tickets disponibles
    int cantTickets;
    // * Cantidad actual de tickets disponibles
    int cantTicketsACT;
    // * Cantidad de taquillas disponibles (5 MAX)
    int disponible;
    // * Cola de espera para los hilos
    BlockingQueue<Fan> colaEspera = new LinkedBlockingQueue<>();

    public Taquillas(int num) {
        this.cantTickets = num;
        this.disponible = 5;
    }

    public synchronized void comprar(Fan cliente) {
        // * Todas las taquillas están ocupadas
        if (this.disponible == 0) {
            System.out.println("Taquilla no disponible para "+cliente.id+" comprar "+cliente.compra+" Colocandose en la cola");
            try {
                this.colaEspera.put(cliente);
                System.out.println("La cola ordenada actualmente es: ");
                for (Fan i :  this.colaEspera) {
                    System.out.println(i.id);
                }
                wait();
            } catch (InterruptedException e) {}
        }
        while(this.disponible == 0) {
                try {
                    for (Fan i :  this.colaEspera) {
                        System.out.println(i.id);
                    }
                    wait();
                } catch (InterruptedException e) {}
            }
        this.disponible--;

        // * La compra excede la cantidad de tickets
        if (this.cantTickets - cliente.compra < 0) {
            System.out.println(
                    "No hay tickets suficientes para que " + cliente.id + " compre " + cliente.compra + " entradas");
        }
        // ! Se proceden a comprar los tickets, ocupando la taquilla
        else {
            System.out.println("Fanatico " + cliente.id + " Comprando " + cliente.compra + " tickets ...");
                this.cantTickets -= cliente.compra;
        }

    }

    public synchronized void cancelar(Fan cliente) {
        // * Todas las taquillas están ocupadas
        if (this.disponible == 0) {
        System.out.println("Taquilla no disponible para " + cliente.id + " cancelar " + cliente.compra + " Colocandose en la cola");
        try {
            this.colaEspera.put(cliente);
            System.out.println("La cola ordenada actualmente es: ");
                    for (Fan i :  this.colaEspera) {
                        System.out.println(i.id);
                    }
            wait();
        } catch (InterruptedException e) {}
        }
        while (this.disponible == 0) {
            try {
                System.out.println("La cola ordenada actualmente es: ");
                    for (Fan i :  this.colaEspera) {
                        System.out.println(i.id);
                    }
                    wait();
                } catch (InterruptedException e) {}
            }

       // ! Se realiza la devolución de los tickets, ocupando la taquilla
        this.disponible--;
        System.out.println(cliente.id + " Cancelando " + cliente.compra + " tickets ...");
        this.cantTickets += cliente.compra;

    }

    public synchronized void liberar(Fan cliente) {
        /*
        !       Se libera la taquilla ocupada y se notifica a cualquiera esperando
        TODO:   Validar que la cantidad de taquillas disponibles no exceda las 5
        */
                System.out.println("El fanatico "+cliente.id+" saliendo de la taquilla");
                if(!(this.colaEspera.isEmpty())){
                    try {
                        this.disponible++;
                        this.colaEspera.take();
                        notifyAll();
                    } catch (InterruptedException e) {}
                }
            }

}

class Fan implements Runnable {
    int id;
    int equipo;
    int compra;
    boolean action;
    Taquillas myTaquilla;

    public Fan(int id, int eq, boolean groupF, int tickets, Taquillas inTaquilla) {
        this.id = id;
        this.equipo = eq;
        this.action = false;
        this.myTaquilla = inTaquilla;

        if (groupF) {
            this.compra = tickets;
        } else {
            this.compra = 1;
        }
    }

    public Fan(int id, int equi, int tickets, Taquillas inTaquilla) {
        this.action = true;
        this.myTaquilla = inTaquilla;
        this.id = id;
        this.equipo = equi;
        this.compra = tickets;
    }

    @Override
    public void run() {
        if (this.action) {

            System.out.println("Fanatico " + this.id + " devolvera entradas: " + this.compra);
            System.out.println("Hay "+this.myTaquilla.disponible+ " taquillas");
            this.myTaquilla.cancelar(this);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {}

           this.myTaquilla.liberar(this);

        } else {
            if (this.equipo == 0) {
                System.out.println("Fanatico " + this.id + " del Caracas quiere comprar: " + this.compra);
            } else {
                System.out.println("Fanatico " + this.id + " del Magallanes quiere comprar: " + this.compra);
            }
            System.out.println("Hay "+this.myTaquilla.disponible+ " taquillas");
            this.myTaquilla.comprar(this);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {}
           this.myTaquilla.liberar(this);

        }
    }
}

public class Proyecto_3_LDP {
    public static void main(String[] args) {
        /*
         * ? El nombre del archivo a leer se debe pasar como parámetro al programa
         * ? para eso vamos a usar args, la primera posición corresponde al primer
         * ? parámetro, por lo tanto se almacena en "filename".
         * ? En futuras versiones se debe sustituir "casosprueba.txt" por la variable
         * ? filename.
         */
        String filename;
        if(args.length==0){
            filename="casoprueba.txt";
        }else{
            filename = args[0];
        }

        try {
            FileReader fr = new FileReader(filename);
            BufferedReader buffer = new BufferedReader(fr);
            String linea;

            Taquillas T = new Taquillas(Integer.parseInt(linea = buffer.readLine()));
            int id = 1;
            while ((linea = buffer.readLine()) != null) {
                String[] partes = linea.split(",");

                switch (partes[0]) {
                    case "Caracas":
                        if (partes[1].equals("grupo")) {
                            Fan cliente = new Fan(id, 0, true, Integer.parseInt(partes[2]), T);
                            Thread t1 = new Thread(cliente);
                            t1.start();
                        } else {
                            Fan cliente = new Fan(id, 0, false, Integer.parseInt(partes[1]), T);
                            Thread t1 = new Thread(cliente);
                            t1.start();
                        }

                        break;

                    case "Magallanes":
                        if (partes[1].equals("grupo")) {
                            Fan cliente = new Fan(id, 1, true, Integer.parseInt(partes[2]), T);
                            Thread t2 = new Thread(cliente);
                            t2.start();
                        } else {
                            Fan cliente = new Fan(id, 1, false, Integer.parseInt(partes[1]), T);
                            Thread t3 = new Thread(cliente);
                            t3.start();
                        }
                        break;

                    case "cancelar":
                        Fan cliente = new Fan(id, 2, Integer.parseInt(partes[1]), T);
                        Thread t4 = new Thread(cliente);
                        t4.start();
                        break;
                }
                id++;
            }

            buffer.close();
        } catch (IOException e) {
        }
    }
}