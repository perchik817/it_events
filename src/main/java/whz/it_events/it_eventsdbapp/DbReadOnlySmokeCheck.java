package whz.it_events.it_eventsdbapp;

import whz.it_events.it_eventsdbapp.config.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DbReadOnlySmokeCheck {
    private static final String DEFAULT_EMAIL = "max.mustermann@example.com";

    public static void main(String[] args) {
        String email = args.length > 0 && !args[0].startsWith("--") ? args[0] : DEFAULT_EMAIL;

        System.out.println("=== DB READ-ONLY SMOKE CHECK ===");
        System.out.println("No data will be changed.");
        System.out.println("User email: " + email);

        DbConnection dbConnection = new DbConnection();
        try {
            Connection connection = dbConnection.getConnection();
            UserRow user = findUserByEmail(connection, email);
            if (user == null) {
                System.out.println("User was not found.");
                return;
            }

            System.out.println();
            System.out.println("User: " + user.name + " " + user.lastName + " (id=" + user.id + ", role=" + user.role + ")");

            printAllEventsForHomePage(connection, user.id);
            printRegisteredEvents(connection, user.id);
            printSessions(connection);

            System.out.println();
            System.out.println("=== DB CHECK FINISHED ===");
        } catch (SQLException exception) {
            System.out.println("DB check failed: " + exception.getMessage());
            throw new RuntimeException(exception);
        } finally {
            dbConnection.close();
        }
    }

    private static UserRow findUserByEmail(Connection connection, String email) throws SQLException {
        String sql = """
                select id, name, last_name, email, role
                from [user]
                where email = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }
                return new UserRow(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("role")
                );
            }
        }
    }

    private static void printAllEventsForHomePage(Connection connection, long userId) throws SQLException {
        String sql = """
                select e.id,
                       e.name,
                       e.description,
                       e.start_date,
                       e.end_date,
                       e.status,
                       l.location_name,
                       l.stadt,
                       l.address,
                       case when exists (
                           select 1
                           from participant p
                           join track t on t.id = p.track_id
                           where p.user_id = ? and t.event_id = e.id
                       ) then 1 else 0 end as registered
                from event e
                left join location l on l.id = e.location_id
                order by e.start_date
                """;

        System.out.println();
        System.out.println("--- Home page: all events ---");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    long eventId = resultSet.getLong("id");
                    System.out.println("[" + eventId + "] " + resultSet.getString("name")
                            + " | " + resultSet.getString("status")
                            + " | " + format(resultSet.getTimestamp("start_date"))
                            + " - " + format(resultSet.getTimestamp("end_date"))
                            + " | " + resultSet.getString("location_name")
                            + ", " + resultSet.getString("stadt")
                            + " | registered=" + resultSet.getBoolean("registered"));
                    System.out.println("    " + resultSet.getString("description"));
                    System.out.println("    address: " + resultSet.getString("address"));
                    printTracksForEvent(connection, eventId, userId);
                }
            }
        }
    }

    private static void printTracksForEvent(Connection connection, long eventId, long userId) throws SQLException {
        String sql = """
                select t.id,
                       t.name,
                       t.description,
                       t.deadline_time,
                       count(p_all.id) as participants_count,
                       case when p_user.id is null then 0 else 1 end as registered
                from track t
                left join participant p_all on p_all.track_id = t.id
                left join participant p_user on p_user.track_id = t.id and p_user.user_id = ?
                where t.event_id = ?
                group by t.id, t.name, t.description, t.deadline_time, p_user.id
                order by t.id
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, eventId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("    track [" + resultSet.getLong("id") + "] "
                            + resultSet.getString("name")
                            + " | deadline=" + format(resultSet.getTimestamp("deadline_time"))
                            + " | participants=" + resultSet.getInt("participants_count")
                            + " | registered=" + resultSet.getBoolean("registered"));
                    System.out.println("        " + resultSet.getString("description"));
                }
            }
        }
    }

    private static void printRegisteredEvents(Connection connection, long userId) throws SQLException {
        String sql = """
                select distinct e.id,
                       e.name,
                       e.start_date,
                       e.end_date,
                       l.location_name,
                       l.stadt
                from participant p
                join track t on t.id = p.track_id
                join event e on e.id = t.event_id
                left join location l on l.id = e.location_id
                where p.user_id = ?
                order by e.start_date
                """;

        System.out.println();
        System.out.println("--- Registered events ---");
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                boolean hasRows = false;
                while (resultSet.next()) {
                    hasRows = true;
                    System.out.println("[" + resultSet.getLong("id") + "] "
                            + resultSet.getString("name")
                            + " | " + format(resultSet.getTimestamp("start_date"))
                            + " | " + resultSet.getString("location_name")
                            + ", " + resultSet.getString("stadt"));
                }
                if (!hasRows) {
                    System.out.println("No registered events.");
                }
            }
        }
    }

    private static void printSessions(Connection connection) throws SQLException {
        String sql = """
                select e.name as event_name,
                       s.titel,
                       s.start_date,
                       s.end_date,
                       s.room,
                       s.capacity,
                       s.session_type
                from session s
                join event e on e.id = s.event_id
                order by s.start_date
                """;

        System.out.println();
        System.out.println("--- Sessions overview ---");
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                System.out.println(resultSet.getString("event_name")
                        + " | " + resultSet.getString("titel")
                        + " | " + resultSet.getString("session_type")
                        + " | " + format(resultSet.getTimestamp("start_date"))
                        + " | room=" + resultSet.getString("room")
                        + " | capacity=" + resultSet.getInt("capacity"));
            }
        }
    }

    private static String format(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime().toString() : "-";
    }

    private record UserRow(long id, String name, String lastName, String email, String role) {
    }
}
