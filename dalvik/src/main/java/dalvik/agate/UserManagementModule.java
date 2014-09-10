package dalvik.agate;


/**
 * Provides a User Management interface for the Dalvik VM.
 * 
 */
public class UserManagementModule {

    native public static String getUserName();
    native public static int getUserID();

    //return -1 on failure, 0 on success
    native public static int login(String user, String password);
    native public static int addUser(String user, String password);
    native public static int addGroup(String group);
    native public static int addUserToGroup(String user, String group);
}
