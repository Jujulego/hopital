package hopital.acces;

import com.sun.istack.internal.Nullable;
import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Infirmier extends Employe {
    // Attributs
    private Service service;
    private String rotation;
    private int salaire;

    // Constructeur
    protected Infirmier() {
        super();
    }

    /**
     * Récupère un infirmier à partir de son numero
     *
     * @param numero numero de l'infirmier
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Infirmier(int numero, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select employe.numero,nom,prenom,tel,adresse,code_service,rotation,salaire " +
                        "from employe inner join infirmier on employe.numero = infirmier.numero " +
                        "where infirmier.numero=?"
        );
        requete.setInt(1, numero);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, connexion);
    }

    /**
     * Récupère un infirmier
     *
     * @param nom nom de l'infirmier
     * @param prenom prenom de l'infirmier
     * @param tel téléphone de l'infirmier
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Infirmier(String nom, String prenom, String tel, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select employe.numero,nom,prenom,tel,adresse,code_service,rotation,salaire " +
                        "from employe inner join infirmier on employe.numero = infirmier.numero " +
                        "where nom=? and prenom=? and tel=?"
        );
        requete.setString(1, nom);
        requete.setString(2, prenom);
        requete.setString(3, tel);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, connexion);
    }

    // Méthodes statiques
    public static Infirmier creerInfirmier(int numero, String nom, String prenom, String adresse, String telephone, String mutuelle, Service service, String rotation, int salaire, Connexion connexion) throws SQLException {
        // Requête
        PreparedStatement requete = connexion.prepRequete("insert into employe values (?, ?, ?, ?, ?)");
        requete.setInt(1, numero);
        requete.setString(2, nom);
        requete.setString(3, prenom);
        requete.setString(4, adresse);
        requete.setString(5, telephone);

        requete.execute();

        requete = connexion.prepRequete("insert into infirmier values (?, ?, ?, ?)");
        requete.setInt(1, numero);
        requete.setString(2, service.getCode());
        requete.setString(3, rotation);
        requete.setInt(4, salaire);

        requete.execute();

        // Création de l'objet
        Infirmier infirmier = new Infirmier();
        infirmier.numero = numero;
        infirmier.nom = nom;
        infirmier.prenom = prenom;
        infirmier.adresse = adresse;
        infirmier.telephone = telephone;
        infirmier.service = service;
        infirmier.rotation = rotation;
        infirmier.salaire = salaire;

        return infirmier;
    }

    /**
     * Récupère tous les infirmiers !
     *
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static LinkedList<Infirmier> tousInfirmiers(Connexion connexion) throws SQLException {
        // Requête
        ResultSet resultSet = connexion.execSelect(
                "select employe.numero,nom,prenom,tel,adresse,code_service,rotation,salaire " +
                        "from employe inner join infirmier on employe.numero = infirmier.numero"
        );

        // Construction du résultat
        return listeInfirmiers(resultSet, connexion);
    }

    /**
     * Construit une liste d'infirmiers, basée sur la liste donnée
     */
    public static LinkedList<Infirmier> listeInfirmiers(ResultSet resultSet, Connexion connexion) throws SQLException {
        try {
            return listeObjets(Infirmier.class, resultSet, connexion);
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return new LinkedList<>();
    }

    // Méthodes
    /**
     * Remplit l'objet avec les champs de la requete
     *
     * @param resultSet resultat d'une recherche.
     * @param connexion connexionECE à la base de donnée (objets liés)
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    @Override
    protected void remplir(ResultSet resultSet, Connexion connexion) throws SQLException {
        super.remplir(resultSet, connexion); // Remplissage de la partie employé

        // Infirmier
        rotation = resultSet.getString("rotation");
        salaire  = resultSet.getInt("salaire");

        String no_service = resultSet.getString("code_service");
        service = no_service == null ? null : new Service(no_service, connexion);
    }

    @Override
    public String toString() {
        if (service == null) {
            return String.format("<Infirmier n°%02d (%s): %s %s, %s, %s, %s€>", getNumero(), rotation, getPrenom(), getNom(), getAdresse(), getTelephone(), salaire);
        }

        return String.format("<Infirmier n°%02d (%s %s): %s %s, %s, %s, %s€>", getNumero(), rotation, service.getNom(), getPrenom(), getNom(), getAdresse(), getTelephone(), salaire);
    }

    @Override
    public void sauver(Connexion connexion) throws SQLException {
        // Gardien
        if (!modifie) return;
        if (supprime) return;

        // Méthode de la classe mère
        super.sauver(connexion);

        // Requête
        PreparedStatement requete = connexion.prepRequete(
                "update docteur " +
                        "set code_service=?, rotation=?, salaire=? " +
                        "where numero = ?"
        );

        requete.setString(1, service.getCode());
        requete.setString(2, rotation);
        requete.setInt(3, salaire);
        requete.setInt(4, numero);

        requete.execute();
    }

    @Override
    public void supprimer(Connexion connexion) throws SQLException {
        // Gardien
        if (!supprime) return;

        // Requete
        PreparedStatement requete = connexion.prepRequete(
                "delete from infirmier " +
                        "where numero = ?"
        );

        requete.setInt(1, numero);

        requete.execute();

        // suppression de l'employé
        super.supprimer(connexion);
    }

    /**
     * Renvoie la liste des chambres que surveille l'infirmier
     *
     * @param connexion connexionECE à la base de données
     * @return liste des chambres
     */
    public LinkedList<Chambre> chambresSurveillees(Connexion connexion) throws SQLException {
        // Requetes
        PreparedStatement requete = connexion.prepRequete(
                "select code_service,no_chambre,surveillant,nb_lits " +
                        "from chambre " +
                        "where surveillant=?"
        );
        requete.setInt(1, numero);

        return Chambre.listeChambres(requete.executeQuery(), connexion);
    }

    // - accesseurs
    @Nullable
    public Service getService() {
        return service;
    }
    public void setService(Service service) {
        this.service = service;
        modifie = true;
    }

    public String getRotation() {
        return rotation;
    }
    public void setRotation(String rotation) {
        this.rotation = rotation;
        modifie = true;
    }

    public int getSalaire() {
        return salaire;
    }
    public void setSalaire(int salaire) {
        this.salaire = salaire;
        modifie = true;
    }
}
