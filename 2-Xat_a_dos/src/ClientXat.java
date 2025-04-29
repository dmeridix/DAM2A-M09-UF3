import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientXat {
    private static final String HOST = "localhost";
    private static final int PORT = 9999;
    private Socket socket;
    ObjectOutputStream output;
    ObjectInputStream input;

    public void connecta(String nom) throws IOException {
        socket = new Socket(HOST, PORT);
        System.out.println("Client connectat a " + HOST + ":" + PORT);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        System.out.println("Flux d'entrada i sortida creat.");

        System.out.println("Missatge ('sortir' per tancar): Escriu el teu nom:");
        output.writeObject(nom);
        output.flush();
        System.out.println("Enviant missatge: " + nom);
    }

    public void enviarMissatge(String missatge) throws IOException {
        System.out.println("Enviant missatge: " + missatge);
        output.writeObject(missatge);
        output.flush();
    }

    public void tancarClient() throws IOException {
        System.out.println("Tancant client...");
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Client tancat.");
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        Scanner sc = new Scanner(System.in);

        try {
            String nom = sc.nextLine();
            client.connecta(nom);

            FilLectorCX lector = new FilLectorCX(client.input);
            lector.start();

            while (true) {
                String missatge = sc.nextLine();
                client.enviarMissatge(missatge);
                if (missatge.equals("sortir")) {
                    break;
                }
            }
            lector.join();
            client.tancarClient();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            sc.close();
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}
