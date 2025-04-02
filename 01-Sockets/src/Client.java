import java.io.*;
import java.net.Socket;

public class Client {
    public static final int PORT = 7777;
    public static final String HOST = "localhost";

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public void conecta() {
        try {
            socket = new Socket(HOST, PORT);
            out = new PrintWriter(socket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Connexió tancada.");
        } catch (IOException e) {
            System.err.println("Error al tancar client: " + e.getMessage());
        }
    }

    public void envia(String st) {
        if (!socket.isClosed()) {
            out.println(st);
            System.out.println("Enviat al servidor: " + st);

            try {
                String resposta = in.readLine();
                System.out.println("Resposta del servidor: " + resposta);
            } catch (IOException e) {
                System.err.println("Error al llegir resposta del servidor: " + e.getMessage());
            }
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

        // 4. Tancar la connexió
        client.tanca();
    }
}