package dalvik.system;

import java.nio.ByteBuffer;

/**
 * Provides a Policy interface for the Dalvik VM. This class is used for
 * set/get policies on Sources and Sinks.
 * 
 */
public final class UserFlowPolicy {

    public static final int NO_POLICY         = 0x00000000;
    public static final int POLICY_1          = 0x00000001; // Only userId 1 can read
    public static final int POLICY_2          = 0x00000002; // Only userId 2 can read
    public static final int POLICY_3          = 0x00000004; // Only userId 3 can read
    public static final int POLICY_4          = 0x00000008; // Only userIds 1 and 2 can read
    public static final int POLICY_5          = 0x00000010; // Only userIds 1 and 3 can read
    public static final int POLICY_6          = 0x00000020; // Only userIds 2 and 3 can read


    /**
     * Checks if information can flow from a label to another.
     *
     * @param srcLabel
     *	    the label of the source data
     * @param destLabel
     *	    the label of the destination data
     * @return if data can flow
     *
     */
    native public static boolean canFlow(int srcLabel, int destLabel);

    /**
     * Updates the target String's policy tag.
     *
     * @param str
     *	    the target string
     * @param poid
     *	    policy Id to update (bitwise or) onto the object
     */
    native public static void addPolicyString(String str, int poid);

    /**
     * Updates the target Object array's policy tag.
     *
     * @param array
     *	    the target object array
     * @param poid
     *	    policy Id to update (bitwise or) onto the object array
     */
    native public static void addPolicyObjectArray(Object[] array, int poid);

    /**
     * Updates the target boolean array's policy tag.
     *
     * @param array
     *	    the target boolean array
     * @param poid
     *	    policy Id to update (bitwise or) onto the boolean array
     */
    native public static void addPolicyBooleanArray(boolean[] array, int poid);

    /**
     * Updates the target char array's policy tag.
     *
     * @param array
     *	    the target char array
     * @param poid
     *	    policy Id to update (bitwise or) onto the char array
     */
    native public static void addPolicyCharArray(char[] array, int poid);

    /**
     * Updates the target byte array's policy tag.
     *
     * @param array
     *	    the target byte array
     * @param poid
     *	    policy Id to update (bitwise or) onto the byte array
     */
    native public static void addPolicyByteArray(byte[] array, int poid);
    
    /**
     * Updates the target direct ByteBuffer's policy tag.
     *
     * @param dByteBuffer 
     *	    the target direct ByteBuffer
     * @param poid
     *      policy Id to update (bitwise or) onto the direct ByteBuffer
     */
    public static void addPolicyDirectByteBuffer(ByteBuffer dByteBuffer, int poid) {
        if (dByteBuffer.isDirect()) {
            dByteBuffer.addDirectByteBufferTaint(poid);
        }
    }

    /**
     * Updates the target int array's policy tag.
     *
     * @param array
     *	    the target int array
     * @param poid
     *	    policy Id to update (bitwise or) onto the int array
     */
    native public static void addPolicyIntArray(int[] array, int poid);
    
    /**
     * Updates the target short array's policy tag.
     *
     * @param array
     *	    the target short array
     * @param poid
     *	    policy Id to update (bitwise or) onto the int array
     */
    native public static void addPolicyShortArray(short[] array, int poid);

    /**
     * Updates the target long array's policy tag.
     *
     * @param array
     *	    the target long array
     * @param poid
     *	    policy Id to update (bitwise or) onto the long array
     */
    native public static void addPolicyLongArray(long[] array, int poid);

    /**
     * Updates the target float array's policy tag.
     *
     * @param array
     *	    the target float array
     * @param poid
     *	    policy Id to update (bitwise or) onto the float array
     */
    native public static void addPolicyFloatArray(float[] array, int poid);

    /**
     * Updates the target double array's policy tag.
     *
     * @param array
     *	    the target double array
     * @param poid
     *	    policy Id to update (bitwise or) onto the double array
     */
    native public static void addPolicyDoubleArray(double[] array, int poid);
    
    /**
     * Add policy to a primitive boolean value. Only the return value has the
     * updated policy tag.
     *
     * @param val
     *	    the input value
     * @param poid
     *	    policy Id to add (bitwise or) onto the input value
     * @return val with the added policy tag
     */
    native public static boolean addPolicyBoolean(boolean val, int poid);
    
    /**
     * Add policy to a primitive char value. Only the return value has the
     * updated policy tag.
     *
     * @param val
     *	    the input value
     * @param poid
     *	    policy Id to add (bitwise or) onto the input value
     * @return val with the added policy tag
     */
    native public static char addPolicyChar(char val, int poid);
    
    /**
     * Add policy to a primitive byte value. Only the return value has the
     * updated policy tag.
     *
     * @param val
     *	    the input value
     * @param poid
     *	    policy Id to add (bitwise or) onto the input value
     * @return val with the added policy tag
     */
    native public static byte addPolicyByte(byte val, int poid);

    /**
     * Add policy to a primitive int value. Only the return value has the
     * updated policy tag.
     *
     * @param val
     *	    the input value
     * @param poid
     *	    policy Id to add (bitwise or) onto the input value
     * @return val with the added policy tag
     */
    native public static int addPolicyInt(int val, int poid);
    
    /**
     * Add policy to a primitive short value. Only the return value has the
     * updated policy tag.
     *
     * @param val
     *	    the input value
     * @param poid
     *	    policyId to add (bitwise or) onto the input value
     * @return val with the added policy tag
     */
    native public static short addPolicyShort(short val, int poid);

    /**
     * Add policy to a primitive long value. Only the return value has the
     * updated policy tag.
     *
     * @param val
     *	    the input value
     * @param poid
     *	    policy Id to add (bitwise or) onto the input value
     * @return val with the added policy tag
     */
    native public static long addPolicyLong(long val, int poid);

    /**
     * Add policy to a primitive float value. Only the return value has the
     * updated policy tag.
     *
     * @param val
     *	    the input value
     * @param poid
     *	    policy Id to add (bitwise or) onto the input value
     * @return val with the added policy tag
     */
    native public static float addPolicyFloat(float val, int poid);

    /**
     * Add policy to a primitive double value. Only the return value has the
     * updated policy tag.
     *
     * @param val
     *	    the input value
     * @param poid
     *	    policy Id to add (bitwise or) onto the input value
     * @return val with the added policy tag
     */
    native public static double addPolicyDouble(double val, int poid);

    /**
     * Get the current policy tag from a String.
     *
     * @param str
     *	    the target String
     * @return the policy tag
     */
    native public static int getPolicyString(String str);

    /**
     * Get the current policy tag from an Object array.
     *
     * @param array 
     *	    the target Object array
     * @return the policy tag
     */
    native public static int getPolicyObjectArray(Object[] array);

    /**
     * Get the current policy tag from a boolean array.
     *
     * @param array 
     *	    the target boolean array
     * @return the policy tag
     */
    native public static int getPolicyBooleanArray(boolean[] array);

    /**
     * Get the current policy tag from a char array.
     *
     * @param array 
     *	    the target char array
     * @return the policy tag
     */
    native public static int getPolicyCharArray(char[] array);

    /**
     * Get the current policy tag from a byte array.
     *
     * @param array 
     *	    the target byte array
     * @return the policy tag
     */
    native public static int getPolicyByteArray(byte[] array);

    /**
     * Get the current policy tag from a direct ByteBuffer.
     *
     * @param dByteBuffer 
     *	    the target direct ByteBuffer
     * @return the policy tag
     */
    public static int getPolicyDirectByteBuffer(ByteBuffer dByteBuffer) {
        if (dByteBuffer.isDirect()) {
            return dByteBuffer.getDirectByteBufferTaint();
        } else {
            return -1;
        }
    }

    /**
     * Get the current policy tag from an int array.
     *
     * @param array
     *	    the target int array
     * @return the policy tag
     */
    native public static int getPolicyIntArray(int[] array);

    /**
     * Get the current policy tag from a short array.
     *
     * @param array 
     *	    the target short array
     * @return the policy tag
     */
    native public static int getPolicyShortArray(short[] array);

    /**
     * Get the current policy tag from a long array.
     *
     * @param array 
     *	    the target long array
     * @return the policy tag
     */
    native public static int getPolicyLongArray(long[] array);

    /**
     * Get the current policy tag from a float array.
     *
     * @param array 
     *	    the target float array
     * @return the policy tag
     */
    native public static int getPolicyFloatArray(float[] array);

    /**
     * Get the current policy tag from a double array.
     *
     * @param array 
     *	    the target double array
     * @return the policy tag
     */
    native public static int getPolicyDoubleArray(double[] array);

    /**
     * Get the current policy tag from a primitive boolean.
     *
     * @param val
     *	    the target boolean
     * @return the policy tag
     */
    native public static int getPolicyBoolean(boolean val);

    /**
     * Get the current policy tag from a primitive char.
     *
     * @param val
     *	    the target char 
     * @return the policy tag
     */
    native public static int getPolicyChar(char val);

    /**
     * Get the current taint tag from a primitive byte.
     *
     * @param val
     *	    the target byte 
     * @return the policy tag
     */
    native public static int getPolicyByte(byte val);

    /**
     * Get the current policy tag from a primitive int.
     *
     * @param val
     *	    the target int 
     * @return the policy tag
     */
    native public static int getPolicyInt(int val);
    
    /**
     * Get the current policy tag from a primitive short.
     *
     * @param val
     *	    the target short 
     * @return the policy tag
     */
    native public static int getPolicyShort(short val);

    /**
     * Get the current policy tag from a primitive long.
     *
     * @param val
     *	    the target long 
     * @return the policy tag
     */
    native public static int getPolicyLong(long val);

    /**
     * Get the current policy tag from a primitive float.
     *
     * @param val
     *	    the target float 
     * @return the policy tag
     */
    native public static int getPolicyFloat(float val);

    /**
     * Get the current policy tag from a primitive double.
     *
     * @param val
     *	    the target double 
     * @return the policy tag
     */
    native public static int getPolicyDouble(double val);

    /**
     * Get the current policy tag from an Object reference.
     *
     * @param obj
     *	    the target Object reference
     * @return the policy tag
     */
    native public static int getPolicyRef(Object obj);
    
    /**
     * Get the policy tag from a file identified by a descriptor.
     *
     * @param fd
     *	    the target file descriptor
     * @return the policy tag
     */
    native public static int getPolicyFile(int fd);
    
    /**
     * add a policy tag to a file identified by a descriptor
     *
     * @param fd
     *	    the target file descriptor
     * @param poid
     *	    the policy Id to add (bitwise or) to the file
     */
    native public static void addPolicyFile(int fd, int tag);

    /**
     * Logging utility accessible from places android.util.Log
     * is not.
     *
     * @param msg
     *	    the message to log
     */
    native public static void log(String msg);


    /**
     * Logging utility to obtain the file path for a file descriptor
     *
     * @param fd
     *	    the file descriptor
     */
    native public static void logPathFromFd(int fd);

    /**
     * Logging utility to obtain the peer IP addr for a file descriptor
     *
     * @param fd
     *	    the file descriptor
     */
    native public static void logPeerFromFd(int fd);
}
