package hopital.graphismes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;

public class ConnexionECEDialog extends JDialog {
    // Attributs
    private JTextField champUtilisateur = new JTextField(10);
    private JPasswordField champMotDePasse = new JPasswordField(10);
    private JButton btnConnexion = new JButton("Connexion ECE");
    private JButton btnLocale = new JButton("Connexion Locale");
    private JLabel lblErreur = new JLabel(" ");

    private LinkedList<ConnexionListener> listeners = new LinkedList<>();

    // Constructeur
    public ConnexionECEDialog() {
        // Paramètres
        setSize(300, 196);
        setResizable(false);
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

        // - erreur !
        add(lblErreur);

        // - boutons
        JPanel boutons = new JPanel();
        boutons.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
        boutons.add(btnLocale);
        boutons.add(btnConnexion);
        add(boutons);



        // Evenements
        DocumentListener documentListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                btnConnexion.setEnabled(
                           champMotDePasse.getPassword().length != 0
                        && champUtilisateur.getDocument().getLength() != 0
                );
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                btnConnexion.setEnabled(
                        champMotDePasse.getPassword().length != 0
                                && champUtilisateur.getDocument().getLength() != 0
                );
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                btnConnexion.setEnabled(
                        champMotDePasse.getPassword().length != 0
                                && champUtilisateur.getDocument().getLength() != 0
                );
            }
        };

        btnConnexion.setEnabled(false);
        champUtilisateur.getDocument().addDocumentListener(documentListener);
        champMotDePasse.getDocument().addDocumentListener(documentListener);

        btnConnexion.addActionListener((ActionEvent actionEvent) -> {
            setMessage("Connexion en cours ...");

            String utilisateur = champUtilisateur.getText();
            char[] motdepasse = champMotDePasse.getPassword();

            for (ConnexionListener listener : listeners) {
                listener.connexionECE(utilisateur, motdepasse);
            }
        });

        btnLocale.addActionListener((ActionEvent actionEvent) -> {
            setMessage("Connexion en cours ...");

            for (ConnexionListener listener : listeners) {
                listener.connexionLocale();
            }
        });
    }

    // Méthodes
    public void ajouterConnexionListener(ConnexionListener listener) {
        listeners.add(listener);
    }

    public void setMessage(String erreur) {
        lblErreur.setText(erreur);
        lblErreur.repaint();
    }

    // Interface
    public interface ConnexionListener {
        void connexionECE(String utilisateur, char[] motDePasse);
        void connexionLocale();
    }
}
