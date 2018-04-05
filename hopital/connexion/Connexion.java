package hopital.connexion;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.sql.*;
import java.util.ArrayList;

/**
 * 
 * Connexion a votre BDD locale ou à distance sur le serveur de l'ECE via le tunnel SSH
 * 
 * @author segado julien
 */
public class Connexion {
    // Attributs
    private Connection conn; // connexion à la base
    private SSHTunnel sshTunnel = null;

    /**
     * Connexion à la base de données locale
     *
     * @param nameDatabase nom de la base locale
     * @param loginDatabase identifiant de connexion au serveur de base de données
     * @param passwordDatabase mot de passe associé
     *
     * @throws java.lang.ClassNotFoundException en cas d'absence du driver
     */
    public Connexion(String nameDatabase, String loginDatabase, String passwordDatabase) throws SQLException, ClassNotFoundException {
        // Chargement driver "com.mysql.jdbc.Driver"
        Class.forName("com.mysql.jdbc.Driver");

        // Connexion à la base de données
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost/" + nameDatabase,
                loginDatabase, passwordDatabase
        );
    }

    /**
     * Connexion à la base de données distante sur le serveur de l'ECE
     *
     * @param usernameECE identifiant de connexion à l'ECE
     * @param passwordECE mot de passe ECE
     * @param loginDatabase identifiant de connexion au serveur de base de données
     * @param passwordDatabase mot de passe base de données
     *
     * @throws SQLException erreur de connexion
     * @throws JSchException erreur de connexion SSH
     * @throws ClassNotFoundException en cas d'absence du driver
     */
    public Connexion(String usernameECE, String passwordECE, String loginDatabase, String passwordDatabase) throws SQLException, JSchException, ClassNotFoundException {
        // chargement driver "com.mysql.jdbc.Driver"
        Class.forName("com.mysql.jdbc.Driver");

        // Connexion via le tunnel SSH avec le username et le password ECE
        sshTunnel = new SSHTunnel(usernameECE, passwordECE);
        sshTunnel.connect();

        //création d'une connexion JDBC à la base
        conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3305/" + usernameECE,
                loginDatabase, passwordDatabase
        );
    }

    /**
     * Retourne la liste des champs de la table en paramètre
     *
     * @param table nom de la table à étudier
     * @return liste des champs
     *
     * @throws java.sql.SQLException erreur d'accès à la base
     */
    public ArrayList<String> recupChampsTable(String table) throws SQLException {
        // Execution de la requete
        ResultSet rset = execSelect("select * from " + table);
        ResultSetMetaData rsetMeta = rset.getMetaData(); // extraction des noms de champs

        // creation d'une ArrayList de String
        ArrayList<String> liste = new ArrayList<>();

        // Ajouter tous les champs du resultat dans l'ArrayList
        for (int i = 0; i < rsetMeta.getColumnCount(); i++) {
            liste.add(rsetMeta.getColumnLabel(i));
        }

        // Retourner l'ArrayList
        return liste;
    }

    /**
     * Execution d'une requete SQL (select) sans paramètres
     *
     * @throws java.sql.SQLException erreur d'accès à la base
     */
    public ResultSet execSelect(String sql) throws SQLException {
        return conn.createStatement().executeQuery(sql);
    }

    /**
     * Execution d'une requete SQL (insert, delete, drop ...) sans paramètres
     *
     * @param sql requete SQL
     * @return le nombre de lignes affectées
     *
     * @throws java.sql.SQLException erreur d'accès à la base
     */
    public int execModif(String sql) throws SQLException {
        return conn.createStatement().executeUpdate(sql);
    }

    /**
     * Préparation d'une requete SQL à paramètres
     *
     * @param sql requete SQL
     *
     * @throws java.sql.SQLException erreur d'accès à la base
     */
    public PreparedStatement prepRequete(String sql) throws SQLException {
        return conn.prepareStatement(sql);
    }

    /**
     * Déconnexion du serveur
     *
     * @throws java.sql.SQLException erreur d'accès à la base
     */
    public void deconnecter() throws SQLException, JSchException {
        // Deconnexion de la base
        conn.close();

        // Deconnexion SSH
        if (sshTunnel != null) {
            sshTunnel.deconnecter();
        }
    }
}
