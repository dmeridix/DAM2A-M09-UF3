import java.io.*;
import java.net.*;
import java.util.*;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";
    private Hashtable<String, GestorClients> clients = new Hashtable<>();
    private boolean sortir = false;
    private ServerSocket serverSocket;

    public void servidorAEscoltar() {
        try {
            serverSocket = new ServerSocket(PORT, 0, InetAddress.getByName(HOST));
            System.out.println("Servidor iniciat a localhost:9999");

            while (!sortir) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connectat: " + socket.getInetAddress());

                GestorClients gc = new GestorClients(socket, this);
                gc.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pararServidor();
        }
    }

    public void pararServidor() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void afegirClient(GestorClients client) {
    String nom = client.getNom();
    clients.put(nom, client);
    System.out.println(nom + " connectat.");
    enviarMissatgeGrup(Missatge.getMissatgeGrup("Entra: " + nom)); // ✅ CORREGIDO
    }

    public synchronized void enviarMissatgeGrup(String missatge) {
        String codificat = Missatge.getMissatgeGrup(missatge); // ✅ CORREGIDO
        System.out.println("DEBUG: multicast " + missatge);
        for (GestorClients client : clients.values()) {
            client.enviarMissatge(codificat);
        }
    }

    public void finalitzarXat() {
        enviarMissatgeGrup(Missatge.getMissatgeGrup(MSG_SORTIR)); // ✅ CORREGIDO
        clients.clear();
        sortir = true;
        pararServidor();
        System.exit(0);
    }


    public synchronized void eliminarClient(String nom) {
        if (clients.containsKey(nom)) {
            clients.remove(nom);
        }
    }

    public synchronized void enviarMissatgePersonal(String remitent, String destinatari, String missatge) {
        GestorClients client = clients.get(destinatari);
        if (client != null) {
            String codificat = Missatge.getMissatgePersonal(destinatari, missatge);
            client.enviarMissatge(codificat);
        }
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();
        servidor.servidorAEscoltar();
    }
}