package hopital.acces;

import com.sun.istack.internal.Nullable;
import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Représente une hospitalisation
 */
public class Hospitalisation extends DataObject {
    // Attributs
    private Malade malade;
    private Service service;
    private Chambre chambre;
    private Integer lit;

    // Constructeur
    protected Hospitalisation() {

    }
    public Hospitalisation(Malade malade, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select code_service,no_chambre,lit " +
                        "from hospitalisation " +
                        "where no_malade=?"
        );
        requete.setInt(1, malade.getNumero());

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        this.malade = malade;
        service = new Service(resultSet.getString("code_service"), connexion);
        chambre = new Chambre(service, resultSet.getInt("no_chambre"), connexion);
        lit = resultSet.getInt("lit");
    }
    public Hospitalisation(Service service, Chambre chambre, int lit, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select no_malade " +
                        "from hospitalisation " +
                        "where code_service=? and no_chambre=? and lit=?"
        );
        requete.setString(1, service.getCode());
        requete.setInt(2, chambre.getNumero());
        requete.setInt(3, lit);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        malade = new Malade(resultSet.getInt("no_malade"), connexion);
        this.service = service;
        this.chambre = chambre;
        this.lit = lit;
    }

    // Méthodes statiques
    /**
     * Crée une nouvelle hspitalisation
     *
     * @param malade patient concerné
     * @param service service
     * @param chambre chambre
     * @param lit numéro du lit dans la chambre
     * @param connexion connexion à la base de données
     * @return la nouvelle hospitalisation
     *
     * @throws SQLException erreur de communication
     */
    public static Hospitalisation creerHospitalisation(Malade malade, Service service, Chambre chambre, int lit, Connexion connexion) throws SQLException {
        // Requête
        PreparedStatement requete = connexion.prepRequete("insert into hospitalisation values (?, ?, ?, ?)");
        requete.setInt(1, malade.getNumero());
        requete.setString(2, service.getCode());
        requete.setInt(3, chambre.getNumero());
        requete.setInt(4, lit);

        requete.execute();

        // Création de l'objet
        Hospitalisation hospitalisation = new Hospitalisation();
        hospitalisation.malade = malade;
        hospitalisation.service = service;
        hospitalisation.chambre = chambre;
        hospitalisation.lit = lit;

        return hospitalisation;
    }

    /**
     * Récupère tous les hospitalisations !
     *
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static LinkedList<Hospitalisation> toutesHospitalisations(Connexion connexion) throws SQLException {
        // Requête
        ResultSet resultSet = connexion.execSelect(
                "select no_malade,code_service,no_chambre,lit " +
                        "from hospitalisation"
        );

        // Construction du résultat
        return listeHospitalisations(resultSet, connexion);
    }

    /**
     * Construit une liste d'hospitalisations
     *
     * @param resultSet resultat d'une requete
     * @param connexion pour récupérer les objets liés
     * @return liste de chambres
     *
     * @throws SQLException erreur dans le resultat donné
     */
    public static LinkedList<Hospitalisation> listeHospitalisations(ResultSet resultSet, Connexion connexion) throws SQLException {
        try {
            return listeObjets(Hospitalisation.class, resultSet, connexion);
        } catch (IllegalAccessException | InstantiationException e) {
            // N'arrive pas !
            e.printStackTrace();
        }

        return new LinkedList<>();
    }

    // Méthodes
    @Override
    protected void remplir(ResultSet resultSet, Connexion connexion) throws SQLException {
        malade = new Malade(resultSet.getInt("no_malade"), connexion);
        service = new Service(resultSet.getString("code_service"), connexion);
        chambre = new Chambre(service, resultSet.getInt("no_chambre"), connexion);
        lit = resultSet.getInt("lit");
    }

    @Override
    public String toString() {
        return String.format("<Hospitalisation : %s %s => %d n°%02d %s>", malade.getPrenom(), malade.getNom(), lit, chambre.getNumero(), service.getNom());
    }

    @Override
    public void sauver(Connexion connexion) throws SQLException {
        // Gardien
        if (!modifie) return;
        if (supprime) return;

        // Requête
        PreparedStatement requete = connexion.prepRequete(
                "update hospitalisation " +
                        "set code_service=?, no_chambre=?, lit=? " +
                        "where no_malade = ?"
        );

        requete.setString(1, service.getCode());
        requete.setInt(2, chambre.getNumero());
        requete.setInt(3, lit);
        requete.setInt(4, malade.getNumero());

        requete.execute();
        modifie = false;
    }

    @Override
    public void supprimer(Connexion connexion) throws SQLException {
        // Gardien
        if (supprime) return;

        // Requête
        PreparedStatement requete = connexion.prepRequete(
                "delete from hospitalisation " +
                        "where malade = ?"
        );

        requete.setInt(1, malade.getNumero());

        requete.execute();
        supprime = true;
    }

    // - accesseurs
    public Malade getMalade() {
        return malade;
    }

    public Service getService() {
        return service;
    }
    public void setService(Service service) {
        this.service = service;
        modifie = true;
    }

    public Chambre getChambre() {
        return chambre;
    }
    public void setChambre(Chambre chambre) {
        this.chambre = chambre;
        modifie = true;
    }

    @Nullable
    public Integer getLit() {
        return lit;
    }
    public void setLit(Integer lit) {
        this.lit = lit;
        modifie = true;
    }
}
