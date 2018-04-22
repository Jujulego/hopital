package hopital.graphismes;

import hopital.connexion.Connexion;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Statistiques extends JFrame {
    // Attributs
    Connexion connexion;

    ChartPanel graphique = null;
    JButton mutuelles = new JButton("Mutuelles");
    JButton repartPatients = new JButton("Répartitions Patients");
    JButton infirmiersServices = new JButton("Infirmiers / services");

    JButton retour = new JButton("Retour");

    // Constructeur
    public Statistiques(Connexion connexion) {
        // Attributs
        this.connexion = connexion;

        // Paramètres
        setVisible(true);
        setSize(1080,760);
        setMinimumSize(new Dimension(1080, 760));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Mise en place
        JPanel boutons = new JPanel();
        boutons.setLayout(new GridLayout(3, 1, 8, 8));
        boutons.add(mutuelles);
        boutons.add(repartPatients);
        boutons.add(infirmiersServices);

        JPanel boutons2 = new JPanel();
        boutons2.add(retour);

        setLayout(new BorderLayout());
        add(boutons, BorderLayout.WEST);
        add(boutons2, BorderLayout.SOUTH);

        // Listeners
        mutuelles.addActionListener(this::graphMutuelles);
        repartPatients.addActionListener(this::graphRepartPatients);
        infirmiersServices.addActionListener(this::graphInfirServ);

        retour.addActionListener((ActionEvent event) -> {
            new Fenetre(connexion);
            this.dispose();
        });
    }

    // Méthodes
    private void setGraphique(JFreeChart chart) {
        if (graphique == null) {
            // Création du panel
            graphique = new ChartPanel(chart, true);
            add(graphique, BorderLayout.CENTER);
        } else {
            // Remplacement du graphique
            graphique.setChart(chart);
        }

        // Rafraichissement
        validate();
        repaint();
    }

    private void graphMutuelles(ActionEvent event) {
        try {
            // Requête
            ResultSet resultSet = connexion.execSelect(
                    "select mutuelle,count(numero) " +
                            "from malade " +
                            "group by mutuelle"
            );

            // Récupération des déonnées
            DefaultPieDataset dataset = new DefaultPieDataset();

            resultSet.beforeFirst();
            while (resultSet.next()) {
                dataset.setValue(
                        resultSet.getString(1),
                        resultSet.getInt(2)
                );
            }

            // Création du graphique
            setGraphique(ChartFactory.createPieChart(
                    "Mutuelles", dataset
            ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void graphRepartPatients(ActionEvent event) {
        try {
            // Requête
            ResultSet resultSet = connexion.execSelect(
                    "select nom,count(no_malade) " +
                            "from hospitalisation left join service on hospitalisation.code_service like service.code " +
                            "group by nom"
            );

            // Récupération des déonnées
            DefaultPieDataset dataset = new DefaultPieDataset();

            resultSet.beforeFirst();
            while (resultSet.next()) {
                dataset.setValue(
                        resultSet.getString(1),
                        resultSet.getInt(2)
                );
            }

            // Création du graphique
            setGraphique(ChartFactory.createPieChart(
                    "Répartions patients", dataset
            ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void graphInfirServ(ActionEvent event) {
        try {
            // Requête
            ResultSet resultSet = connexion.execSelect(
                    "select nom,rotation,count(numero) " +
                            "from infirmier left join service on infirmier.code_service like service.code " +
                            "group by nom, rotation"
            );

            // Récupération des données
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();

            resultSet.beforeFirst();
            while (resultSet.next()) {
                dataset.setValue(
                        resultSet.getInt(3),
                        resultSet.getString(2),
                        resultSet.getString(1)
                );
            }

            // Création du graphique
            setGraphique(ChartFactory.createBarChart(
                    "Infirmiers par Services", "Services", "Infirmiers", dataset
            ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
