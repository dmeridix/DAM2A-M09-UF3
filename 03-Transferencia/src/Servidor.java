import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static final int PORT = 9999;
    public static final String HOST = "localhost";

    private ServerSocket serverSocket;
    private Socket cliSocket;

    public Socket connectar() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Acceptant connexions en -> " + HOST + ":" + PORT);
        System.out.println("Esperant el nom del fitxer del client...");
        cliSocket = serverSocket.accept();
        return cliSocket;
    }

    public void enviarFixers(Socket socket, String nomFitxer) throws IOException, ClassNotFoundException {
        ObjectOutputStream sortida = new ObjectOutputStream(socket.getOutputStream());
        try {
            Fitxer fitxer = new Fitxer(nomFitxer);
            sortida.writeObject("OK");
            sortida.writeObject(fitxer.getNom());
            sortida.writeObject(fitxer.getContingut());
            System.out.println("Fitxer enviat: " + fitxer.getNom());
        } catch (IOException e) {
            sortida.writeObject("ERROR");
            sortida.writeObject("No s'ha trobat el fitxer");
        }
    }

    public void tancarConnexio() throws IOException {
        if (cliSocket != null && !cliSocket.isClosed()) cliSocket.close();
        if (serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
        System.out.println("Connexió tancada.");
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            Socket clientSocket = servidor.connectar();
            ObjectInputStream entrada = new ObjectInputStream(clientSocket.getInputStream());
            String nomFitxer = (String) entrada.readObject();
            System.out.println("Rebuda peticio de fitxer: " + nomFitxer);
            if (!nomFitxer.equalsIgnoreCase("sortir")) {
                servidor.enviarFixers(clientSocket, nomFitxer);
            }
            servidor.tancarConnexio();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}