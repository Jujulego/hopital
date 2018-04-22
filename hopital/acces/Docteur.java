package hopital.acces;

import hopital.connexion.Connexion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

public class Docteur extends Employe {
    // Attributs
    private String specialite;

    // Constructeur
    protected Docteur() {
    }

    /**
     * Récupère un docteur à partir de son numero
     *
     * @param numero numero du docteur
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Docteur(int numero, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select employe.numero,nom,prenom,tel,adresse,specialite " +
                        "from employe inner join docteur on employe.numero = docteur.numero " +
                        "where docteur.numero=?"
        );
        requete.setInt(1, numero);

        // Remplissage
        ResultSet resultSet = requete.executeQuery();
        resultSet.first();

        remplir(resultSet, null);
    }

    /**
     * Récupère un docteur
     *
     * @param nom nom du docteur
     * @param prenom prenom du docteur
     * @param tel téléphone du docteur
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public Docteur(String nom, String prenom, String tel, Connexion connexion) throws SQLException {
        // Preparation de la requete
        PreparedStatement requete = connexion.prepRequete(
                "select employe.numero,nom,prenom,tel,adresse,specialite " +
                        "from employe inner join docteur on employe.numero = docteur.numero " +
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
    public static Docteur creerDocteur(int numero, String nom, String prenom, String adresse, String telephone, String specialite, Connexion connexion) throws SQLException {
        // Requête
        PreparedStatement requete = connexion.prepRequete("insert into employe values (?, ?, ?, ?, ?)");
        requete.setInt(1, numero);
        requete.setString(2, nom);
        requete.setString(3, prenom);
        requete.setString(4, adresse);
        requete.setString(5, telephone);

        requete.execute();

        requete = connexion.prepRequete("insert into docteur values (?, ?)");
        requete.setInt(1, numero);
        requete.setString(2, specialite);

        requete.execute();

        // Création de l'objet
        Docteur docteur = new Docteur();
        docteur.numero = numero;
        docteur.nom = nom;
        docteur.prenom = prenom;
        docteur.adresse = adresse;
        docteur.telephone = telephone;
        docteur.specialite = specialite;

        return docteur;
    }

    /**
     * Récupère tous les docteurs !
     *
     * @param connexion connexionECE à la base de donnée
     *
     * @throws SQLException erreur de communication avec la base de données
     */
    public static LinkedList<Docteur> tousDocteurs(Connexion connexion) throws SQLException {
        // Requête
        ResultSet resultSet = connexion.execSelect(
                "select employe.numero,nom,prenom,tel,adresse,specialite " +
                        "from employe inner join docteur on employe.numero = docteur.numero "
        );

        return listeDocteurs(resultSet, connexion);
    }

    /**
     * Construit une liste de docteurs, basée sur la liste donnée
     */
    public static LinkedList<Docteur> listeDocteurs(ResultSet resultSet, Connexion connexion) throws SQLException {
        try {
            return listeObjets(Docteur.class, resultSet, connexion);
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

        // Spécialité
        specialite = resultSet.getString("specialite");
    }

    @Override
    public String toString() {
        return String.format("<Docteur n°%02d (%s): %s %s, %s, %s>", getNumero(), specialite, getPrenom(), getNom(), getAdresse(), getTelephone());
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
                        "set specialite=? " +
                        "where numero = ?"
        );

        requete.setString(1, specialite);
        requete.setInt(2, numero);

        requete.execute();
    }

    @Override
    public void supprimer(Connexion connexion) throws SQLException {
        // Gardien
        if (!supprime) return;

        // Requete
        PreparedStatement requete = connexion.prepRequete(
                "delete from docteur " +
                        "where numero = ?"
        );

        requete.setInt(1, numero);

        requete.execute();

        // suppression de l'employé
        super.supprimer(connexion);
    }

    /**
     * Renvoie la liste des patients du docteur
     *
     * @param connexion connexionECE à la base de donnée
     * @return liste des patients
     *
     * @throws SQLException erreur sur la requête
     */
    public LinkedList<Malade> getPatients(Connexion connexion) throws SQLException {
        // Requete
        PreparedStatement requete = connexion.prepRequete(
                "select numero,nom,prenom,adresse,tel,mutuelle "+
                        "from soigne " +
                            "inner join malade on no_malade=numero " +
                        "where no_docteur=?"
        );
        requete.setInt(1, numero);

        // Construction du résultat
        return Malade.listeMalades(requete.executeQuery(), connexion);
    }

    public void ajouterPatient(Malade malade, Connexion connexion) throws SQLException {
        // Ajout de la laison :
        PreparedStatement requete = connexion.prepRequete(
                "insert into soigne values (?, ?)"
        );

        requete.setInt(1, numero);
        requete.setInt(2, malade.getNumero());

        requete.execute();
    }

    public void enleverPatient(Malade malade, Connexion connexion) throws SQLException {
        // Suppression de la liaison
        PreparedStatement requete = connexion.prepRequete(
                "delete from soigne " +
                        "where no_docteur = ? and no_malade = ?"
        );

        requete.setInt(1, numero);
        requete.setInt(2, malade.getNumero());

        requete.execute();
    }

    // - accesseur
    public String getSpecialite() {
        return specialite;
    }
    public void setSpecialite(String specialite) {
        this.specialite = specialite;
        modifie = true;
    }
}
