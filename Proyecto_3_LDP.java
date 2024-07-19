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
    int cantTicketsMAX;
    // * Cantidad actual de tickets disponibles
    int cantTicketsACT;
    // * Cantidad de taquillas disponibles (5 MAX)
    int disponible;
    // * Cola de espera para los hilos
    BlockingQueue<Fan> colaEspera = new LinkedBlockingQueue<>();

    public Taquillas(int num) {
        this.cantTicketsMAX = num;
        this.cantTicketsACT = num;
        this.disponible = 5;
    }

    public synchronized void comprar(Fan cliente) {
        // * Todas las taquillas están ocupadas
        try {
            this.colaEspera.put(cliente);
        } catch (InterruptedException e) {}

        while (true) {
        if (this.disponible == 0) {
            System.out.println("Taquilla no disponible para "+cliente.id+" comprar "+cliente.compra+" Colocandose en la cola");
            try {
                /*System.out.println("El tamaño actual es "+this.colaEspera.size()+" y contiene: ");
                for (Fan i :  this.colaEspera) {
                    System.out.println(i.id);
                }*/
                wait();
            } catch (InterruptedException e) {}
        }
        while(this.disponible == 0 || cliente!=this.colaEspera.peek()) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }

        this.disponible--;
        if(cliente == this.colaEspera.peek()){
            if (this.cantTicketsACT - cliente.compra < 0) {
                System.out.println(
                    "No hay tickets suficientes para que " + cliente.id + " compre " + cliente.compra + " entradas");
                    this.colaEspera.poll(); //! OJO PELAO 1
                }
                // ! Se proceden a comprar los tickets, ocupando la taquilla
                else {
                    System.out.println("Fanatico " + cliente.id + " Comprando " + cliente.compra + " tickets ...");
                    this.cantTicketsACT -= cliente.compra;
                    this.colaEspera.poll();
                }
                break;
        }
        // * La compra excede la cantidad de tickets
    }
    }

    public synchronized void cancelar(Fan cliente) {
        // * Todas las taquillas están ocupadas
        try {
            this.colaEspera.put(cliente);
        } catch (InterruptedException e) {}
        
        while (true) {
            if (this.disponible == 0) {
                System.out.println("Taquilla no disponible para "+cliente.id+" cancelar "+cliente.compra+" Colocandose en la cola");
                try {
                    /*System.out.println("El tamaño actual es "+this.colaEspera.size()+" y contiene: ");
                    for (Fan i :  this.colaEspera) {
                        System.out.println(i.id);
                    }*/
                    wait();
                } catch (InterruptedException e) {}
            }
            while(this.disponible == 0 || cliente!=this.colaEspera.peek()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {}
                }
    
            this.disponible--;
            if(cliente == this.colaEspera.peek()){
                    System.out.println("Fanatico " + cliente.id + " Devolviendo " + cliente.compra + " tickets ...");
                    this.cantTicketsACT += cliente.compra;
                    this.colaEspera.poll();
                    break;
            }
            // * La compra excede la cantidad de tickets
        }
    }

    

    public synchronized void liberar(Fan cliente) {
        /*
        !       Se libera la taquilla ocupada y se notifica a cualquiera esperando
        TODO:   Validar que la cantidad de taquillas disponibles no exceda las 5
        */
        System.out.println("El fanatico "+cliente.id+" saliendo de la taquilla");
        this.disponible++;
        notifyAll();
    }

}

class Fan extends Thread {
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
            this.myTaquilla.cancelar(this);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}

           this.myTaquilla.liberar(this);

        } else {
            if (this.equipo == 0) {
                System.out.println("Fanatico " + this.id + " del Caracas quiere comprar: " + this.compra);
            } else {
                System.out.println("Fanatico " + this.id + " del Magallanes quiere comprar: " + this.compra);
            }

            
            try {
                this.myTaquilla.comprar(this);
                Thread.sleep(100);
                this.myTaquilla.liberar(this);
            } catch (InterruptedException e) {}

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
                            Thread t1  = new Fan(id, 0, true, Integer.parseInt(partes[2]), T);
                            t1.start();
                        } else {
                            Thread t1  = new Fan(id, 0, true, Integer.parseInt(partes[1]), T);
                            t1.start();
                        }

                        break;

                    case "Magallanes":
                        if (partes[1].equals("grupo")) {
                            Thread t2 = new Fan(id, 1, true, Integer.parseInt(partes[2]), T);
                            t2.start();
                        } else {
                            Thread t2 = new Fan(id, 1, false, Integer.parseInt(partes[1]), T);
                            t2.start();
                        }
                        break;

                    case "cancelar":
                        Thread t3 = new Fan(id, 2, Integer.parseInt(partes[1]), T);
                        t3.start();
                        break;
                }
                id++;
            }

            buffer.close();
        } catch (IOException e) {
        }
    }
}