import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

class Taquilla {
    int numTickets;
    public int Tdisp=2;
     
    Taquilla (int numTickets){
        this.numTickets = 50;
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

    public synchronized void cancelar(int equipo, int cantEntradas){
        numTickets+=cantEntradas;
    }
}

class Fanatico implements Runnable {
    Taquilla T;
    int equipo;
    Fanatico(Taquilla T, int equipo){
        this.T = T;
        this.equipo = equipo;
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
       
            default:
                break;
        }

    }
}

class LVBP {
    public static void main (String args[]){
    String entrada = entrada[100][3];
    
    try (FileReader fr = new FileReader("casoprueba.txt")) {
        BufferedReader br = new BufferedReader(fr);
        // Lectura del fichero
        String linea;
        int numTickets = Integer.parseInt(linea=br.readLine());
        System.out.println("Cantidad de Tickets "+numTickets);

        while((linea=br.readLine())!=null){

        }
     }
     catch(Exception e){
        e.printStackTrace();
     }
  
        Taquilla T = new Taquilla(100);
        /*int i=0;

        while(i<10){
            // Capturar entrada del archivo
            int equipo = (int)((Math.random()*2)+1);
            System.out.println("Equipo "+equipo+" Seleccionado");
            Fanatico fanatico = new Fanatico(T,equipo);
            Thread t1 = new Thread(fanatico);
            t1.start();
            i++;
        }*/


    }
}