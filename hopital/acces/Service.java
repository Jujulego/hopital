package hopital.acces;

import com.sun.istack.internal.Nullable;
import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Service extends DataObject {
    // Attributs
    private String code;
    private String nom;
    private String batiment;
    private Docteur directeur = null;

    // Constructeurs
    protected Service() {

    }

    /**
     * Récupère un service à partir de son code
     *
     * @param code code du service
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Service(String code, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select code,nom,batiment,directeur " +
                        "from service " +
                        "where code=?"
        );
        requete.setString(1, code);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, connexion);
    }

    // Méthodes statiques
    public static Service creerService(String code, String nom, String batiment, Docteur directeur, Connexion connexion) throws SQLException {
        // Requête
        PreparedStatement requete = connexion.prepRequete(
                "insert into service values (?, ?, ?, ?)"
        );
        requete.setString(1, code);
        requete.setString(2, nom);
        requete.setString(3, batiment);
        requete.setInt(4, directeur.getNumero());

        requete.execute();

        // Création de l'objet
        Service service = new Service();
        service.code = code;
        service.nom = nom;
        service.directeur = directeur;
        service.batiment = batiment;

        return service;
    }

    /**
     * Récupère un service à partir de son nom
     *
     * @param nom nom du service
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static Service nomService(String nom, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select code,nom,batiment,directeur " +
                        "from service " +
                        "where nom=?"
        );
        requete.setString(1, nom);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        Service service = new Service();
        service.remplir(resultSet, connexion);

        return service;
    }

    /**
     * Récupère tous les services !
     *
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static LinkedList<Service> tousServices(Connexion connexion) throws SQLException {
        // Requête
        ResultSet resultSet = connexion.execSelect(
                "select code,nom,batiment,directeur " +
                        "from service"
        );

        // Construction du résultat
        return listeServices(resultSet, connexion);
    }

    /**
     * Construit une liste de services
     *
     * @param resultSet resultat d'une requete
     * @param connexion pour récupérer les objets liés
     * @return liste de chambres
     *
     * @throws SQLException erreur dans le resultat donné
     */
    public static LinkedList<Service> listeServices(ResultSet resultSet, Connexion connexion) throws SQLException {
        try {
            return listeObjets(Service.class, resultSet, connexion);
        } catch (IllegalAccessException | InstantiationException e) {
            // N'arrive pas !
            e.printStackTrace();
        }

        return new LinkedList<>();
    }

    // Méthodes
    /**
     * Remplit l'objet avec les champs de la requete
     *
     * @param resultSet resultat d'une recherche.
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    @Override
    protected void remplir(ResultSet resultSet, Connexion connexion) throws SQLException {
        code     = resultSet.getString("code");
        nom      = resultSet.getString("nom");
        batiment = resultSet.getString("batiment");

        int no_dir = resultSet.getInt("directeur");
        directeur = no_dir == 0 ? null : new Docteur(no_dir, connexion);
    }

    @Override
    public String toString() {
        String str = String.format("<Service %s : %s, batiment %s", code, nom, batiment);

        if (directeur != null) {
            str += String.format(", directeur : %s %s>", directeur.getPrenom(), directeur.getNom());
        } else {
            str += ">";
        }

        return str;
    }

    @Override
    public void sauver(Connexion connexion) throws SQLException {
        // Gardien
        if (!modifie) return;
        if (supprime) return;

        // Requête
        PreparedStatement requete = connexion.prepRequete(
                "update service " +
                        "set batiment=?, directeur=? " +
                        "where code like ?"
        );

        requete.setString(1, batiment);
        requete.setInt(2, directeur.getNumero());
        requete.setString(3, code);

        requete.execute();
        modifie = false;
    }

    @Override
    public void supprimer(Connexion connexion) throws SQLException {
        // Gardien
        if (supprime) return;

        // Requête
        PreparedStatement requete = connexion.prepRequete(
                "delete from service " +
                        "where code like ?"
        );

        requete.setString(1, code);

        requete.execute();
        supprime = true;
    }

    /**
     * Renvoie la liste des chambres liées à ce service
     *
     * @param connexion connexion à la base de données
     * @return la liste des chambres
     *
     * @throws SQLException erreur de communication
     */
    public LinkedList<Chambre> getChambres(Connexion connexion) throws SQLException {
        // Requete
        PreparedStatement requete = connexion.prepRequete(
                "select code_service,no_chambre,surveillant,nb_lits " +
                        "from chambre " +
                        "where code_service=?"
        );
        requete.setString(1, code);

        // Resultat
        return Chambre.listeChambres(requete.executeQuery(), connexion);
    }

    // - accesseurs
    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public String getBatiment() {
        return batiment;
    }
    public void setBatiment(String batiment) {
        this.batiment = batiment;
        modifie = true;
    }

    @Nullable
    public Docteur getDirecteur() {
        return directeur;
    }
    public void setDirecteur(Docteur directeur) {
        this.directeur = directeur;
        modifie = true;
    }
}
