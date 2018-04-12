package hopital.acces;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Correspond à la table "chambre" de la base de données
 *
 * @author julien
 */
public class Chambre {
    // Attributs
    private int numero;
    private Service service;
    private Infirmier surveillant;
    private int nbLits;

    // Constructeur
    private Chambre() {

    }

    public Chambre(Service service, int numero, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select no_chambre,surveillant,nb_lits " +
                        "from chambre " +
                        "where code_service=? and no_chambre=?"
        );
        requete.setString(1, service.getCode());
        requete.setInt(2, numero);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, service, connexion);
    }

    // Méthodes statiques
    /**
     * Récupère toutes les chambres !
     *
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static LinkedList<Chambre> toutesChambres(Connexion connexion) throws SQLException {
        // Requête
        ResultSet resultSet = connexion.execSelect(
                "select code_service,no_chambre,surveillant,nb_lits " +
                        "from chambre"
        );

        // Construction du résultat
        return listeChambres(resultSet, connexion);
    }

    /**
     * Construit une liste de chambres
     *
     * @param resultSet resultat d'une requete
     * @param connexion pour récupérer les objets liés
     * @return liste de chambres
     *
     * @throws SQLException erreur dans le resultat donné
     */
    public static LinkedList<Chambre> listeChambres(ResultSet resultSet, Connexion connexion) throws SQLException {
        LinkedList<Chambre> chambres = new LinkedList<>();

        resultSet.beforeFirst();
        while (resultSet.next()) {
            Chambre chambre = new Chambre();
            chambre.remplir(resultSet, connexion);

            chambres.addLast(chambre);
        }

        return chambres;
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
    private void remplir(ResultSet resultSet, Connexion connexion) throws SQLException {
        remplir(resultSet, new Service(resultSet.getString("code_service"), connexion), connexion);
    }

    /**
     * Remplit l'objet avec les champs de la requete
     *
     * @param resultSet resultat d'une recherche.
     * @param service service contenant la chambre
     * @param connexion connexionECE à la base de donnée (objets liés)
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    private void remplir(ResultSet resultSet, Service service, Connexion connexion) throws SQLException {
        numero = resultSet.getInt("no_chambre");
        nbLits = resultSet.getInt("nb_lits");

        this.service = service;

        int no_infirmier = resultSet.getInt("surveillant");
        surveillant = no_infirmier == 0 ? null : new Infirmier(no_infirmier, connexion);
    }

    @Override
    public String toString() {
        if (surveillant == null) {
            return String.format("<Chambre n°%02d %s : %d lits>", numero, service.getNom(), nbLits);
        }

        return String.format("<Chambre n°%02d %s : %d lits, surv = %s %s>", numero, service.getNom(), nbLits, surveillant.getPrenom(), surveillant.getNom());
    }

    // - accesseurs
    public int getNumero() {
        return numero;
    }

    @NotNull
    public Service getService() {
        return service;
    }

    @Nullable
    public Infirmier getSurveillant() {
        return surveillant;
    }

    public int getNbLits() {
        return nbLits;
    }
}
