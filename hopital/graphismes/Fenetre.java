package hopital.graphismes;

import com.jcraft.jsch.JSchException;
import hopital.acces.Docteur;
import hopital.acces.Employe;
import hopital.acces.Infirmier;
import hopital.acces.Service;
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
import java.sql.ResultSet;
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

    JList<String> j1_metier;
    JList<String> j2_specification;
    JList<String> j3_info;

    JScrollPane scroll;
    JScrollPane scroll2;
    JScrollPane scroll3;





    //j1_metier
    DefaultListModel<String> trois_metier = new DefaultListModel<>();

    //j2_specification
    DefaultListModel<String> ListeMetier_docteur= new DefaultListModel<>();
    DefaultListModel<String> ListeMetier_infirmier= new DefaultListModel<>();
    DefaultListModel<String> ListeMetier_personne= new DefaultListModel<>();

    //j3_info
    DefaultListModel<String> ListeInfo = new DefaultListModel<>();


    // Constructeur
    public Fenetre() {
        // Paramètres
        setSize(1080, 760);
        setVisible(true);

        // Activation de la boite de dialogue
        connexionDialog.setVisible(true);
        connexionDialog.setLocationRelativeTo(this);
        connexionDialog.ajouterConnexionListener(this);

        //Chargement des listes
        trois_metier.addElement("Docteur");
        trois_metier.addElement("Infirmier");
        trois_metier.addElement("Patient");

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

        //Ben caca
        option1=new JButton("Mise a jour");
        option2=new JButton("Recherche");
        option3=new JButton("Generation");

        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
        add(option1);
        add(option2);
        add(option3);

        option1.addActionListener(this);
        option2.addActionListener(this);
        option3.addActionListener(this);

        //On rempli la ListeMetier_infirmier avec  tous_Services qui fait les requetes
        try {

            LinkedList<Service>tous_Services= Service.tousServices(connexion);
            for(Service m_service:tous_Services){
                ListeMetier_infirmier.addElement(m_service.getNom());
            }

            ResultSet requete_liste_metier_docteur = connexion.execSelect("select distinct specialite from docteur");
            requete_liste_metier_docteur.beforeFirst();
            while(requete_liste_metier_docteur.next())
            {
                ListeMetier_docteur.addElement(requete_liste_metier_docteur.getString("specialite"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        getContentPane().validate();
        getContentPane().repaint();

    }

    @Override
    public void connexionEchouee() {
        connexionDialog.setMessage("Erreur de connexion !!!");
    }


    // Evenements
    @Override
    public void actionPerformed(ActionEvent e) {
        Object Source=e.getSource();

        if(Source==option1)
        {
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
            //option1.setEnabled(false);

        }


        if(Source==option2)
        {
            getContentPane().removeAll();

            getContentPane().revalidate();
            getContentPane().repaint();

            afficher_j1_metier();

        }


        if(Source==option3)
        {

        }

    }


    public void afficher_j1_metier(){
        j1_metier= new JList<>(trois_metier);
        scroll = new JScrollPane(j1_metier);

        j2_specification= new JList(ListeMetier_personne);
        scroll2=new JScrollPane(j2_specification);

        j3_info=new JList<>(ListeInfo);
        scroll3=new JScrollPane(j3_info);

        scroll2.setVisible(false);
        scroll3.setVisible(false);

        j1_metier.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                String a;
                if(!e.getValueIsAdjusting()) {
                    final List<String> selectedValuesList = j1_metier.getSelectedValuesList();
                    a=selectedValuesList.toString();
                    //On enleve le dernier caractere qui est un ]
                    a=a.replaceAll("]","");

                    //On enleve le 1er caractère qui est un [
                    a=a.substring(1);

                    ListeMetier_personne.clear();
                    ListeInfo.clear();



                    afficher_j2_specification(a);



                }
            }
        });

        add(scroll);
        getContentPane().validate();
        getContentPane().repaint();
    }

    public void afficher_j2_specification(String a){
        scroll2.setVisible(true);
        scroll3.setVisible(false);

        if(a.equals("Docteur"))
        {

           for(int i=0;i<ListeMetier_docteur.size();i++)
            {
                //listModel.addElement(ListeMetier_docteur.getElementAt(i));
                ListeMetier_personne.addElement(ListeMetier_docteur.getElementAt(i));
            }

            //listModel.addElement(ListeMetier_docteur);
            //j2_specification.setModel(ListeMetier_docteur);
        }

        if(a.equals("Infirmier"))
        {

            for(int i=0;i<ListeMetier_infirmier.size();i++)
            {
                ListeMetier_personne.addElement(ListeMetier_infirmier.getElementAt(i));
            }
            //j2_specification.setModel(ListeMetier_infirmier);
            //listModel.addElement(ListeMetier_infirmier);

        }
        if(a.equals("Patient"))
        {
            ListeMetier_personne.clear();
            ListeInfo.clear();

            getContentPane().validate();
            getContentPane().repaint();

        }

        //j2_specification.setModel(listModel);
        //j2_specification=new JList<>(listModel);

        j2_specification.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                String a;
                if(!e.getValueIsAdjusting()) {
                    final List<String> selectedValuesList = j2_specification.getSelectedValuesList();
                    a=selectedValuesList.toString();

                    //On enleve le dernier caractere qui est un ]
                    a=a.replaceAll("]","");

                    //On enleve le 1er caractère qui est un [
                    a=a.substring(1);
                    ListeInfo.clear();
                    afficher_j3_info(a);
                }
            }
        });



        add(scroll2);

        getContentPane().validate();
        getContentPane().repaint();

    }


    public void afficher_j3_info(String a){
        scroll3.setVisible(true);

        DefaultListModel<String>a_afficher=new DefaultListModel<>();

        try {
            LinkedList<Employe> employes = Employe.tousEmployes(connexion);
            for(int i=0;i<employes.size();i++)
            {
                Employe e=employes.get(i);

                if( (e instanceof Docteur) && ((Docteur) e).getSpecialite().equals(a) )
                {
                    ListeInfo.addElement(e.toString());

                }

                if( ( e instanceof Infirmier) && (((Infirmier) e).getService().toString()).contains(a) )
                {
                    ListeInfo.addElement(e.toString());
                }
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        //j3_info.setModel(a_afficher);

        add(scroll3);

        getContentPane().validate();
        getContentPane().repaint();


    }








}
