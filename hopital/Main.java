package hopital;

import com.jcraft.jsch.JSchException;
import hopital.connexion.Connexion;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    // Attributs
    private static Scanner scanner = new Scanner(System.in);

    // Main !!!
    public static void main(String[] args) {
        try {
            /*Connexion connexion = new Connexion("hopital",
                    "hopital", "pm1caalceymgpv0vm7lprg8ipfknux57"
            );*/
            System.out.print("UserECE : ");
            String user = scanner.nextLine();
            System.out.print("Password : ");
            String psw = scanner.nextLine();
            Connexion connexion = new Connexion(
                    user, psw,
                    "jc151870-rw", "YDRyIxgl"
            );

            connexion.execSelect("select * from docteur;");

            connexion.deconnecter();

        } catch (JSchException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
