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
            System.out.println("Error rebent missatge.");
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            oos.writeObject(missatge);
            oos.flush();
        } catch (IOException e) {
            System.out.println("Flux tancat. No s'ha pogut enviar el missatge.");
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
                sortir = true;
                break;

            case Missatge.CODI_MSG_PERSONAL:
                parts = Missatge.getPartsMissatge(missatgeCru);
                if (parts.length >= 3) {
                    String destinatari = parts[1];
                    String contingut = parts[2];
                    System.out.printf("Missatge personal per(%s) de(%s): %s%n", destinatari, this.nom, contingut);
                    servidor.enviarMissatgePersonal(this.nom, destinatari, contingut); // ✅ LÍNEA CLAVE
                }
                break;

            default:
                System.out.println("Codi desconegut: " + codi);
        }
    }
}