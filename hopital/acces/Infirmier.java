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
        LinkedList<Infirmier> infirmiers = new LinkedList<>();

        resultSet.beforeFirst();
        while (resultSet.next()) {
            Infirmier infirmier = new Infirmier();
            infirmier.remplir(resultSet, connexion);

            infirmiers.addLast(infirmier);
        }

        return infirmiers;
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
            return String.format("Infirmier n°%02d (%s): %s %s, %s, %s, %s€", getNumero(), rotation, getPrenom(), getNom(), getAdresse(), getTel(), salaire);
        }

        return String.format("Infirmier n°%02d (%s %s): %s %s, %s, %s, %s€", getNumero(), rotation, service.getNom(), getPrenom(), getNom(), getAdresse(), getTel(), salaire);
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
    public String getRotation() {
        return rotation;
    }
    public int getSalaire() {
        return salaire;
    }
}
