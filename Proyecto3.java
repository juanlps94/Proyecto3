import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

class Taquilla {
    int numTickets;
    public int Tdisp=5;
     
    Taquilla (int numTickets){
        this.numTickets = numTickets;
    }

    public synchronized void comprar(){
        while(this.Tdisp<1){   
            try {
            System.out.println("No hay taquillas disponibles...");
            wait();
        } catch (InterruptedException e) {}
    }
    this.Tdisp--;
    }

    public synchronized void liberarTaquila(){   
            System.out.println("Liberando taquilla...");
            this.Tdisp++;
            notifyAll();
    }

    public synchronized void cancelar(int cantEntradas){
        numTickets+=cantEntradas;
    }
}

class Fanatico implements Runnable {
    Taquilla T;
    int equipo,cantEntradas;
    Fanatico(Taquilla T, int equipo){
        this.T = T;
        this.equipo = equipo;
    }

    Fanatico(int cantEntradas){
        this.cantEntradas = cantEntradas;
    }

    
  
    public void Comprar() {

        if(this.T.numTickets<0){
            System.out.println("No hay tickets disponibles...");
            }else{

                if(this.equipo==1){     
                System.out.println("Fanatico del Caracas comprando tickets");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                T.numTickets--;
                System.out.println("Ticket comprado");
            
            }else if(this.equipo==2) {
               
                System.out.println("Fanatico del magallanes comprando tickets");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                T.numTickets--;
                System.out.println("Ticket comprado");
           
            }
        }
    }

    @Override
    public void run(){
        T.comprar();
        switch (equipo) {
            case 1:
                this.Comprar();
                T.liberarTaquila();
            break;
            case 2:
                this.Comprar();
                T.liberarTaquila();
            break;
            case 3:
                T.cancelar(equipo);
            break;      
            default:
                break;
        }

    }
}

class LVBP {
    public static void main (String args[]){
    try (FileReader fr = new FileReader("casoprueba.txt")) {
    int tickets,equipo;
    BufferedReader br = new BufferedReader(fr);
    // Lectura del fichero
    String linea;
    tickets = Integer.parseInt(linea=br.readLine());
    Taquilla T = new Taquilla(tickets);

        while((linea=br.readLine())!=null){
            String[] parts = linea.split(",");
            switch (parts[0]) {
                case "Caracas":
                    // Capturar entrada del archivo
                    equipo = 1;
                    System.out.println("Equipo Caracas Seleccionado");
                    Fanatico fanatico1 = new Fanatico(T,equipo);
                    Thread t1 = new Thread(fanatico1);
                    t1.start();
                break;
                case "Magallanes":
                    equipo = 2;
                    System.out.println("Equipo Mallanes Seleccionado");
                    Fanatico fanatico2 = new Fanatico(T,equipo);
                    Thread t2 = new Thread(fanatico2);
                    t2.start();
                break;
                case "cancelar":
                    equipo = 3;
                    int cantEntradas = Integer.parseInt(parts[1]);
                    System.out.println("Equipo Caracas Seleccionado");
                    Fanatico cancelarEntradas = new Fanatico(cantEntradas);
                    Thread t3 = new Thread(cancelarEntradas);
                    t3.start();
                break;
            }   
        }
  
     }
     catch(Exception e){}
    }
}