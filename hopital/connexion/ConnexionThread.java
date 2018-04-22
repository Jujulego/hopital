package hopital.connexion;

import com.jcraft.jsch.JSchException;

import java.sql.SQLException;
import java.util.LinkedList;

public class ConnexionThread extends Thread {
    // Attributs
    private boolean connexionECE;
    private Connexion connexion = null;
    private LinkedList<ConnexionListener> listeners = new LinkedList<>();

    private String usernameECE;
    private String passwordECE;
    private String nameDatabase;
    private String loginDatabase;
    private String passwordDatabase;
    private boolean distant;

    // Connexion
    public ConnexionThread(String nameDatabase, String loginDatabase, String passwordDatabase, boolean distant) {
        // Type de connxion
        this.connexionECE = false;

        // Paramètres de connexion
        this.nameDatabase = nameDatabase;
        this.loginDatabase = loginDatabase;
        this.passwordDatabase = passwordDatabase;
        this.distant = distant;
    }

    public ConnexionThread(String usernameECE, String passwordECE, String loginDatabase, String passwordDatabase) {
        // Type de connxion
        this.connexionECE = true;

        // Paramètres de connexion
        this.usernameECE = usernameECE;
        this.passwordECE = passwordECE;
        this.loginDatabase = loginDatabase;
        this.passwordDatabase = passwordDatabase;
    }

    // Méthodes
    @Override
    public void run() {
        try {
            // Connexion !!!
            if (connexionECE) {
                connexion = new Connexion(usernameECE, passwordECE, loginDatabase, passwordDatabase);
            } else {
                connexion = new Connexion(nameDatabase, loginDatabase, passwordDatabase, distant);
            }

            // listeners
            for (ConnexionListener listener : listeners) {
                listener.connexionReussie(connexion);
            }
        } catch (JSchException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();

            // listeners
            for (ConnexionListener listener : listeners) {
                listener.connexionEchouee();
            }
        }
    }

    public void ajouterConnexionListener(ConnexionListener listener) {
        listeners.add(listener);
    }

    // Accesseurs
    public Connexion getConnexion() {
        return connexion;
    }

    // Listeners
    public interface ConnexionListener {
        void connexionReussie(Connexion connexion);
        void connexionEchouee();
    }
}
