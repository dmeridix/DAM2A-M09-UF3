import java.io.*;
import java.net.Socket;

public class Client {
    public static final int PORT = Servidor.PORT;
    public static final String HOST = Servidor.HOST;

    private Socket socket;
    private PrintWriter out;

    public void conecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream(), true); // Autoflush activado
            System.out.println("Connectat a servidor en " + HOST + ":" + PORT);
        } catch (IOException e) {
            System.err.println("Error al conectar client: " + e.getMessage());
        }
    }

    public void tanca() {
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Client tancat.");
        } catch (IOException e) {
            System.err.println("Error al tancar client: " + e.getMessage());
        }
    }

    public void envia(String st) {
        if (!socket.isClosed()) {
            out.println(st); // Enviar mensaje
            System.out.println("Enviat al servidor: " + st);
        } else {
            System.err.println("No s'ha pogut enviar. Connexió no disponible.");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        client.conecta();

        client.envia("Prova d'enviament 1");
        client.envia("Prova d'enviament 2");
        client.envia("Adéu!");

        System.out.println("Prem ENTER per tancar la connexió...");
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            System.err.println("Error en llegir l'entrada: " + e.getMessage());
        }

        client.tanca();
    }
}