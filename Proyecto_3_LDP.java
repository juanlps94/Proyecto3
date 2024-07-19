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
//* Cantidad máxima de tickets disponibles
    int cantTicketsMAX;
//* Cantidad actual de tickets disponibles
    int cantTicketsACT;
//* Cantidad de taquillas disponibles (5 MAX)
    int disponible;
//* Cola de espera para los hilos
    BlockingQueue<Fan> colaEspera;

    public Taquillas(int num) {
        this.cantTicketsMAX = num;
        this.cantTicketsACT = num;
        this.disponible = 5;
        this.colaEspera = new LinkedBlockingQueue<>(5);
    }

    public int getTicketsMAX(){
        return this.cantTicketsMAX;
    }

    public int getTicketsACT(){
        return this.cantTicketsACT;
    }

    public synchronized void comprar(Fan cliente) {
        System.out.println("Turno ("+cliente.id+")");
        System.out.println("Quedan "+this.cantTicketsACT+" tickets disponibles");
        System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") comprando "+cliente.compra+" tickets");

        while (this.disponible == 0) {};    //? Espera hasta que haya una taquilla disponible

        try {
            this.disponible--;
            System.out.println("Taquillas disponibles: "+this.disponible);
            this.colaEspera.put(cliente);

            if(this.cantTicketsACT - cliente.compra < 0) {
                System.out.println("No hay tickets suficientes");
            }
            else{
                this.cantTicketsACT -= cliente.compra;
                System.out.println("Compra exitosa ... Esperando");
            }
            Thread.sleep(1000);
                
            this.colaEspera.take();
            this.disponible++;
            System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") liberando la taquilla");
            System.out.println("Taquillas disponibles: "+this.disponible+"\n");
        } catch (InterruptedException e) {e.printStackTrace();}
    }

    public synchronized void cancelar(Fan cliente) {
        System.out.println("Turno ("+cliente.id+")");
        System.out.println("Quedan "+this.cantTicketsACT+" tickets disponibles");
        System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") devolviendo "+cliente.compra+" tickets");
        try {
            this.disponible--;
            this.colaEspera.put(cliente);
            
            System.out.println("Taquillas disponibles: "+this.disponible);

            if(this.cantTicketsACT + cliente.compra > this.cantTicketsMAX){
                System.err.println("La cantidad de tickets a devolver excede el máximo");
            }
            else{
                this.cantTicketsACT += cliente.compra;
                System.out.println("Devolución exitosa, esperando ...");
            }

            Thread.sleep(1000);
                
            this.colaEspera.take();
            this.disponible++;
            System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") liberando la taquilla");
            System.out.println("Taquillas disponibles: "+this.disponible+"\n");

        } catch (InterruptedException e) {e.printStackTrace();}
    }
}

class Fan implements Runnable {
    int id;
    String equipo;
    int compra;
    boolean action;
    Taquillas myTaquilla;

    public Fan(int id, int eq, boolean groupF, int tickets, Taquillas inTaquilla) {
        this.id = id;
        this.action = false;
        this.myTaquilla = inTaquilla;

        if (eq == 0) {
            this.equipo = "Caracas";
        }
        else{
            this.equipo = "Magallanes";
        }

        if (groupF) {
            this.compra = tickets;
        }
        else {
            this.compra = 1;
        }
    }

    public Fan(int id, int tickets, Taquillas inTaquilla) {
        this.id = id;
        this.action = true;
        this.myTaquilla = inTaquilla;
        this.compra = tickets;
    }

    @Override
    public void run() {
        if (this.action) {
            this.myTaquilla.cancelar(this);
        }
        else {
            this.myTaquilla.comprar(this);
        }
    }
}

public class Proyecto_3_LDP {
    public static void main(String[] args) {
        /*
         ?  El nombre del archivo a leer se debe pasar como parámetro al programa
         ?  para eso vamos a usar args, la primera posición corresponde al primer
         ?  parámetro, por lo tanto se almacena en "filename".
         ?  En futuras versiones se debe sustituir "casosprueba.txt" por la variable
         ?  filename. 
        */

        int id = 1;
        String filename = args[0];
        
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader buffer = new BufferedReader(fr);
            String linea;

            Taquillas T = new Taquillas(Integer.parseInt(linea = buffer.readLine()));

            while((linea = buffer.readLine()) != null) {
                String[] partes = linea.split(",");

                switch (partes[0]) {
                    case "Caracas":
                        if (partes[1].equals("grupo")) {
                            Fan cliente = new Fan(id, 0, true, Integer.parseInt(partes[2]), T);
                            Thread t1 = new Thread(cliente);
                            t1.start();
                        }
                        else {
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
                        }
                        else {
                            Fan cliente = new Fan(id, 1, false, Integer.parseInt(partes[1]), T);
                            Thread t3 = new Thread(cliente);
                            t3.start();
                        }
                    break;

                    case "cancelar":
                        Fan cliente = new Fan(id, Integer.parseInt(partes[1]), T);
                        Thread t4 = new Thread(cliente);
                        t4.start();
                    break;
                }

                //* Se pone a dormir al hilo main para dar chance a los hilos de ejecutarse */
                try{
                    Thread.sleep(0);
                }
                catch(InterruptedException e){};
                id++;
            }
            buffer.close();
        } catch (IOException e) {}
    }
}