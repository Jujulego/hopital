package hopital.graphismes;

import com.jcraft.jsch.JSchException;
import hopital.acces.Docteur;
import hopital.acces.Employe;
import hopital.acces.Infirmier;
import hopital.connexion.Connexion;
import hopital.connexion.ConnexionThread;

import javax.print.Doc;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.Graphics;
import java.util.LinkedList;

import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class Fenetre extends JFrame implements ConnexionECEDialog.ConnexionListener, ConnexionThread.ConnexionListener, ActionListener {
    // Attrinuts
    ConnexionECEDialog connexionDialog = new ConnexionECEDialog();
    ConnexionThread connexionThread;
    Connexion connexion;

    JButton option1;
    JButton option2;
    JButton option3;
    JList<Employe> jliste_metier_specifique= new JList<>();

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
        option3=new JButton("Docteur");

        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
        add(option1);
        add(option2);
        add(option3);

        option1.addActionListener(this);
        option2.addActionListener(this);
        option3.addActionListener(this);

        getContentPane().validate();
        getContentPane().repaint();

    }

    @Override
    public void connexionEchouee() {
        connexionDialog.setMessage("Erreur de connexion !!!");
    }


    //Ben
    /*
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
*/
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

                //System.out.println("indice : " + list.getSelectedIndex());
                //System.out.println("indice : " + list.getSelectionMode());


                getContentPane().validate();
                getContentPane().repaint();





            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            //option1.setEnabled(false);

        }

        /*
        if(Source==option2)
        {
            System.out.println("Option 2");
            try {
                LinkedList<Docteur> docteur = Docteur.tousDocteurs(connexion);
                JList<Employe> list = new JList<>(docteur.toArray(new Docteur[docteur.size()]));

                JScrollPane scroll = new JScrollPane(list);
                add(scroll);


                for (int i=0;i<docteur.size();i++)
                {
                    Docteur bunny=docteur.get(i);
                    //if(bunny.getSpecialite()=="")

                }


                getContentPane().validate();
                getContentPane().repaint();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }



        }

        if(Source==option3)
        {
            System.out.println("Option 3");
            try {
                LinkedList<Infirmier> infirmier  = Infirmier.tousInfirmiers(connexion);
                JList<Employe> list = new JList<>(infirmier.toArray(new Infirmier[infirmier.size()]));

                JScrollPane scroll = new JScrollPane(list);
                add(scroll);

                getContentPane().validate();
                getContentPane().repaint();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        */
        if(Source==option3)
        {
            System.out.println("Option 3");

            DefaultListModel<String> ListeMetier = new DefaultListModel<>();
            ListeMetier.addElement("Anesthesiste");
            ListeMetier.addElement("Cardiologue");
            ListeMetier.addElement("Orthopediste");
            ListeMetier.addElement("Pneumologue");
            ListeMetier.addElement("Radiologue");
            ListeMetier.addElement("Traumatologue");


            JList<String> jliste_metier = new JList<>(ListeMetier);

            JScrollPane scroll = new JScrollPane(jliste_metier);
            add(scroll);
            jliste_metier.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            getContentPane().validate();
            getContentPane().repaint();

            jliste_metier.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e)
                {
                    if(!e.getValueIsAdjusting()) {
                        final List<String> selectedValuesList = jliste_metier.getSelectedValuesList();
                        //System.out.println(selectedValuesList);
                        afficher_classe_metier(selectedValuesList.toString());
                    }
                }
            });
        }

    }

    public void afficher_classe_metier(String metier)
    {
        DefaultListModel<Employe> employe_specifique = new DefaultListModel<>();

        try{
            LinkedList<Employe> employes = Employe.tousEmployes(connexion);


            for(int i=0; i<employes.size();i++)
            {
                Employe e=employes.get(i);
                //System.out.println("aaaa: :" + instanceof e );
                if(e instanceof Docteur)
                {
                    String a="[" + ((Docteur) e).getSpecialite() + "]";
                    if(a.equals(metier))
                    {
                        //System.out.println(((Docteur) e).toString());
                        employe_specifique.addElement(e);
                    }
                }

            }
        } catch (SQLException e1) {
        e1.printStackTrace();
    }

    if(employe_specifique.size()!=0){
        //jliste_metier_specifique = new JList<>(employe_specifique);

        //JList.setListData(new String[0]; else jliste_metier_specifique.removeAllElements();
        jliste_metier_specifique.removeAll();

        //jliste_metier_specifique.add(employe_specifique);
        jliste_metier_specifique.setModel(employe_specifique);
        JScrollPane scroll = new JScrollPane(jliste_metier_specifique);
        add(scroll);
        getContentPane().validate();
        getContentPane().repaint();
    }






    }





}
