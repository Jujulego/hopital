package hopital.graphismes;

import com.jcraft.jsch.JSchException;
import hopital.acces.*;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class Fenetre extends JFrame implements ConnexionECEDialog.ConnexionListener, ConnexionThread.ConnexionListener, ActionListener, ItemListener {
    // Attrinuts
    ConnexionECEDialog connexionDialog = new ConnexionECEDialog();
    ConnexionThread connexionThread;
    Connexion connexion;

    private JPanel panoramix;
    private JPanel jour_nuit;
    private JPanel panel_recherche;
    private JPanel panel_scroll;
    private JButton stats = new JButton("Statistiques");

    //private JButton option1;
    private JButton option2;
    private JButton option3;
    private JButton bouton_recherche;
    private JButton bouton_ajouter;

    private JCheckBox jour;
    private JCheckBox nuit;

    private JTextField recherhe_spe;

    private DefaultListModel<JTextField>ajout=new DefaultListModel<>();

    private JList<String> j1_metier;
    private JList<String> j2_specification;
    private JList<String> j3_info;
    private JList<String> j4_recherche;



    private JScrollPane scroll;
    private JScrollPane scroll2;
    private JScrollPane scroll3;
    private JScrollPane scroll4;


    //j1_metier
    private DefaultListModel<String> trois_metier = new DefaultListModel<>();

    //j2_specification
    private DefaultListModel<String> ListeMetier_docteur= new DefaultListModel<>();
    private DefaultListModel<String> ListeMetier_infirmier= new DefaultListModel<>();
    private DefaultListModel<String> ListeMetier_personne= new DefaultListModel<>();

    private DefaultListModel<String> Liste_Patient= new DefaultListModel<>();

    //j3_info
    private DefaultListModel<String> ListeInfo = new DefaultListModel<>();

    //j4 pour afficher les recherches en particulier
    private DefaultListModel<String> ListeRecherche = new DefaultListModel<>();

    private JComboBox combo = new JComboBox();
    private JComboBox combo_spe_doc = new JComboBox();
    private JComboBox combo_spe_rot = new JComboBox();

    private JPanel jpanel_ajouter = new JPanel();

    private JTextField mutuelle;
    private JTextField service=new JTextField("Service");;

    JTextField numero=new JTextField("numero");
    JTextField nom=new JTextField("nom");
    JTextField prenom=new JTextField("prenom");
    JTextField telephone=new JTextField("telephone");
    JTextField adresse=new JTextField("adresse");
    JTextField salaire=new JTextField("Salaire");

    // Constructeur
    public Fenetre() {
        // Paramètres
        setSize(1080, 760);
        setVisible(true);

        //Chargement des listes
        trois_metier.addElement("Docteur");
        trois_metier.addElement("Infirmier");
        trois_metier.addElement("Patient");


        ajout.addElement(new JTextField("numero"));
        ajout.addElement(new JTextField("numero"));
        ajout.addElement(new JTextField("nom"));
        ajout.addElement(new JTextField("prenom"));
        ajout.addElement(new JTextField("telephone"));
        ajout.addElement(new JTextField("adresse"));


        jour_nuit=new JPanel();
        jour_nuit.setLayout(new FlowLayout());

        panel_recherche=new JPanel();
        panel_recherche.setLayout(new FlowLayout());

        panel_scroll=new JPanel();
        panel_scroll.setLayout(new FlowLayout());


        panoramix=new JPanel();
        panoramix.setLayout(new FlowLayout());
        panoramix.setBackground(Color.BLUE);

        recherhe_spe=new JTextField("Rechercher une personne en particulier");
        recherhe_spe.setSize(200,50);


        combo.setPreferredSize(new Dimension(100, 20));
        combo.addItem("Docteur");
        combo.addItem("Infirmier");
        combo.addItem("Malade");



        jpanel_ajouter.add(numero);
        jpanel_ajouter.add(nom);
        jpanel_ajouter.add(prenom);
        jpanel_ajouter.add(telephone);
        jpanel_ajouter.add(adresse);



        this.setContentPane(panoramix);
        this.setVisible(true);

        // Activation de la boite de dialogue
        connexionDialog.setVisible(true);
        connexionDialog.setLocationRelativeTo(this);
        connexionDialog.ajouterConnexionListener(this);

        stats.addActionListener((ActionEvent event) -> {
            new Statistiques(connexion);
            this.dispose();
        });
    }

    public Fenetre(Connexion connexion) {
        this();

        connexionDialog.dispose();
        connexionReussie(connexion);
    }

    // Méthodes
    @Override
    public void connexionECE(String utilisateur, char[] motDePasse) {
        connexionThread = new ConnexionThread(
                "hopital",
                utilisateur, new String(motDePasse), true
        );
        connexionThread.ajouterConnexionListener(this);
        connexionThread.start();
    }

    @Override
    public void connexionLocale() {
        connexionThread = new ConnexionThread(
                "hopital",
                "hopital", "pm1caalceymgpv0vm7lprg8ipfknux57", false
        );
        connexionThread.ajouterConnexionListener(this);
        connexionThread.start();
    }

    @Override
    public void connexionReussie(Connexion connexion) {
        this.connexion = connexion;
        connexionDialog.setVisible(false);

        //option1=new JButton("Mise a jour");
        option2=new JButton("Recherche");
        option3=new JButton("Generation");



        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
        //add(option1);
        add(option2);
        add(option3);
        add(stats);


        //option1.addActionListener(this);
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

            LinkedList<Malade>Tous_malade= Malade.tousMalades(connexion);
            for(Malade m_malade:Tous_malade){
                Liste_Patient.addElement(m_malade.toString());
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


    //Listener des checkbox jour/nuit
    public void itemStateChanged(ItemEvent e) {
        Object source = e.getItemSelectable();

        String a;
        final List<String> selectedValuesList = j2_specification.getSelectedValuesList();
        a=selectedValuesList.toString();

        //On enleve le dernier caractere qui est un ]
        a=a.replaceAll("]","");

        //On enleve le 1er caractère qui est un [
        a=a.substring(1);
        ListeInfo.clear();
        if(scroll3.isVisible())afficher_j3_info(a);

    }

    // Evenements
    @Override
    public void actionPerformed(ActionEvent e) {
        Object Source=e.getSource();

        //if(Source==option1)
        {

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

            ajouter_personne();
            System.out.println("option 3");
        }

        if(Source==bouton_recherche){
            rechercher_en_particulier(recherhe_spe.getText());
        }

        if(Source==bouton_ajouter){
            if(combo.getSelectedItem().toString().equals("Malade")){
                try{
                    Malade moliere=Malade.creerMalade(Integer.parseInt(numero.getText()),nom.getText(),prenom.getText(),adresse.getText(),telephone.getText(),mutuelle.getText(), connexion);
                    }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }

            if(combo.getSelectedItem().toString().equals("Docteur")){
                try{
                    Docteur nouveau=Docteur.creerDocteur(Integer.parseInt(numero.getText()),nom.getText(),prenom.getText(),adresse.getText(),telephone.getText(),combo_spe_doc.getSelectedItem().toString(), connexion);
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }


                }
/*
            if(combo.getSelectedItem().toString().equals("Infirmier")){
                try{
                    Infirmier bob=Infirmier.creerInfirmier(Integer.parseInt(numero.getText()),nom.getText(),prenom.getText(),adresse.getText(),telephone.getText(),mutuelle.getText(),service.getText(),combo_spe_rot.getSelectedItem().toString(),salaire.getText(), connexion);

                    }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }*/
        }

    }


    public void afficher_j1_metier(){
        j1_metier= new JList<>(trois_metier);
        scroll = new JScrollPane(j1_metier);

        j2_specification= new JList(ListeMetier_personne);
        scroll2=new JScrollPane(j2_specification);

        j3_info=new JList<>(ListeInfo);
        scroll3=new JScrollPane(j3_info);

        j4_recherche=new JList<>(ListeRecherche);
        scroll4=new JScrollPane(j4_recherche);



        scroll2.setVisible(false);
        scroll3.setVisible(false);
        scroll4.setVisible(false);

        jour=new JCheckBox("Jour");
        jour.setSelected(false);
        jour.setVisible(false);
        jour.addItemListener(this);

        nuit= new JCheckBox("Nuit");
        nuit.setSelected(false);
        nuit.setVisible(false);
        nuit.addItemListener(this);

        bouton_recherche=new JButton("Rechercher");
        bouton_recherche.addActionListener(this);

        panel_scroll.add(scroll);
        panel_scroll.add(scroll2);
        panel_scroll.add(scroll3);
        panel_scroll.add(scroll4);

        panel_recherche.add(recherhe_spe);
        panel_recherche.add(bouton_recherche);


        jour_nuit.add(jour);
        jour_nuit.add(nuit);


        panoramix.add(panel_scroll,FlowLayout.LEFT);
        panoramix.add(jour_nuit,FlowLayout.CENTER);
        panoramix.add(panel_recherche,FlowLayout.RIGHT);






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

        getContentPane().validate();
        getContentPane().repaint();
    }

    public void afficher_j2_specification(String a){
        scroll2.setVisible(true);
        scroll3.setVisible(false);
        scroll4.setVisible(false);

        if(a.equals("Docteur"))
        {

           for(int i=0;i<ListeMetier_docteur.size();i++)
            {
                ListeMetier_personne.addElement(ListeMetier_docteur.getElementAt(i));
            }
            jour.setVisible(false);
            nuit.setVisible(false);
        }

        if(a.equals("Infirmier"))
        {
            for(int i=0;i<ListeMetier_infirmier.size();i++)
            {
                ListeMetier_personne.addElement(ListeMetier_infirmier.getElementAt(i));
            }
            jour.setVisible(true);
            nuit.setVisible(true);
        }
        if(a.equals("Patient"))
        {
            for(int i=0;i<Liste_Patient.size();i++)
            {
                ListeMetier_personne.addElement(Liste_Patient.getElementAt(i));
            }

            nuit.setVisible(false);
            jour.setVisible(false);

            getContentPane().validate();
            getContentPane().repaint();

        }

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

        getContentPane().validate();
        getContentPane().repaint();

    }


    public void afficher_j3_info(String a){

        scroll4.setVisible(false);

        try {
            LinkedList<Employe> employes = Employe.tousEmployes(connexion);
            for(int i=0;i<employes.size();i++)
            {
                Employe e=employes.get(i);

                if( (e instanceof Docteur) && ((Docteur) e).getSpecialite().equals(a) )
                {
                    ListeInfo.addElement(e.toString());
                    scroll3.setVisible(true);
                }


                if( ( e instanceof Infirmier) && (((Infirmier) e).getService().getNom().equals(a) ) )
                {
                    if( (((Infirmier) e).getRotation().equals("JOUR")) && jour.isSelected() ){
                        ListeInfo.addElement(e.toString());
                    }
                    if( (((Infirmier) e).getRotation().equals("NUIT") ) && nuit.isSelected() ){
                        ListeInfo.addElement(e.toString());
                    }
                    scroll3.setVisible(true);
                }
            }

        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        getContentPane().validate();
        getContentPane().repaint();

    }

    public void rechercher_en_particulier(String a)
    {
        if(!a.equals("") && !a.equals(" ")){
            ListeRecherche.removeAllElements();
            scroll4.setVisible(true);
            try {
                LinkedList<Employe> employes = Employe.tousEmployes(connexion);

                for(int i=0;i<employes.size();i++){
                    if(employes.get(i).getAdresse().contains(a) || employes.get(i).getNom().contains(a)  || employes.get(i).getPrenom().contains(a) || employes.get(i).getTelephone().contains(a) || String.valueOf(employes.get(i).getNumero()).contains(a) ){
                        ListeRecherche.addElement(employes.get(i).toString());
                    }
                }
                LinkedList<Malade>Tous_malade= Malade.tousMalades(connexion);
                for(Malade m_malade:Tous_malade){
                    if(String.valueOf(m_malade.getNumero()).contains(a) || m_malade.getNom().contains(a) || m_malade.getPrenom().contains(a) || m_malade.getNom().contains(a)     )
                    ListeRecherche.addElement(m_malade.toString());
                }

                if(ListeRecherche.size()==0)
                {
                    ListeRecherche.addElement(a + " Non trouvé !");
                }


                getContentPane().validate();
                getContentPane().repaint();

                recherhe_spe.setText("");


            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }


    }


    public void ajouter_personne(){
        String[] choix = {"Docteur", "Infirmier", "Malade"};
        String[] variable={"Traumatologue" ,"Pneumologue" ,"Cardiologue" ,"Orthopediste" , "Radiologue" ,  "Anesthesiste"};
        String[] rot={"JOUR","NUIT"};
        mutuelle=new JTextField("Mutuelle");

        combo = new JComboBox(choix);
        combo_spe_doc = new JComboBox(variable);
        combo_spe_rot=new JComboBox(rot);

        combo_spe_doc.addActionListener(new ItemAction());
        combo_spe_rot.addActionListener(new ItemAction());

        jpanel_ajouter.add(combo_spe_doc);
        jpanel_ajouter.add(combo_spe_rot);
        jpanel_ajouter.add(mutuelle);
        jpanel_ajouter.add(salaire);
        bouton_ajouter=new JButton("Ajouter personne");


        bouton_ajouter.addActionListener(this);

        combo_spe_rot.setVisible(false);
        mutuelle.setVisible(false);
        salaire.setVisible(false);
        service.setVisible(false);



        jpanel_ajouter.setLayout(new FlowLayout());
        jpanel_ajouter.add(combo);
        jpanel_ajouter.add(combo_spe_doc);
        jpanel_ajouter.add(bouton_ajouter);
        jpanel_ajouter.add(service);

        this.setContentPane(jpanel_ajouter);
        this.setVisible(true);

        combo.addActionListener(new ItemAction());

        System.out.println("ahaha:" + combo.getSelectedItem());


        validate();
        repaint();

    }


    public void ajouter_doc()
    {
        combo_spe_rot.setVisible(false);
        combo_spe_doc.setVisible(true);
        mutuelle.setVisible(false);
        service.setVisible(false);


        validate();
        repaint();
    }

    public void ajouter_inf()
    {
        combo_spe_doc.setVisible(false);
        mutuelle.setVisible(false);
        combo_spe_rot.setVisible(true);
        salaire.setVisible(true);
        service.setVisible(true);


        validate();
        repaint();

    }

    public void ajouter_mal()
    {
        combo_spe_doc.setVisible(false);
        combo_spe_rot.setVisible(false);

        mutuelle.setVisible(true);
        service.setVisible(false);



    }


    class ItemAction implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            if(String.valueOf(combo.getSelectedItem()).equals("Docteur"))
            {
                ajouter_doc();
            }
            if(String.valueOf(combo.getSelectedItem()).equals("Infirmier"))
            {
                ajouter_inf();
            }
            if(String.valueOf(combo.getSelectedItem()).equals("Malade"))
            {
                ajouter_mal();
            }
        }
    }


}
