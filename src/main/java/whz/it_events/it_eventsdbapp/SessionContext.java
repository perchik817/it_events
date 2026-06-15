package whz.it_events.it_eventsdbapp;

import whz.it_events.it_eventsdbapp.model.User;
import whz.it_events.it_eventsdbapp.model.enums.Role;

/**
 * Global singleton that holds the currently logged-in user.
 * Set once after successful login, read by all controllers.
 */
public class SessionContext {

    private static User currentUser;

    private SessionContext() {}

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Role getRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }

    public static boolean isAdmin() {
        return Role.ADMIN.equals(getRole());
    }

    public static boolean isJury() {
        return Role.JURY.equals(getRole());
    }

    public static boolean isUser() {
        return Role.USER.equals(getRole());
    }

    /** Admin can do everything. ReadWriter (Jury) can insert/edit but not delete. User can only read. */
    public static boolean canWrite() {
        return isAdmin() || isJury();
    }

    public static boolean canDelete() {
        return isAdmin();
    }

    public static void clear() {
        currentUser = null;
    }
}
