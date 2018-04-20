package hopital.acces;

import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Correspond à la table "employe" de la base de données
 *
 * @author julien
 */
public class Employe extends DataObject {
    // Attributs
    protected int numero;
    protected String nom;
    protected String prenom;
    protected String telephone;
    protected String adresse;

    // Constructeurs
    protected Employe() {
    }

    /**
     * Récupère un employé à partir de son numero
     *
     * @param numero numero de l'employé
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Employe(int numero, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select numero,nom,prenom,telephone,adresse " +
                        "from employe " +
                        "where numero=?"
        );
        requete.setInt(1, numero);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, null);
    }

    /**
     * Récupère un employé
     *
     * @param nom nom de l'employé
     * @param prenom prenom de l'employé
     * @param telephone téléphone de l'employé
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Employe(String nom, String prenom, String telephone, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select numero,nom,prenom,telephone,adresse " +
                        "from employe " +
                        "where nom=? and prenom=? and telephone=?"
        );
        requete.setString(1, nom);
        requete.setString(2, prenom);
        requete.setString(3, telephone);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, null);
    }

    // Méthodes statiques
    public static Employe creerEmploye(int numero, String nom, String prenom, String adresse, String telephone, Connexion connexion) throws SQLException {
        // Requête
        PreparedStatement requete = connexion.prepRequete("insert into employe values (?, ?, ?, ?, ?)");
        requete.setInt(1, numero);
        requete.setString(2, nom);
        requete.setString(3, prenom);
        requete.setString(4, adresse);
        requete.setString(5, telephone);

        requete.execute();

        // Création de l'objet
        Employe employe = new Employe();
        employe.numero = numero;
        employe.nom = nom;
        employe.prenom = prenom;
        employe.adresse = adresse;
        employe.telephone = telephone;

        return employe;
    }

    /**
     * Récupère tous les employés !
     *
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static LinkedList<Employe> tousEmployes(Connexion connexion) throws SQLException {
        // Requête
        ResultSet resultSet = connexion.execSelect(
                "select * " +
                        "from employe " +
                            "left join docteur on employe.numero = docteur.numero " +
                            "left join infirmier on employe.numero = infirmier.numero"
        );

        // Construction du résultat
        return listeEmployes(resultSet, connexion);
    }

    /**
     * Construit une liste d'employés
     *
     * @param resultSet resultat d'une requete
     * @param connexion pour récupérer les objets liés
     * @return liste des employés
     *
     * @throws SQLException erreur dans le resultat donné
     */
    public static LinkedList<Employe> listeEmployes(ResultSet resultSet, Connexion connexion) throws SQLException {
        // Construiction de la liste
        LinkedList<Employe> employes = new LinkedList<>();

        resultSet.beforeFirst();
        while (resultSet.next()) {
            Employe employe;

            // Construction
            if (resultSet.getString("specialite") != null) {
                employe = new Docteur();

            } else if (resultSet.getString("rotation") != null) {
                employe = new Infirmier();

            } else {
                employe = new Employe();
            }

            // Remplissage
            employe.remplir(resultSet, connexion);
            employes.addLast(employe);
        }

        return employes;
    }

    // Méthodes
    /**
     * Remplit l'objet avec les champs de la requete
     *
     * @param resultSet resultat d'une recherche
     * @param connexion connexionECE à la base de donnée (objets liés)
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    @Override
    protected void remplir(ResultSet resultSet, Connexion connexion) throws SQLException {
        numero  = resultSet.getInt("numero");
        nom     = resultSet.getString("nom");
        prenom  = resultSet.getString("prenom");
        telephone = resultSet.getString("telephone");
        adresse = resultSet.getString("adresse");
    }

    @Override
    public String toString() {
        return String.format("<Employé n°%02d : %s %s, %s, %s>", numero, prenom, nom, adresse, telephone);
    }

    @Override
    public void sauver(Connexion connexion) throws SQLException {
        // Gardien
        if (!modifie) return;
        if (supprime) return;

        // Requête
        PreparedStatement requete = connexion.prepRequete(
                "update employe " +
                        "set nom=?, prenom=?, adresse=?, telephone=? " +
                        "where numero = ?"
        );

        requete.setString(1, nom);
        requete.setString(2, prenom);
        requete.setString(3, adresse);
        requete.setString(4, telephone);
        requete.setInt(5, numero);

        requete.execute();
        modifie = false;
    }

    @Override
    public void supprimer(Connexion connexion) throws SQLException {
        // Gardien
        if (!supprime) return;

        // Requete
        PreparedStatement requete = connexion.prepRequete(
                "delete from employe " +
                        "where numero = ?"
        );

        requete.setInt(1, numero);

        requete.execute();
        supprime = true;
    }

    // - accesseurs
    public int getNumero() {
        return numero;
    }

    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
        modifie = true;
    }

    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
        modifie = true;
    }

    public String getAdresse() {
        return adresse;
    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
        modifie = true;
    }

    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
        modifie = true;
    }
}
