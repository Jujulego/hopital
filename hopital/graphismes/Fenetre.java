package hopital.graphismes;

import hopital.connexion.Connexion;
import hopital.connexion.ConnexionThread;

import javax.swing.*;

public class Fenetre extends JFrame implements ConnexionECEDialog.ConnexionListener, ConnexionThread.ConnexionListener {
    // Attrinuts
    ConnexionECEDialog connexionDialog = new ConnexionECEDialog();
    ConnexionThread connexionThread;
    Connexion connexion;

    // Constructeur
    public Fenetre() {
        // Paramètres
        setSize(1080, 760);
        setVisible(true);

        // Activation de la boite de dialogue
        connexionDialog.setVisible(true);
        connexionDialog.setLocationRelativeTo(this);
        connexionDialog.ajouterConnexionListener(this);
    }

    // Méthodes
    @Override
    public void connexionECE(String utilisateur, char[] motDePasse) {
        connexionThread = new ConnexionThread(
                utilisateur, new String(motDePasse),
                "jc151870", "YDRyIxgl"
        );
        connexionThread.ajouterConnexionListener(this);
        connexionThread.start();
    }

    @Override
    public void connexionLocale() {
        connexionThread = new ConnexionThread(
                "hopital",
                "hopital", "pm1caalceymgpv0vm7lprg8ipfknux57"
        );
        connexionThread.ajouterConnexionListener(this);
        connexionThread.start();
    }

    @Override
    public void connexionReussie(Connexion connexion) {
        this.connexion = connexion;
        connexionDialog.setVisible(false);
    }

    @Override
    public void connexionEchouee() {
        connexionDialog.setMessage("Erreur de connexion !!!");
    }
}
