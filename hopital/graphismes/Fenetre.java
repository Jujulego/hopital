package hopital.graphismes;

import com.jcraft.jsch.JSchException;
import hopital.acces.Employe;
import hopital.connexion.Connexion;
import hopital.connexion.ConnexionThread;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.Graphics;
import java.util.LinkedList;

public class Fenetre extends JFrame implements ConnexionECEDialog.ConnexionListener, ConnexionThread.ConnexionListener, ActionListener {
    // Attrinuts
    ConnexionECEDialog connexionDialog = new ConnexionECEDialog();
    ConnexionThread connexionThread;
    Connexion connexion;

    JButton option1;
    JButton option2;

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
        connexionThread = new ConnexionThread(
                utilisateur, new String(motDePasse),
                "jc151870", ""
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

        //Ben caca
        option1=new JButton("Option1");
        option2=new JButton("Option2");

        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
        add(option1);
        add(option2);

        option1.addActionListener(this);
        option2.addActionListener(this);

        getContentPane().validate();
        getContentPane().repaint();

    }

    @Override
    public void connexionEchouee() {
        connexionDialog.setMessage("Erreur de connexion !!!");
    }


    //Ben
    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g);
        Color oldColor = g.getColor();
        Color couleur;
        couleur=Color.red;
        g.setColor( couleur );
        g.drawString("Bonjours , on est le (daet), vous avez n projet",60,60);
        g.setColor( oldColor );

    }

    // Evenements
    @Override
    public void actionPerformed(ActionEvent e) {
        Object Source=e.getSource();

        if(Source==option1)
        {
            System.out.println("Option 1");
            try {
                LinkedList<Employe> employes = Employe.tousEmployes(connexion);
                JList<Employe> list = new JList<>(employes.toArray(new Employe[employes.size()]));

                JScrollPane scroll = new JScrollPane(list);
                add(scroll);

                getContentPane().validate();
                getContentPane().repaint();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        }
        if(Source==option2)
        {
            System.out.println("Option 2");
        }






    }



}
