import java.io.*;

public class FilLectorCX extends Thread {
    private ObjectInputStream input;

    public FilLectorCX(ObjectInputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String missatge = (String) input.readObject();
                System.out.println("Missatge ('sortir' per tancar): Rebut: " + missatge);
                System.out.println(missatge);
                if (missatge.equals("sortir")) {
                    System.out.println("Missatge de tancament rebut. Finalitzant client.");
                    break;
                }
            }            
        } catch (ClassNotFoundException e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
         catch (IOException e) {
            System.out.println("El servidor ha tancat la connexió.");
        }
    }
}
