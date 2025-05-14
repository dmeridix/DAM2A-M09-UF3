import java.io.*;
import java.net.*;

public class GestorClients extends Thread {
    private Socket clientSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ServidorXat servidor;
    private String nom;
    private boolean sortir = false;

    public GestorClients(Socket socket, ServidorXat servidor) {
        this.clientSocket = socket;
        this.servidor = servidor;
        try {
            this.oos = new ObjectOutputStream(clientSocket.getOutputStream());
            this.ois = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNom() {
        return nom;
    }

    public void run() {
        try {
            String missatgeCru;
            while (!sortir && (missatgeCru = (String) ois.readObject()) != null) {
                processaMissatge(missatgeCru);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMissatge(String remitent, String missatge) {
        try {
            oos.writeObject(missatge);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processaMissatge(String missatgeCru) {
        String codi = Missatge.getCodiMissatge(missatgeCru);
        if (codi == null) return;

        switch (codi) {
            case Missatge.CODI_CONECTAR:
                String[] parts = Missatge.getPartsMissatge(missatgeCru);
                if (parts.length > 1) {
                    this.nom = parts[1];
                    servidor.afegirClient(this);
                }
                break;

            case Missatge.CODI_SORTIR_CLIENT:
                servidor.eliminarClient(nom);
                sortir = true;
                break;

            case Missatge.CODI_SORTIR_TOTS:
                servidor.finalitzarXat();
                break;

                case Missatge.CODI_MSG_PERSONAL:
                parts = Missatge.getPartsMissatge(missatgeCru);
                if (parts.length >= 3) {
                    String remitent = parts[0];
                    String destinatari = parts[1];
                    String missatge = parts[2];
            
                    System.out.printf("Missatge personal per(%s) de(%s): %s%n",
                            destinatari, remitent, missatge);
                }
                break;

            default:
                System.out.println("Código desconocido: " + codi);
        }
    }
}