package hopital;

import com.jcraft.jsch.JSchException;
import hopital.acces.*;
import hopital.connexion.Connexion;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    // Attributs
    private static Scanner scanner = new Scanner(System.in);

    // Main !!!
    public static void main(String[] args) {
        try {
            Connexion connexion = new Connexion("hopital",
                    "hopital", "pm1caalceymgpv0vm7lprg8ipfknux57"
            );

            /*ResultSet set = connexion.execSelect(
                    "select code_service,no_chambre,surveillant,nb_lits " +
                            "from chambre " +
                            "where code_service='CAR'"
            );
            afficher(set);*/

            for (Chambre chambre : new Service("CAR", connexion).getChambres(connexion)) {
                System.out.println(chambre);
            }

            connexion.deconnecter();

        } catch (JSchException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche un resultset
     *
     * @param resultSet resultset à afficher
     * @throws SQLException
     */
    public static void afficher(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int max[] = new int[metaData.getColumnCount()];

        // Init max
        for (int i = 0; i < max.length; ++i) {
            max[i] = metaData.getColumnLabel(i+1).length();
        }

        // Récupération des valeurs
        resultSet.beforeFirst();
        while (resultSet.next()) {
            // lecture
            for (int i = 0; i < metaData.getColumnCount(); ++i) {
                String str = resultSet.getString(i+1);

                if (str != null && str.length() > max[i]) {
                    max[i] = str.length();
                }
            }
        }

        // Génération d'une ligne
        StringBuilder ligne = new StringBuilder();
        for (int i = 0; i < max.length; ++i) {
            ligne.append("+");

            max[i] = max[i] + 2;
            for (int j = 0; j < max[i]; ++j) {
                ligne.append("-");
            }
        }

        ligne.append("+");

        // Entête
        System.out.println(ligne);
        for (int i = 0; i < max.length; ++i) {
            System.out.print('|');
            System.out.print(String.format("%" + (max[i]+metaData.getColumnLabel(i+1).length())/2 + "s", metaData.getColumnLabel(i+1)));

            for (int j = 0; j < (max[i]-metaData.getColumnLabel(i+1).length()+1)/2; ++j) {
                System.out.print(' ');
            }
        }

        System.out.println('|');
        System.out.println(ligne);

        resultSet.beforeFirst();
        while (resultSet.next()) {
            for (int i = 0; i < max.length; ++i) {
                System.out.print('|');
                System.out.print(String.format("%" + (max[i]-1) + "s ", resultSet.getString(i+1)));
            }

            System.out.print('|');
            System.out.println();
        }
        System.out.println(ligne);
    }
}
