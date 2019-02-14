package com.victorely.cercle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Cercle {

    private static final Logger logger = LogManager.getLogger(Cercle.class.getName());

    public static void main(String[] args) {
        int lastEventId; //127973 ou 127960 128406
        try {
            lastEventId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            lastEventId = 127973; // Initialisation avec le dernier evenement fait à Paris la première semaine de février
        }
        int code = 0;
        int j = 0;
        // Boucle pour tester tous les comptes du Cercle sur Paylogic
        for (String string : getSaleId()) {
            // Boucle pour tester les évènements du dernier + 1 jusqu'à 600 en plus
            for (int i = lastEventId + 1; i <= lastEventId + 601; i++) {
                String cercleUrl = "https://frontoffice.paylogic.nl/?event_id=" + i + "&point_of_sale_id=" + string;
                URL url = null;
                HttpURLConnection connection;
                // Permet de récupérer le code retour des URL que l'on test
                try {
                    url = new URL(cercleUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    code = connection.getResponseCode();
                    connection.disconnect(); // On ferme la connexion pour limiter les ressources
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
                logger.info("Temps restant avant la fin du programme : " + (600 * getSaleId().size() - j));
                // Si l'on va ici, c'est gagné !
                if (code == 200) {
                    // Ouvrir le navigateur par défaut avec la page en question !
                    // TODO Faire en sorte que lorsque l'on définit l'heure d'ouverture de la billeterie, ça ouvre immédiatement la page en plus !
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        try {
                            Desktop.getDesktop().browse(new URI(url.toString()));
                        } catch (URISyntaxException | IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    logger.info(url);
                }
                else if (code == 429) {
                    logger.error("Trop de requêtes");
                }
                else {
                    logger.error("Code retour erreur : " + code);
                }
                // On applique ici une pause pour ne pas avoir le code retour HTTP 429
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }
                j++;
            }
        }
    }

    // List des ID du Cercle déjà existant
    private static List<String> getSaleId() {
        List<String> list = new ArrayList<>();
        list.add("17782");
        list.add("17783");
        list.add("16530");
        return list;
    }

}
