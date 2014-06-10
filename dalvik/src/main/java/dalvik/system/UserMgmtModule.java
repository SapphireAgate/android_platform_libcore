package dalvik.system;


/**
 * Provides a User Management interface for the Dalvik VM.
 * 
 */
public class UserMgmtModule {
	native public static int login(String user, String password);
	native public static int adddGroup(String group);
	native public static int addUserToGroup(String user, String group);
}
