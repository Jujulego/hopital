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
public class Employe {
    // Attributs
    protected int numero;
    private String nom;
    private String prenom;
    private String tel;
    private String adresse;

    // Constructeurs
    protected Employe() {
    }

    /**
     * Récupère un employé à partir de son numero
     *
     * @param numero numero de l'employé
     * @param connexion connexion à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Employe(int numero, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select numero,nom,prenom,tel,adresse " +
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
     * @param tel téléphone de l'employé
     * @param connexion connexion à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Employe(String nom, String prenom, String tel, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select numero,nom,prenom,tel,adresse " +
                        "from employe " +
                        "where nom=? and prenom=? and tel=?"
        );
        requete.setString(1, nom);
        requete.setString(2, prenom);
        requete.setString(3, tel);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, null);
    }

    // Méthodes statiques
    /**
     * Récupère tous les employés !
     *
     * @param connexion connexion à la base de donnée
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
     * @param connexion connexion à la base de donnée (objets liés)
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    protected void remplir(ResultSet resultSet, Connexion connexion) throws SQLException {
        numero  = resultSet.getInt("numero");
        nom     = resultSet.getString("nom");
        prenom  = resultSet.getString("prenom");
        tel     = resultSet.getString("tel");
        adresse = resultSet.getString("adresse");
    }

    @Override
    public String toString() {
        return String.format("<Employé n°%02d : %s %s, %s, %s>", numero, prenom, nom, adresse, tel);
    }

    // - accesseurs
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
}
