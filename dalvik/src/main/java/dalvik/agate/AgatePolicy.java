package dalvik.agate;

import java.nio.ByteBuffer;

/**
 * Provides an Object that securely keeps a policy.
 * 
 */
public final class AgatePolicy {
    // TODO: switch to int
    private byte[] tag = {0}; // tagged object, to avoid the possibility of changing the
                     //policy through reflectionreflection

    public int getPolicy() {
        return PolicyManagementModule.getPolicyByteArray(tag);
    }

    public void addPolicy(int t) {
        PolicyManagementModule.addPolicyByteArray(this.tag, t);
    }

    public void addPolicy(String[] user_readers, String[] group_readers) {
        PolicyManagementModule.addPolicyByteArray(this.tag, user_readers, group_readers);
    }

    public void addPolicy(int[] user_readers, int[] group_readers) {
        PolicyManagementModule.addPolicyByteArray(this.tag, user_readers, group_readers);
    }
}
