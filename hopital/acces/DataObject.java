package hopital.acces;

import hopital.connexion.Connexion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * Base des objets stockés dans la base de données
 */
public abstract class DataObject {
    // Attributs
    protected boolean modifie = false;   // Indique si l'objet à été modifié
    protected boolean supprime = false;  // Indique si l'objet à été supprimé

    // Méthodes statiques
    /**
     * Construit une liste de chambres
     *
     * @param classe classe d'objet
     * @param resultSet resultat d'une requete
     * @param connexion pour récupérer les objets liés
     * @return liste de chambres
     *
     * @throws SQLException erreur dans le resultat donné
     * @throws IllegalAccessException erreur lors de la création d'un objet de la classe T, il faut un constructeur par défaut au minimum protected
     * @throws InstantiationException erreur lors de la création d'un objet de la classe T, il faut un constructeur par défaut
     */
    public static <T extends DataObject> LinkedList<T> listeObjets(Class<T> classe, ResultSet resultSet, Connexion connexion) throws SQLException, IllegalAccessException, InstantiationException {
        LinkedList<T> objets = new LinkedList<>();

        resultSet.beforeFirst();
        while (resultSet.next()) {
            T objet = classe.newInstance();
            objet.remplir(resultSet, connexion);

            objets.addLast(objet);
        }

        return objets;
    }

    // Méthodes abstraites
    /**
     * Remplit l'objet avec les champs de la requete
     *
     * @param resultSet resultat d'une recherche.
     * @param connexion connexion à la base de donnée (objets liés)
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    protected abstract void remplir(ResultSet resultSet, Connexion connexion) throws SQLException;

    /**
     * Enregistre dans la base de données les modifications appliquées à l'objet
     *
     * @param connexion connexion à la base données
     * @throws SQLException erreur de communication avec la base de données
     */
    public abstract void sauver(Connexion connexion) throws SQLException;

    /**
     * Supprime l'objet !
     *
     * @param connexion connexion à la base de données
     * @throws SQLException erreur de communication
     */
    public abstract void supprimer(Connexion connexion) throws SQLException;

    // Méthodes
    public boolean estModifie() {
        return modifie;
    }

    public boolean estSupprime() {
        return supprime;
    }
}
