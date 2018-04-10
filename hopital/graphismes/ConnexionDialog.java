package hopital.graphismes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

public class ConnexionDialog extends JDialog {
    // Attributs
    private JTextField champUtilisateur = new JTextField(10);
    private JPasswordField champMotDePasse = new JPasswordField(10);
    private JButton btnConnexion = new JButton("Connexion");

    private LinkedList<ConnexionListener> listeners = new LinkedList<>();

    // Constructeur
    public ConnexionDialog() {
        // Paramètres
        setSize(300,200);
        setTitle("Connexion à l'ECE");
        setLayout(new FlowLayout(FlowLayout.CENTER, 16, 16));

        // Elements
        // - formulaire
        JPanel formulaire = new JPanel();
        formulaire.setLayout(new GridLayout(2, 2, 8, 16));
        formulaire.add(new JLabel("Utilisateur :"));
        formulaire.add(champUtilisateur);
        formulaire.add(new JLabel("Mot de passe :"));
        formulaire.add(champMotDePasse);
        add(formulaire);

        add(btnConnexion);
        btnConnexion.addActionListener((ActionEvent actionEvent) -> {
            String utilisateur = champUtilisateur.getText();
            char[] motdepassse = champMotDePasse.getPassword();

            for (ConnexionListener listener : listeners) {
                listener.connexion(utilisateur, motdepassse);
            }
        });
    }

    // Méthodes
    public void ajouterConnexionListener(ConnexionListener listener) {
        listeners.add(listener);
    }

    // Interface
    public interface ConnexionListener {
        void connexion(String utilisateur, char[] motDePasse);
    }
}
