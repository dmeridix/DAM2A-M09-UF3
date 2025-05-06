import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final int PORT = Servidor.PORT;
    public static final String HOST = Servidor.HOST;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public void connectar() throws IOException {
        socket = new Socket(HOST, PORT);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        System.out.println("Connectat al servidor en " + HOST + ":" + PORT);
    }

    public void rebreFixers(String nomFitxer) throws IOException, ClassNotFoundException {
        out.writeObject(nomFitxer);
        out.flush();
        String resposta = (String) in.readObject();
        if (resposta.equals("OK")) {
            String nomRebut = (String) in.readObject();
            byte[] contingutRebut = (byte[]) in.readObject();
            try (FileOutputStream fos = new FileOutputStream(nomRebut)) {
                fos.write(contingutRebut);
                System.out.println("Fitxer rebut i guardat: " + nomRebut);
            }
        } else {
            System.out.println((String) in.readObject());
        }
    }

    public void tancarConnexio() throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null && !socket.isClosed()) socket.close();
        System.out.println("Connexió tancada.");
    }

    public static void main(String[] args) {
        Client client = new Client();
        Scanner sc = new Scanner(System.in);
        try {
            client.connectar();
            while (true) {
                System.out.print("Introdueix el nom del fitxer ('sortir' per tancar): ");
                String nomFitxer = sc.nextLine().trim();
                if (nomFitxer.equalsIgnoreCase("sortir")) break;
                client.rebreFixers(nomFitxer);
            }
            client.tancarConnexio();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}