import java.io.*;
import java.net.*;
import java.util.*;

public class ClientXat extends Thread {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean sortir = false;

    public void connecta() {
        try {
            socket = new Socket("localhost", 9999);
            oos = new ObjectOutputStream(socket.getOutputStream());
            System.out.println("Cliente conectado a localhost:9999\nFlujo de salida creado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enviarMissatge(String missatge) {
        try {
            oos.writeObject(missatge);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tancarClient() {
        try {
            if (ois != null) ois.close();
            if (oos != null) oos.close();
            if (socket != null) socket.close();
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
                String codi = Missatge.getCodiMissatge(missatgeCru);
    
                if (codi == null) continue;
    
                switch (codi) {
                    case Missatge.CODI_SORTIR_TOTS:
                        sortir = true;
                        break;
    
                        case Missatge.CODI_MSG_PERSONAL:
                        String[] parts = Missatge.getPartsMissatge(missatgeCru);
                        if (parts.length >= 3) {
                            String remitent = parts[0];
                            String destinatari = parts[1];
                            String contingut = parts[2];
                    
                            System.out.println("Missatge de(" + remitent + ") per (" + destinatari + "): " + contingut);
                        }
                        break;
    
                    case Missatge.CODI_MSG_GRUP:
                        parts = Missatge.getPartsMissatge(missatgeCru);
                        if (parts.length >= 2) {
                            System.out.println("DEBUG: multicast " + parts[1]);
                        }
                        break;
    
                    default:
                        System.out.println("Error recibiendo mensaje.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error recibiendo mensaje. Saliendo...");
        } finally {
            tancarClient();
        }
    }

    public void ajuda() {
        System.out.println("---------------------");
        System.out.println("Comandes disponibles:");
        System.out.println("1.- Conectar al servidor (primero obligatorio)");
        System.out.println("2.- Enviar mensaje personal");
        System.out.println("3.- Enviar mensaje al grupo");
        System.out.println("4.- (o línea en blanco) -> Salir del cliente");
        System.out.println("5.- Finalizar todos");
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
    
        // Hilo que escucha mensajes entrantes del servidor
        Thread threadRebre = new Thread(client);
        threadRebre.start();
    
        boolean continuar = true;
        while (continuar && !client.sortir) {
    
            // Mostramos el menú cada vez que pedimos una nueva opción
            client.ajuda();  // <-- Aquí mostramos el menú siempre antes de pedir opción
    
            String opcio = client.getLinea(scanner, "> ", false);
    
            if (opcio.isEmpty()) {
                // Opción 4: Línea vacía → Salir del cliente
                client.enviarMissatge(Missatge.getMissatgeSortirClient("Adéu"));
                continuar = false;
                continue;
            }
    
            switch (opcio) {
                case "1":
                    String nom = client.getLinea(scanner, "Introdueix el nom: ", true);
                    String missatgeConectar = Missatge.getMissatgeConectar(nom);
                    client.enviarMissatge(missatgeConectar);
                    System.out.println("Enviant missatge: " + missatgeConectar);
    

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
                    System.out.println("Opción inválida.");
            }
        }
    
        client.tancarClient();
        System.out.println("Saliendo...");
    }
}