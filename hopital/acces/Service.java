package hopital.acces;

import com.sun.istack.internal.Nullable;
import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Service {
    // Attributs
    private String code;
    private String nom;
    private String batiment;
    private Docteur directeur = null;

    // Constructeurs
    private Service() {

    }

    /**
     * Récupère un service à partir de son code
     *
     * @param code code du service
     * @param connexion connexion à la base de donnée
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
    /**
     * Récupère un service à partir de son nom
     *
     * @param nom nom du service
     * @param connexion connexion à la base de donnée
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
     * @param connexion connexion à la base de donnée
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
        LinkedList<Service> services = new LinkedList<>();

        resultSet.beforeFirst();
        while (resultSet.next()) {
            Service service = new Service();
            service.remplir(resultSet, connexion);

            services.addLast(service);
        }

        return services;
    }

    // Méthodes
    /**
     * Remplit l'objet avec les champs de la requete
     *
     * @param resultSet resultat d'une recherche.
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    private void remplir(ResultSet resultSet, Connexion connexion) throws SQLException {
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

    @Nullable
    public Docteur getDirecteur() {
        return directeur;
    }
}
