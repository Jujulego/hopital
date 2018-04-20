package hopital.acces;

import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Représente un malade hospitalisé
 */
public class Malade extends DataObject {
    // Attributs
    private int numero;
    private String nom;
    private String prenom;
    private String telephone;
    private String adresse;
    private String mutuelle;

    // Constructeur
    protected Malade() {

    }

    /**
     * Récupère un malade à partir de son numero
     *
     * @param numero numero du malade
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Malade(int numero, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select numero,nom,prenom,telephone,adresse,mutuelle " +
                        "from malade " +
                        "where numero=?"
        );
        requete.setInt(1, numero);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, connexion);
    }

    /**
     * Récupère un malade
     *
     * @param nom nom du malade
     * @param prenom prenom du malade
     * @param telephone téléphone du malade
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Malade(String nom, String prenom, String telephone, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select numero,nom,prenom,telephone,adresse,mutuelle " +
                        "from malade " +
                        "where nom=? and prenom=? and telephone=?"
        );
        requete.setString(1, nom);
        requete.setString(2, prenom);
        requete.setString(3, telephone);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, connexion);
    }

    // Méthodes statiques
    public static Malade creerMalade(int numero, String nom, String prenom, String adresse, String telephone, String mutuelle, Connexion connexion) throws SQLException {
        // Requête
        PreparedStatement requete = connexion.prepRequete("insert into malade values (?, ?, ?, ?, ?, ?)");
        requete.setInt(1, numero);
        requete.setString(2, nom);
        requete.setString(3, prenom);
        requete.setString(4, adresse);
        requete.setString(5, telephone);
        requete.setString(6, mutuelle);

        requete.execute();

        // Création de l'objet
        Malade malade = new Malade();
        malade.numero = numero;
        malade.nom = nom;
        malade.prenom = prenom;
        malade.adresse = adresse;
        malade.telephone = telephone;
        malade.mutuelle = mutuelle;

        return malade;
    }

    /**
     * Récupère tous les malades !
     *
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static LinkedList<Malade> tousMalades(Connexion connexion) throws SQLException {
        // Requête
        ResultSet resultSet = connexion.execSelect(
                "select numero,nom,prenom,telephone,adresse,mutuelle " +
                        "from malade"
        );

        // Construction du résultat
        return listeMalades(resultSet, connexion);
    }

    /**
     * Construit une liste des malades
     */
    public static LinkedList<Malade> listeMalades(ResultSet resultSet, Connexion connexion) throws SQLException {
        try {
            return listeObjets(Malade.class, resultSet, connexion);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return new LinkedList<>();
    }

    // Méthodes
    @Override
    protected void remplir(ResultSet resultSet, Connexion connexion) throws SQLException {
        numero   = resultSet.getInt("numero");
        nom      = resultSet.getString("nom");
        prenom   = resultSet.getString("prenom");
        telephone = resultSet.getString("telephone");
        adresse  = resultSet.getString("adresse");
        mutuelle = resultSet.getString("mutuelle");
    }

    @Override
    public String toString() {
        return String.format("<Malade n°%02d : %s %s, %s, %s>", numero, prenom, nom, adresse, telephone, mutuelle);
    }

    /**
     * Renvoie la liste de docteurs s'occupant de ce patient
     *
     * @param connexion connexionECE à la base de donnée
     * @return liste des docteurs
     *
     * @throws SQLException erreur sur la requête
     */
    public LinkedList<Docteur> getDocteurs(Connexion connexion) throws SQLException {
        // Requete
        PreparedStatement requete = connexion.prepRequete(
                "select employe.numero,nom,prenom,adresse,telephone,specialite " +
                        "from soigne " +
                            "inner join docteur on no_docteur=numero " +
                            "inner join employe on employe.numero = docteur.numero " +
                        "where no_malade=?"
        );
        requete.setInt(1, numero);

        // Construction du résultat
        return Docteur.listeDocteurs(requete.executeQuery(), connexion);
    }

    /**
     * Un docteur de plus suit ce patient
     *
     * @param docteur le nouveau docteur
     * @param connexion connexion à la base
     * @throws SQLException erreur de communication
     */
    public void ajouterDocteur(Docteur docteur, Connexion connexion) throws SQLException {
        // Ajout de la laison :
        PreparedStatement requete = connexion.prepRequete(
                "insert into soigne values (?, ?)"
        );

        requete.setInt(1, docteur.getNumero());
        requete.setInt(2, numero);

        requete.execute();
    }

    /**
     * Un docteur ne suit plus ce patient
     *
     * @param docteur le docteur
     * @param connexion connexion à la base
     * @throws SQLException erreur de communication
     */
    public void enleverDocteur(Docteur docteur, Connexion connexion) throws SQLException {
        // Suppression de la liaison
        PreparedStatement requete = connexion.prepRequete(
                "delete from soigne " +
                        "where no_docteur = ? and no_malade = ?"
        );

        requete.setInt(1, docteur.getNumero());
        requete.setInt(2, numero);

        requete.execute();
    }

    @Override
    public void sauver(Connexion connexion) throws SQLException {
        // Gardien
        if (!modifie) return;
        if (supprime) return;

        // Requête
        PreparedStatement requete = connexion.prepRequete(
                "update malade " +
                        "set nom=?, prenom=?, adresse=?, telephone=?, mutuelle=? " +
                        "where numero = ?"
        );

        requete.setString(1, nom);
        requete.setString(2, prenom);
        requete.setString(3, adresse);
        requete.setString(4, telephone);
        requete.setString(5, mutuelle);
        requete.setInt(6, numero);

        requete.execute();
        modifie = false;
    }

    @Override
    public void supprimer(Connexion connexion) throws SQLException {
        // Gardien
        if (!supprime) return;

        // Requete
        PreparedStatement requete = connexion.prepRequete(
                "delete from malade " +
                        "where numero = ?"
        );

        requete.setInt(1, numero);

        requete.execute();
        supprime = true;
    }

    // - accesseur
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

    public String getMutuelle() {
        return mutuelle;
    }
    public void setMutuelle(String mutuelle) {
        this.mutuelle = mutuelle;
        modifie = true;
    }
}
