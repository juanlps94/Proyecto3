import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class Taquillas {
    int cantTickets;
    int disponible;

    public Taquillas(int num) {
        this.cantTickets = num;
        this.disponible = 5;
    }

    public synchronized void comprar(int compra) {
        while(this.disponible == 0) {
            try {
                System.out.println("Taquilla no disponible");
                wait();
            } catch (InterruptedException e) {}
        }
        
        if(this.cantTickets - compra < 0) {
            System.out.println("No hay tickets suficientes");
        }
        else {
            System.out.println("Cliente comprando "+compra+" tickets ...");
            this.disponible--;
            this.cantTickets -= compra;
        }
    }

    public synchronized void liberar() {
        System.out.println("Saliendo de la taquilla");
        this.disponible++;
    }

    public synchronized void cancelar(int compra) {
        while (this.disponible == 0) {
            try {
                System.out.println("Taquilla no disponible");
                wait();
            } catch (InterruptedException e) {}
        }
        System.out.println("Cliente devolviendo "+compra+" tickets ...");
        this.disponible--;
        this.cantTickets += compra;
    }
}

class Fan implements Runnable {
    int equipo;
    int compra;
    boolean action;
    Taquillas myTaquilla;

    public Fan(int eq, boolean groupF, int tickets, Taquillas inTaquilla) {
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

    public Fan(int tickets, Taquillas inTaquilla) {
        this.action = true;
        this.myTaquilla = inTaquilla;
        this.compra = tickets;
    }

    @Override
    public void run() {
        if (this.action) {
            if (this.equipo == 0) {
                System.out.println("Fanatico del Caracas:");
            }
            else {
                System.out.println("Fanatico del Magallanes");
            }
            this.myTaquilla.cancelar(this.compra);
        }
        else {
            if (this.equipo == 0) {
                System.out.println("Fanatico del Caracas:");
            }
            else {
                System.out.println("Fanatico del Magallanes");
            }

            this.myTaquilla.comprar(this.compra);
            
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {}

            this.myTaquilla.liberar();
        }
    }
}

class LVBP {
    public static void main(String[] args) {
        
        if (args.length != 0) {
            String filename = args[0];
        }
        else {
            System.err.println("File not found: java Proyecto_3_LDP <filename>");
        }
        
        try {
            FileReader fr = new FileReader("casosprueba.txt");
            BufferedReader buffer = new BufferedReader(fr);
            String linea;

            Taquillas T = new Taquillas(Integer.parseInt(linea = buffer.readLine()));

            while((linea = buffer.readLine()) != null) {
                String[] partes = linea.split(",");

                switch (partes[0]) {
                    case "Caracas":
                        if (partes[1].equals("grupo")) {
                            Fan cliente = new Fan(0, true, Integer.parseInt(partes[2]), T);
                            Thread t1 = new Thread(cliente);
                            t1.start();
                        }
                        else {
                            Fan cliente = new Fan(0, false, Integer.parseInt(partes[2]), T);
                            Thread t1 = new Thread(cliente);
                            t1.start();
                        }
                    break;
                    
                    case "Magallanes":
                        if (partes[1].equals("grupo")) {
                            Fan cliente = new Fan(1, true, Integer.parseInt(partes[2]), T);
                            Thread t2 = new Thread(cliente);
                            t2.start();
                        }
                        else {
                            Fan cliente = new Fan(1, false, Integer.parseInt(partes[2]), T);
                            Thread t3 = new Thread(cliente);
                            t3.start();
                        }
                    break;

                    case "cancelar":
                        Fan cliente = new Fan(Integer.parseInt(partes[1]), T);
                        Thread t4 = new Thread(cliente);
                        t4.start();
                    break;
                }
            }
            buffer.close();
        } catch (IOException e) {}
    }
}
public class Proyecto_3_LDP {
    
}
