import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

class Taquilla {
    int numTickets;
    public int Tdisp=5;
     
    public Taquilla (int numTickets){
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

    public Fanatico(Taquilla T, int equipo,int cantEntradas){
        this.T = T;
        this.equipo = equipo;
        this.cantEntradas = cantEntradas;
    }
   
    public void Comprar() {

        if(this.T.numTickets<0){
            System.out.println("No hay tickets disponibles...");
            }else if (this.T.numTickets-this.cantEntradas<0) {
                System.out.println("No hay suficientes tickets disponibles para una compra de"+this.cantEntradas+"...");
            } else{

                if(this.equipo==1){     
                System.out.println("Fanatico del Caracas comprando "+this.cantEntradas+" tickets");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                T.numTickets--;
                System.out.println("Ticket comprado");
            
            }else if(this.equipo==2) {
               
                System.out.println("Fanatico del magallanes comprando "+this.cantEntradas+" tickets");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {}
                T.numTickets--;
                System.out.println("Ticket comprado");
           
            }
        }
    }
    private void Cancelar() {
        System.out.println("Se estan Devolviendo "+this.cantEntradas+" tickets");
        this.T.numTickets = this.T.numTickets+this.cantEntradas;
    }

    @Override
    public void run(){
        switch (equipo) {
            case 1:
                T.comprar();
                this.Comprar();
                T.liberarTaquila();
            break;
            case 2:
                T.comprar();
                this.Comprar();
                T.liberarTaquila();
            break;
            case 3:
                T.cancelar(this.cantEntradas);
                this.Cancelar();
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
        int cantEntradas;
        Taquilla T = new Taquilla(tickets);

            while((linea=br.readLine())!=null){
                String[] parts = linea.split(",");
                // Capturar entrada del archivo
                switch (parts[0]) {
                    case "Caracas":
                        equipo = 1;
                        if(parts[1].equals("grupo")){
                            System.out.println("Equipo Caracas Seleccionado para compra en grupo");
                            cantEntradas = Integer.parseInt(parts[2]);
                            Fanatico fanatico = new Fanatico(T,equipo,cantEntradas);
                            Thread t = new Thread(fanatico);
                            t.start();
                        }else{
                            System.out.println("Equipo Caracas Seleccionado");
                            Fanatico fanatico = new Fanatico(T,equipo,1);
                            Thread t = new Thread(fanatico);
                            t.start();
                        }
                    break;
                    case "Magallanes":
                        equipo = 2;
                        if(parts[1].equals("grupo")){
                            System.out.println("Equipo Magallanes Seleccionado para compra en grupo");
                            cantEntradas = Integer.parseInt(parts[2]);
                            Fanatico fanatico = new Fanatico(T,equipo,cantEntradas);
                            Thread t2 = new Thread(fanatico);
                            t2.start();
                        }else{
                            System.out.println("Equipo Magallanes Seleccionado");
                            Fanatico fanatico = new Fanatico(T,equipo,1);
                            Thread t2 = new Thread(fanatico);
                            t2.start();
                        }
                    break;
                    case "cancelar":
                        equipo = 3;
                        cantEntradas = Integer.parseInt(parts[1]);
                        System.out.println("Se devolveran "+cantEntradas+" entradas...");
                        Fanatico cancelarFanatico = new Fanatico(T,equipo,cantEntradas);
                        Thread t3 = new Thread(cancelarFanatico);
                        t3.start();
                    break;
                }   
            }
     }catch(Exception e){}

    }
}
