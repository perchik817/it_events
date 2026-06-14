package whz.it_events.it_eventsdbapp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
//        if (args.length > 0 && "--smoke".equals(args[0])) {
//            ServiceSmokeCheck.main(args);
//            return;
//        }
        try {
            EntityManagerFactory emf =
                    Persistence.createEntityManagerFactory("itEventsPU");

            EntityManager em = emf.createEntityManager();

            System.out.println("Connection successful!");

            em.close();
            emf.close();

        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        }
        Application.launch(MainApp.class, args);
    }
}
