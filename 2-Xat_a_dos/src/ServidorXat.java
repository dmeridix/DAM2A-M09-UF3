import java.io.*;
import java.net.*;

public class ServidorXat {
    private static final int PORT = 9999;
    private static final String HOST = "localhost";
    private static final String MSG_SORTIR = "sortir";

    private ServerSocket serverSocket;

    public void iniciarServidor() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor iniciat a " + HOST + ":" + PORT);
    }

    public void pararServidor() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Servidor aturat.");
        }
    }

    public String getNom(ObjectInputStream input) throws IOException, ClassNotFoundException {
        String nom = (String) input.readObject();
        System.out.println("Nom rebut: " + nom);
        return nom;
    }

    public static void main(String[] args) {
        ServidorXat servidor = new ServidorXat();

        try {
            servidor.iniciarServidor();
            Socket clientSocket = servidor.serverSocket.accept();
            System.out.println("Client connectat: " + clientSocket);

            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

            String nom = servidor.getNom(input);
            System.out.println("Fitxer de xat creat.");

            FilServidorXat fil = new FilServidorXat(input, nom);
            fil.start();

            BufferedReader consola = new BufferedReader(new InputStreamReader(System.in));
            String missatge;
            do {
                System.out.print("Missatge ('sortir' per tancar): ");
                missatge = consola.readLine();
                output.writeObject(missatge);
                output.flush();
            } while (!missatge.equals(MSG_SORTIR));

            fil.join();
            clientSocket.close();
            System.out.println("Fitxer de xat finalitzat.");

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                servidor.pararServidor();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
