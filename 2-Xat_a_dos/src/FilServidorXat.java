import java.io.*;

public class FilServidorXat extends Thread {
    private ObjectInputStream input;
    private String nom;
    private static final String MSG_SORTIR = "sortir";

    public FilServidorXat(ObjectInputStream input, String nom) {
        this.input = input;
        this.nom = nom;
    }

    @Override
    public void run() {
        System.out.println("Fil de " + nom + " iniciat");
        try {
            while (true) {
                String missatge = (String) input.readObject();
                System.out.println("Rebut: " + missatge);
                if (missatge.equals(MSG_SORTIR)) {
                    System.out.println("Missatge de tancament rebut. Finalitzant fil.");
                    break;
                } else {
                    System.out.println("Hola " + nom + "!");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("El client ha tancat la connexió.");
        }
    }
}