import java.io.*;
import java.net.*;
import java.util.*;

public class ClientXat extends Thread {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir = false;
    private String nom;

    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Client connectat a localhost:9999");
            System.out.println("Flux d'entrada i sortida creat.");
        } catch (IOException e) {
            e.printStackTrace();
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

    public void tancarClient() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
            System.out.println("Flux d'entrada tancat.\nFlux de sortida tancat.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("DEBUG: Iniciant rebuda de missatges...");
            while (!sortir) {
                String missatgeCru = (String) ois.readObject();

                if (missatgeCru == null || missatgeCru.trim().isEmpty()) {
                    System.out.println("Missatge buit rebut. Saltant...");
                    continue;
                }

                String codi = Missatge.getCodiMissatge(missatgeCru);
                if (codi == null) continue;

                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;

                    case Missatge.CODI_MSG_PERSONAL:
                        String[] parts = Missatge.getPartsMissatge(missatgeCru);
                        if (parts != null && parts.length >= 3) {
                            System.out.println("Missatge de(" + parts[1] + "): " + parts[2]);
                        } else {
                            System.out.println("ERROR: format de missatge personal invàlid");
                        }
                        break;

                    case Missatge.CODI_MSG_GRUP:
                        parts = Missatge.getPartsMissatge(missatgeCru);
                        if (parts != null && parts.length >= 2) {
                            System.out.println(parts[1]);
                        }
                        break;

                    default:
                        System.out.println("Tipus de missatge desconegut: " + codi);
                }
            }
        } catch (Exception e) {
            System.out.println("\nError rebent missatge. Sortint...");
        } finally {
            tancarClient();
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primer pass obligatori)");
        System.out.println("2.- Enviar missatge personal");
        System.out.println("3.- Enviar missatge al grup");
        System.out.println("4.- (o línia en blanc)-> Sortir del client");
        System.out.println("5.- Finalitzar tothom");
        System.out.println("---------------------");
    }

    public String getLinea(Scanner scanner, String pregunta, boolean obligatori) {
        System.out.print(pregunta);
        String resposta = scanner.nextLine().trim();
        while (obligatori && resposta.isEmpty()) {
            System.out.print(pregunta);
            resposta = scanner.nextLine().trim();
        }
        return resposta;
    }

    public static void main(String[] args) {
        ClientXat client = new ClientXat();
        client.connecta();

        Scanner scanner = new Scanner(System.in);
        Thread threadRebre = new Thread(client);
        threadRebre.start();

        boolean continuar = true;
        while (continuar && !client.sortir) {
            client.ajuda();
            String opcio = client.getLinea(scanner, "> ", false);

            if (opcio.isEmpty()) {
                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                continuar = false;
                continue;
            }

            switch (opcio) {
                case "1":
                    String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                    client.nom = nom;
                    client.enviarMissatge(Missatge.getMissatgeConectar(nom));
                    break;

                case "2":
                    String destinatari = client.getLinea(scanner, "Destinatari: ", true);
                    String missatgeP = client.getLinea(scanner, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgePersonal(destinatari, missatgeP));
                    break;

                case "3":
                    String missatgeG = client.getLinea(scanner, "Missatge a enviar: ", true);
                    client.enviarMissatge(Missatge.getMissatgeGrup(missatgeG));
                    break;

                case "4":
                    client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                    continuar = false;
                    break;

                case "5":
                    client.enviarMissatge(Missatge.getMissatgeSortirTots("Adéu"));
                    continuar = false;
                    break;

                default:
                    System.out.println("Opció invàlida.");
            }
        }

        client.tancarClient();
        System.out.println("Tancant client...");
    }
}