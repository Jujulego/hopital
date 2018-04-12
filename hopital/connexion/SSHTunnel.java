package hopital.connexion;

import com.jcraft.jsch.*;

/**
 * Etablit une connexionECE au serveur de la base de données via un tunnel SSH
 *
 * Date: 23/03/2014 Time: 13:30
 *
 * @author pieraggi
 */
public class SSHTunnel {
    // Attributs
    private String tunnelHost = "gandalf.ece.fr";
    private String host = "sql-users.ece.fr";
    private int tunnelHostPort = 22;
    private int hostPort = 3305;
    private String username;
    private String password;

    private Session session;

    /* ************************
     *       Constructors     *
     ************************ */
    /**
     * Constructeur permettant la connexionECE à un serveur via un double tunnel
     * SSH
     *
     * @param username Nom d'utilisateur ECE
     * @param password Mot de passe ECE
     * @param tunnelHost Hote porteur du tunnel
     * @param host Second hôte avec lequel il faut établir un tunnel SSH
     * @param tunnelHostPort Port utiliser pour se connecter au premier hôte
     * @param hostPort Port utiliser pour se connecter au second hôte
     */
    public SSHTunnel(String username, String password, String tunnelHost, String host, int tunnelHostPort, int hostPort) {
        this.username = username;
        this.password = password;

        setTunnelHost(tunnelHost);
        setTunnelHostPort(tunnelHostPort);

        setHost(host);
        setHostPort(hostPort);
    }

    /**
     * Constructeur permettant la connexionECE automatique au serveur de la base de
     * données ECE
     *
     * @param username Nom d'utilisateur ECE
     * @param password Mot de passe ECE
     */
    public SSHTunnel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /* ************************
     *         Methods        *
     ************************ */
    /**
     * Tente de se connecter au serveur
     *
     * @return la session de connexionECE
     *
     * @throws JSchException en cas d'erreur de connexionECE
     */
    @SuppressWarnings("CallToThreadDumpStack")
    public Session connect() throws JSchException {
        // Initialise la connexionECE
        JSch jsch = new JSch();
        session = jsch.getSession(this.getUsername(), this.getTunnelHost(), this.getTunnelHostPort());

        // Automatiser la connexionECE (ne pas afficher d'interface pour rentrer les mots de passe)
        session.setUserInfo(new SilentUserInfo(this.password));

        // Etablissement du premier tunnel SSH
        session.connect();

        // Etablissement du second tunnel SSH (port forwarding with option -L)
        session.setPortForwardingL(this.getHostPort(), this.getHost(), this.getHostPort());

        return session;
    }

    /**
     * Déconnexion !
     */
    public void deconnecter() throws JSchException {
        session.setPortForwardingL(this.getHostPort(), this.getHost(), this.getHostPort());
        session.disconnect();
    }

    /* ************************
     *    Getters & Setters   *
     ************************ */
    public String getTunnelHost() {
        return tunnelHost;
    }

    private void setTunnelHost(String tunnelHost) {
        this.tunnelHost = tunnelHost;
    }

    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    public int getTunnelHostPort() {
        return tunnelHostPort;
    }

    private void setTunnelHostPort(int tunnelHostPort) {
        this.tunnelHostPort = tunnelHostPort;
    }

    public int getHostPort() {
        return hostPort;
    }

    private void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public String getUsername() {
        return username;
    }

    /* ************************
     *      Private class     *
     ************************ */
    /**
     * Classe gérant l'interaction de l'utilisateur lors de la connexionECE. Elle
     * automatise la connexionECE en fournissant les informations de connexionECE sans
     * les demander à l'utilisateur
     */
    static class SilentUserInfo implements UserInfo, UIKeyboardInteractive {

        private final String password;

        public SilentUserInfo(String password) {
            this.password = password;
        }

        @Override
        public String getPassword() {
            return this.password;
        }

        @Override
        public boolean promptYesNo(String str) {
            return true;
        }

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return true;
        }

        @Override
        public boolean promptPassword(String message) {
            return true;
        }

        @Override
        public void showMessage(String message) {
        }

        @Override
        public String[] promptKeyboardInteractive(String destination,
                String name,
                String instruction,
                String[] prompt,
                boolean[] echo) {
            return null;
        }
    }
}
