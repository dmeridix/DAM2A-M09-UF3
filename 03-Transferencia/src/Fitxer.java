import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Fitxer {
    private String nom;
    private byte[] contingut;

    public Fitxer(String nom) throws IOException {
        this.nom = nom;
        contingut = llegirContingut();
    }

    private byte[] llegirContingut() throws IOException {
        File fitxer = new File(nom);
        if (!fitxer.exists()) throw new IOException();
        try (FileInputStream fitxerEntrada = new FileInputStream(fitxer)) {
            byte[] dades = new byte[(int) fitxer.length()];
            fitxerEntrada.read(dades);
            return dades;
        }
    }

    public byte[] getContingut() {
        return contingut;
    }

    public String getNom() {
        return nom;
    }
}