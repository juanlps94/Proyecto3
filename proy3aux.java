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
        this.colaEspera = new LinkedBlockingQueue<>();
    }

    public synchronized void comprar(Fan cliente) {
//*     Todas las taquillas están ocupadas
        if (this.disponible == 0) {
            System.out.println("Hilo ("+cliente.id+")");
            System.out.println("Ninguna taquilla disponible ... Entrando a la cola");
            
            while(this.disponible == 0) {
                try {
                    this.colaEspera.put(cliente);
                    wait();
                } catch (InterruptedException e) {}
            } 
        }
        
        this.disponible--;

        System.out.println("Hilo ("+cliente.id+")");
        System.out.println("Quedan "+this.cantTicketsACT+" tickets disponibles");

        if (this.cantTicketsACT - cliente.compra < 0) {
            System.out.println("No hay tickets suficientes para la compra\n");
            this.disponible++;
        }      
        else {
            System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") comprando "+cliente.compra+" tickets ...");
            System.out.println("Taquillas disponibles: "+this.disponible);
            
            try {
                System.out.println("Pagando ...");
                this.cantTicketsACT -= cliente.compra;

                Thread.sleep(3000);
                
                System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") saliendo de la taquilla");

                this.disponible++;
                System.out.println("Taquillas disponibles: "+this.disponible+".\n");
                
            } catch (InterruptedException e) {}
        }

        if(!(this.colaEspera.isEmpty())){
            Fan fanatico= this.colaEspera.poll();
            System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") despertando a ("+fanatico.id+").\n");
            notifyAll();
        }  
    } 

    public synchronized void cancelar(Fan cliente) {
//*     Todas las taquillas están ocupadas 
        if (this.disponible == 0) {
            System.out.println("Hilo ("+cliente.id+")");
            System.out.println("Ninguna taquilla disponible ... Entrando a la cola");
            
            while(this.disponible == 0) {
                try {
                    this.colaEspera.put(cliente);
                    wait();
                } catch (InterruptedException e) {}
            }
            
        }

        this.disponible--;

        System.out.println("Hilo ("+cliente.id+")");
        System.out.println("Quedan "+this.cantTicketsACT+" tickets disponibles");

        if( this.cantTicketsACT + cliente.compra > this.cantTicketsMAX){
            System.out.println("La cantidad de tickets a devolver excede el limite\n");
            this.disponible++;
        }
        else{
            System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") devolviendo "+cliente.compra+" tickets ...");
            System.out.println("Taquillas disponibles: "+this.disponible);
            
            try {
                System.out.println("Devolviendo ...");
                this.cantTicketsACT += cliente.compra;
                
                Thread.sleep(3000);

                System.out.println("("+cliente.equipo+") Cliente ("+cliente.id+") saliendo de la taquilla");
                
                this.disponible++;
                System.out.println("Taquillas disponibles: "+this.disponible+"\n");
            } catch (InterruptedException e) {}
        }

        if(!(this.colaEspera.isEmpty())){
            Fan fanatico= this.colaEspera.poll();
            System.out.println("Despertare al fanatico "+fanatico.id);
            notifyAll();
        }  
    }
}

class Fan implements Runnable {
    int id;
    String equipo;
    int compra;
    boolean action;
    Taquillas myTaquilla;

    public Fan(int id,int eq, boolean groupF, int tickets, Taquillas inTaquilla) {
        this.id=id;
        this.action = false;
        this.myTaquilla = inTaquilla;

        if (eq == 0) {
            this.equipo = "Caracas";
        }
        else {
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
        this.action = true;
        this.myTaquilla = inTaquilla;
        this.id=id;
        this.equipo=null;
        this.compra = tickets;
    }

    public void Despertar(){
        this.notify();
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

public class proy3aux {
    public static void main(String[] args) {
        /*
         ?  El nombre del archivo a leer se debe pasar como parámetro al programa
         ?  para eso vamos a usar args, la primera posición corresponde al primer
         ?  parámetro, por lo tanto se almacena en "filename".
         ?  En futuras versiones se debe sustituir "casosprueba.txt" por la variable
         ?  filename. 
        */

        String filename = args[0];
        
        try {
            FileReader fr = new FileReader(filename);
            BufferedReader buffer = new BufferedReader(fr);
            String linea;

            Taquillas T = new Taquillas(Integer.parseInt(linea = buffer.readLine()));
            int id=1;
            while((linea = buffer.readLine()) != null) {
                String[] partes = linea.split(",");

                switch (partes[0]) {
                    case "Caracas":
                        if (partes[1].equals("grupo")) {
                            Fan cliente = new Fan(id,0, true, Integer.parseInt(partes[2]), T);
                            Thread t1 = new Thread(cliente);
                            t1.start();
                        }
                        else {
                            Fan cliente = new Fan(id,0, false, Integer.parseInt(partes[1]), T);
                            Thread t1 = new Thread(cliente);
                            t1.start();
                        }
                        
                    break;
                    
                    case "Magallanes":
                        if (partes[1].equals("grupo")) {
                            Fan cliente = new Fan(id,1, true, Integer.parseInt(partes[2]), T);
                            Thread t2 = new Thread(cliente);
                            t2.start();
                        }
                        else {
                            Fan cliente = new Fan(id,1, false, Integer.parseInt(partes[1]), T);
                            Thread t3 = new Thread(cliente);
                            t3.start();
                        }
                    break;

                    case "cancelar":
                        Fan cliente = new Fan(id,Integer.parseInt(partes[1]), T);
                        Thread t4 = new Thread(cliente);
                        t4.start();
                    break;
                }
            id++;
            }
            buffer.close();
        } catch (IOException e) {}
    }
}