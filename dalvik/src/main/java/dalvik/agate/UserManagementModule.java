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

    public static int addGroup(String groupName) {
	if(userId < 0)
	    return -1;
	return addGroup(groupName, userId);
    }

    public static int addUserToGroup(String user, String group) {
	if(userId < 0)
	    return -1;
	return addUserToGroup(user, group, userId);
    }

    native private static int loginImpl(String user, String password);
    //return -1 on failure, 0 on success
    native public static int addUser(String user, String password);
    native private static int addGroup(String group, int ownerId);
    native private static int addUserToGroup(String user, String group, int userId);
}
