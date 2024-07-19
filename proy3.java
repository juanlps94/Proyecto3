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
    int cantTickets;

//* Cantidad de taquillas disponibles (5 MAX)
    int disponible;

//* Cola de espera para los hilos
    BlockingQueue<Fan> colaEspera;

    public Taquillas(int num) {
        this.cantTickets = num;
        this.disponible = 1;
        this.colaEspera = new LinkedBlockingQueue<>();
    }

    public synchronized void comprar(Fan cliente) {
//*     Todas las taquillas están ocupadas
    if (this.disponible == 0) {
        System.out.println("Taquilla no disponible para "+cliente.id+" comprar "+cliente.compra+" Colocandose en la cola");
        while(this.disponible == 0) {
            try {
                this.colaEspera.put(cliente);
                wait();
            } catch (InterruptedException e) {}
        }
        
    }
        
//*     La compra excede la cantidad de tickets
        if(this.cantTickets - cliente.compra < 0) {
            System.out.println("No hay tickets suficientes");
        }
//!     Se proceden a comprar los tickets, ocupando la taquilla        
        else {      
            System.out.println("Comprando "+cliente.compra+" tickets ...");
            this.disponible--;
            this.cantTickets -= cliente.compra;
        }

        
    }

    public synchronized void liberar() {
/*
!       Se libera la taquilla ocupada y se notifica a cualquiera esperando
TODO:   Validar que la cantidad de taquillas disponibles no exceda las 5
*/
        System.out.println("");
        System.out.println("Saliendo de la taquilla");
        this.disponible++;
        Fan fanatico= this.colaEspera.poll();
        if(this.colaEspera.isEmpty()){
            notifyAll();
        }else{
            System.out.println("Despertare al fanatico "+fanatico.id);

            notifyAll();
        }
        
    }

    public synchronized void cancelar(Fan cliente) {
//*     Todas las taquillas están ocupadas 
        if (this.disponible == 0) {
            System.out.println("Taquilla no disponible para el fanatico "+cliente.id+" cancelar entradas Colocandose en la cola");
            try {
                this.colaEspera.put(cliente);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            while (this.disponible == 0) {
                try {
                    wait();
                } catch (InterruptedException e) {}
            }
            
        }
//!     Se realiza la devolución de los tickets, ocupando la taquilla
        System.out.println(cliente.id+" Cancelando "+cliente.compra+" tickets ...");
        this.disponible--;
        this.cantTickets += cliente.compra;
    }
}

class Fan implements Runnable {
    int equipo;
    int compra;
    int id;
    boolean action;
    Taquillas myTaquilla;

    public Fan(int id,int eq, boolean groupF, int tickets, Taquillas inTaquilla) {
        this.id=id;
        this.equipo = eq;
        this.action = false;
        this.myTaquilla = inTaquilla;

        if (groupF) {
            this.compra = tickets;
        }
        else {
            this.compra = 1;
        }
    }

    public Fan(int id,int equi, int tickets, Taquillas inTaquilla) {
        this.action = true;
        this.myTaquilla = inTaquilla;
        this.id=id;
        this.equipo=equi;
        this.compra = tickets;
    }

    public void Despertar(){
        this.notify();
    }

    @Override
    public void run() {
        if (this.action) {
            
            System.out.println("Fanatico "+this.id+ " devolvera entradas: "+this.compra);
            this.myTaquilla.cancelar(this);

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}

            this.myTaquilla.liberar();
        }
        else {
            if (this.equipo == 0) {
                System.out.println("Fanatico "+this.id+ " del Caracas quiere comprar: "+this.compra);
            }
            else {
                System.out.println("Fanatico "+this.id+ " del Magallanes quiere comprar: "+this.compra);
            }

            this.myTaquilla.comprar(this);
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}

            this.myTaquilla.liberar();
        }
    }
}

public class proy3 {
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
                        Fan cliente = new Fan(id,2,Integer.parseInt(partes[1]), T);
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