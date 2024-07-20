import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class Taquillas {
    // * Cantidad máxima de tickets disponibles
    int cantTicketsMAX;
    // * Cantidad actual de tickets disponibles
    int cantTicketsACT;
    // * Cantidad de taquillas disponibles (5 MAX)
    int disponible = 5;
    // * Bandera para la prioridad
    boolean timer = false;

    // * Cola de espera para los hilos
    BlockingQueue<Fan> colaEspera = new LinkedBlockingQueue<>();
    BlockingQueue<Fan> colaMagallanes = new LinkedBlockingQueue<>();

    private void iniciarVIP() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (Taquillas.this) {
                    timer = true;
                    System.out.println("\n---- COMPRAS VIP (MAGALLANES) ACTIVAS ----\n");
                }

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {e.printStackTrace();}

                synchronized (Taquillas.this) {
                    timer = false;
                    System.out.println("\n---- COMPRAS VIP (MAGALLANES) FINALIZADAS ----\n");

                    Taquillas.this.notifyAll();
                }
            }
        }, 2000, 15000);
    }

    public Taquillas(int num) {
        this.cantTicketsMAX = num;
        this.cantTicketsACT = num;
        this.iniciarVIP();
    }

   
    public synchronized void comprar(Fan cliente) {
        try {
            this.colaEspera.put(cliente);

            if (cliente.equipo == 1) {
                this.colaMagallanes.put(cliente);
            }
        } catch (InterruptedException e) {e.printStackTrace();}

        while (true) {
            if (this.timer) {
                if (cliente.equipo != 1) {
                    try {
                        wait();
                    } catch(InterruptedException e) {e.printStackTrace();}
                }
                else {
                    if (this.disponible == 0) {
                        System.out.println("Taquilla no disponible para "+cliente.id+"("+cliente.equipo+")"+" comprar "+cliente.compra+" Colocandose en la cola");
                        try {
                            wait();
                        } catch (InterruptedException e) {e.printStackTrace();}
                    }

                    while(this.disponible == 0 || cliente!=this.colaMagallanes.peek()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {e.printStackTrace();}
                    }

                    this.disponible--;
                    if(cliente == this.colaMagallanes.peek()){
                        if (this.cantTicketsACT - cliente.compra < 0) {
                            System.out.println(
                                "No hay tickets suficientes para que " + cliente.id +" ("+cliente.equipo+")"+" compre " + cliente.compra + " entradas");
                                this.colaMagallanes.poll();
                                this.colaEspera.remove(cliente);
                        }
                        else {
                            System.out.println("Fanatico " + cliente.id +" ("+cliente.equipo+")"+" Comprando " + cliente.compra + " tickets ...");
                            this.cantTicketsACT -= cliente.compra;
                            this.colaMagallanes.poll();
                            this.colaEspera.remove(cliente);
                        }
                    }
                break;
                }
            }
            else {
                if (this.disponible == 0) {
                    System.out.println("Taquilla no disponible para "+cliente.id+" ("+cliente.equipo+")"+" comprar "+cliente.compra+" Colocandose en la cola");
                    try {
                        wait();
                    } catch (InterruptedException e) {e.printStackTrace();}
                }

                while(this.disponible == 0 || cliente!=this.colaEspera.peek()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {e.printStackTrace();}
                }
                this.disponible--;
                if(cliente == this.colaEspera.peek()){
                    if (this.cantTicketsACT - cliente.compra < 0) {
                        System.out.println("No hay tickets suficientes para que " + cliente.id +" ("+cliente.equipo+")"+" compre " + cliente.compra + " entradas");
                        
                        this.colaEspera.poll();
                    }
                    else {
                        System.out.println("Fanatico " + cliente.id +" ("+cliente.equipo+")"+ " Comprando " + cliente.compra + " tickets ...");
                        this.cantTicketsACT -= cliente.compra;
                        
                        this.colaEspera.poll();
                    }
                
                    break;
                }
            }
        }
    }

    public synchronized void cancelar(Fan cliente) {
        try {
            this.colaEspera.put(cliente);
        } catch (InterruptedException e) {e.printStackTrace();}
        
        while (true) {
            if (this.timer) {
                try {
                    wait();
                } catch (InterruptedException e) {e.printStackTrace();}
            }
            else{
                if (this.disponible == 0) {
                    System.out.println("Taquilla no disponible para "+cliente.id+" ("+cliente.equipo+")"+" cancelar "+cliente.compra+" Colocandose en la cola");
                    try {
                        wait();
                    } catch (InterruptedException e) {e.printStackTrace();}
                }

                while(this.disponible == 0 || cliente!=this.colaEspera.peek()) {
                    try {
                        wait();
                    } catch (InterruptedException e) {e.printStackTrace();}
                }
        
                this.disponible--;

                if(cliente == this.colaEspera.peek()){
                    if (cliente.compra + this.cantTicketsACT > this.cantTicketsMAX){
                        System.out.println("Fanatico " + cliente.id +" ("+cliente.equipo+")"+ " Devolviendo una cantidad excesiva de tickets ...");
                        this.colaEspera.poll();
                    }
                    else{
                        System.out.println("Fanatico " + cliente.id +" ("+cliente.equipo+")"+ " Devolviendo " + cliente.compra + " tickets ...");
                        this.cantTicketsACT += cliente.compra;
                        this.colaEspera.poll();
                    }
                    break;
                }
            }
        }
    }

    public synchronized void liberar(Fan cliente) {
        System.out.println("El fanatico "+cliente.id+" ("+cliente.equipo+")"+" saliendo de la taquilla");
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
        }
        else {
            this.compra = 1;
        }
    }

    public Fan(int id, int equipo, int tickets, Taquillas inTaquilla) {
        this.action = true;
        this.myTaquilla = inTaquilla;
        this.id = id;
        this.equipo = equipo;
        this.compra = tickets;
    }

    @Override
    public void run() {
        if (this.action) {
            System.out.println("Fanatico (" + this.id + ") devolverá entradas: " + this.compra);
            this.myTaquilla.cancelar(this);

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {e.printStackTrace();}

           this.myTaquilla.liberar(this);

        }
        else {
            if (this.equipo == 0) {
                System.out.println("Fanatico (" + this.id + ") del Caracas quiere comprar: " + this.compra);
            } else {
                System.out.println("Fanatico (" + this.id + ") del Magallanes quiere comprar: " + this.compra);
            }

            try {
                this.myTaquilla.comprar(this);
                Thread.sleep(1500);
                this.myTaquilla.liberar(this);
            } catch (InterruptedException e) {e.printStackTrace();}
        }
    }
}

public class Proyecto_3_LDP {
    public static void main(String[] args) {
        String filename;

        if(args.length==0){
            filename = "casoprueba.txt";
        }
        else {
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
                        } 
                        else {
                            Thread t1  = new Fan(id, 0, true, Integer.parseInt(partes[1]), T);
                            t1.start();
                        }

                        break;

                    case "Magallanes":
                        if (partes[1].equals("grupo")) {
                            Thread t2 = new Fan(id, 1, true, Integer.parseInt(partes[2]), T);
                            t2.start();
                        }
                        else {
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
        } 
        catch (IOException e) {e.printStackTrace();}
    }
}