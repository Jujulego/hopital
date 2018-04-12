package hopital.acces;

import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Hospitalisation {
    // Attributs
    private Malade malade;
    private Service service;
    private Chambre chambre;
    private Integer lit;

    // Constructeur
    private Hospitalisation() {

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
     * Récupère tous les hospitalisations !
     *
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static LinkedList<Hospitalisation> toutesHospitalisation(Connexion connexion) throws SQLException {
        // Requête
        ResultSet resultSet = connexion.execSelect(
                "select no_malade,code_service,no_chambre,lit " +
                        "from hospitalisation"
        );

        // Construction du résultat
        LinkedList<Hospitalisation> hospitalisations = new LinkedList<>();

        resultSet.beforeFirst();
        while (resultSet.next()) {
            Hospitalisation hospitalisation = new Hospitalisation();
            hospitalisation.malade = new Malade(resultSet.getInt("no_malade"), connexion);
            hospitalisation.service = new Service(resultSet.getString("code_service"), connexion);
            hospitalisation.chambre = new Chambre(hospitalisation.service, resultSet.getInt("no_chambre"), connexion);
            hospitalisation.lit = resultSet.getInt("lit");

            hospitalisations.addLast(hospitalisation);
        }

        return hospitalisations;
    }

    // Méthode
    @Override
    public String toString() {
        return String.format("<Hospitalisation : %s %s => %d n°%02d %s>", malade.getPrenom(), malade.getNom(), lit, chambre.getNumero(), service.getNom());
    }

    // - accesseurs
    public Malade getMalade() {
        return malade;
    }
    public Service getService() {
        return service;
    }
    public Chambre getChambre() {
        return chambre;
    }
    public Integer getLit() {
        return lit;
    }
}
