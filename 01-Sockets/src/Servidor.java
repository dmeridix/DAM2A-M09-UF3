import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static final int PORT = 7777;

    private ServerSocket serverSocket;
    private Socket cliSocket;

    public void connecta() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Servidor en marxa a localhost:" + PORT);
            System.out.println("Esperant connexions...");
            cliSocket = serverSocket.accept();
            System.out.println("Client connectat: " + cliSocket.getInetAddress());
        } catch (IOException ex) {
            System.err.println("Error al iniciar el servidor: " + ex.getMessage());
        }
    }

    public void repDades() {
        try (BufferedReader bf = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
             PrintWriter out = new PrintWriter(cliSocket.getOutputStream())) {
    
            String text;
            while ((text = bf.readLine()) != null) {
                System.out.println("Rebut: " + text);
                out.println("Missatge rebut: " + text);
            }
            System.out.println("Connexió tancada pel client.");
        } catch (IOException e) {
            System.err.println("Error al rebre dades: " + e.getMessage());
        }
    }

    public void tanca() {
        try {
            if (!cliSocket.isClosed()) {
                cliSocket.close();
            }
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Servidor tancat.");
        } catch (IOException e) {
            System.err.println("Error al tancar el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor sr = new Servidor();
        sr.connecta();
        sr.repDades();
        sr.tanca();
    }
}