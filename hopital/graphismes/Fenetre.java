package hopital.graphismes;

import com.jcraft.jsch.JSchException;
import hopital.connexion.Connexion;

import javax.swing.*;
import java.sql.SQLException;

public class Fenetre extends JFrame implements ConnexionECEDialog.ConnexionListener {
    // Attrinuts
    ConnexionECEDialog connexionDialog = new ConnexionECEDialog();
    Connexion connexion;

    // Constructeur
    public Fenetre() {
        // Paramètres
        setSize(1080, 760);
        setVisible(true);

        // Activation de la boite de dialogue
        connexionDialog.setVisible(true);
        connexionDialog.ajouterConnexionListener(this);
    }

    // Méthodes
    @Override
    public void connexionECE(String utilisateur, char[] motDePasse) {
        try {
            connexion = new Connexion(
                    utilisateur, new String(motDePasse),
                    "jc151870", ""
            );
            connexionDialog.setVisible(false);
        } catch (JSchException | SQLException | ClassNotFoundException e) {
            connexionDialog.setMessage("Erreur de connexion !!!");
            e.printStackTrace();
        }
    }

    @Override
    public void connexionLocale() {
        try {
            connexion = new Connexion(
                    "hopital",
                    "hopital", "pm1caalceymgpv0vm7lprg8ipfknux57"
            );
            connexionDialog.setVisible(false);
        } catch (SQLException | ClassNotFoundException e) {
            connexionDialog.setMessage("Erreur de connexion !!!");
            e.printStackTrace();
        }
    }
}
