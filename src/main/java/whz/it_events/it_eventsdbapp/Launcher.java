package whz.it_events.it_eventsdbapp;

import javafx.application.Application;

public class Launcher {
    public static void main(String[] args) {
        if (args.length > 0 && "--smoke".equals(args[0])) {
            ServiceSmokeCheck.main(args);
            return;
        }
        if (args.length > 0 && "--db-smoke".equals(args[0])) {
            String[] smokeArgs = args.length > 1 ? new String[]{args[1]} : new String[0];
            DbReadOnlySmokeCheck.main(smokeArgs);
            return;
        }
        Application.launch(HelloApplication.class, args);
    }
}
