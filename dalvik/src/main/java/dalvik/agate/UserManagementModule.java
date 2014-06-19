package dalvik.agate;


/**
 * Provides a User Management interface for the Dalvik VM.
 * 
 */
public class UserManagementModule {

    // assume this is tainted(can't flow anywhere outside this address space)
    private static int userId = -1;

    public static int login(String user, String password) {
        userId = loginImpl(user, password);
        return userId;
    }

    public static int getUserId() {
        return userId;
    }

    native public static int loginImpl(String user, String password);
    native public static int adddGroup(String group);
    native public static int addUserToGroup(String user, String group);
}
