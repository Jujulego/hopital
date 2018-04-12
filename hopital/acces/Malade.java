package hopital.acces;

import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Malade {
    // Attributs
    private int numero;
    private String nom;
    private String prenom;
    private String tel;
    private String adresse;
    private String mutuelle;

    // Constructeur
    private Malade() {

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
                "select numero,nom,prenom,tel,adresse,mutuelle " +
                        "from malade " +
                        "where numero=?"
        );
        requete.setInt(1, numero);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet);
    }

    /**
     * Récupère un malade
     *
     * @param nom nom du malade
     * @param prenom prenom du malade
     * @param tel téléphone du malade
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Malade(String nom, String prenom, String tel, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select numero,nom,prenom,tel,adresse,mutuelle " +
                        "from malade " +
                        "where nom=? and prenom=? and tel=?"
        );
        requete.setString(1, nom);
        requete.setString(2, prenom);
        requete.setString(3, tel);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet);
    }

    // Méthodes statiques
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
                "select numero,nom,prenom,tel,adresse,mutuelle " +
                        "from malade"
        );

        // Construction du résultat
        return listeMalades(resultSet);
    }

    /**
     * Construit une liste des malades
     */
    public static LinkedList<Malade> listeMalades(ResultSet resultSet) throws SQLException {
        LinkedList<Malade> malades = new LinkedList<>();

        resultSet.beforeFirst();
        while (resultSet.next()) {
            Malade malade = new Malade();
            malade.remplir(resultSet);

            malades.addLast(malade);
        }

        return malades;
    }

    // Méthodes
    /**
     * Remplit l'objet avec les champs de la requete
     *
     * @param resultSet resultat d'une recherche.
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    private void remplir(ResultSet resultSet) throws SQLException {
        numero   = resultSet.getInt("numero");
        nom      = resultSet.getString("nom");
        prenom   = resultSet.getString("prenom");
        tel      = resultSet.getString("tel");
        adresse  = resultSet.getString("adresse");
        mutuelle = resultSet.getString("mutuelle");
    }

    @Override
    public String toString() {
        return String.format("<Malade n°%02d : %s %s, %s, %s>", numero, prenom, nom, adresse, tel, mutuelle);
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
                "select employe.numero,nom,prenom,adresse,tel,specialite " +
                        "from soigne " +
                            "inner join docteur on no_docteur=numero " +
                            "inner join employe on employe.numero = docteur.numero " +
                        "where no_malade=?"
        );
        requete.setInt(1, numero);

        // Construction du résultat
        return Docteur.listeDocteurs(requete.executeQuery());
    }

    // - accesseur
    public int getNumero() {
        return numero;
    }
    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public String getAdresse() {
        return adresse;
    }
    public String getTel() {
        return tel;
    }
    public String getMutuelle() {
        return mutuelle;
    }
}
