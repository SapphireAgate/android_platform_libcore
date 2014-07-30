/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package libcore.io;

import java.io.FileDescriptor;
import java.util.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.NioUtils;
import libcore.util.MutableInt;
import libcore.util.MutableLong;

// begin WITH_TAINT_TRACKING
import dalvik.system.Taint;
import dalvik.agate.UserManagementModule;
import dalvik.agate.PolicyManagementModule;
// end WITH_TAINT_TRACKING
// end WITH_TAINT_TRACKING

public final class Posix implements Os {
    Posix() { }
	private static class SocketTaint {
		public SocketTaint(int taint, int byteCount) {
			this.taint = taint;
			this.byteCount = byteCount;
		}

		int taint;
		int byteCount;
	}
	private static final Map<Integer, SocketTaint> incomingSocketTaint = new HashMap<Integer, SocketTaint>();

// begin WITH_SAPPHIRE_AGATE
//   private byte[] toByteArray(int value) {
//        byte[] bytes = new byte[4];
//        for (int i = 0; i < 4; i++) {
//            bytes[i] = (byte)((value >>> ((3 - i) * 8)) & 0xff);
//        }
//        return bytes; 
//   }

//   private int fromByteArray(byte[] bytes) {
//        int value = 0;
//        for (int i = 0; i < 4; i++) {
//            value = value << 8;
//            value |= bytes[i] & 0xff;
//        }
//        return value;
//   }
// end WITH_SAPPHIRE_AGATE

// begin WITH_SAPPHIRE_AGATE
    public native FileDescriptor accept(FileDescriptor fd, InetSocketAddress peerAddress) throws ErrnoException, SocketException;
//    public native FileDescriptor acceptImpl(FileDescriptor fd, InetSocketAddress peerAddress) throws ErrnoException, SocketException;
//    public FileDescriptor accept(FileDescriptor fd, InetSocketAddress peerAddress) throws ErrnoException, SocketException {
//        byte[] buf = new byte[4];
//        FileDescriptor fs = acceptImpl(fd, peerAddress);
//
//        // First, get the ID from the other trusted runtime
//        int r = recvfrom(fs, buf, 0, 4, 0, null);
//        int id = fromByteArray(buf);
//
//        Taint.log("[accept] userId = " + id);
//        int fsInt = fs.getDescriptor();
//        PolicyManagementModule.logPathFromFd(fsInt);
//
//        // Taint the filedescriptor
//        // TODO: This is hard-coded for now
//        String[] readers = {"u1"};
//        PolicyManagementModule.addPolicySocket(fsInt, readers, null);
//
//        int fdInt = fs.getDescriptor();  
//        Taint.log("[accept] set policy on file descriptor = 0x" + Integer.toHexString(PolicyManagementModule.getPolicySocket(fdInt)));
//
//        return fs;
//    }
// end WITH_SAPPHIRE_AGATE

    public native boolean access(String path, int mode) throws ErrnoException;
    public native void bind(FileDescriptor fd, InetAddress address, int port) throws ErrnoException, SocketException;
    public native void chmod(String path, int mode) throws ErrnoException;
    public native void chown(String path, int uid, int gid) throws ErrnoException;
    public native void close(FileDescriptor fd) throws ErrnoException;
// begin WITH_TAINT_TRACKING
    //public native void connect(FileDescriptor fd, InetAddress address, int port) throws ErrnoException, SocketException;
    public native void connectImpl(FileDescriptor fd, InetAddress address, int port) throws ErrnoException, SocketException;
    public void connect(FileDescriptor fd, InetAddress address, int port) throws ErrnoException, SocketException {
        String addr = address.getHostAddress();
        if (addr != null) {
             fd.hasName = true;
             fd.name = addr;
    	}
        connectImpl(fd, address, port);
//begin WITH_SAPPHIRE_AGATE
//
//        /* Handshake to exchange certificates */
//        // First, send the ID of this runtime system, (signed by the UMS)
//        int userId = UserManagementModule.getUserId(); 
//        String username = UserManagementModule.getUserName(); 
//	if(username == null)
//		throw new SocketException("failed to contact database");
//
//        // Put user id in the message
//        byte[] buf = username.getBytes();
//
//        int r = sendto(fd, buf, 0, 4, 0, address, port);
//end WITH_SAPPHIRE_AGATE
    }
// end WITH_TAINT_TRACKING
    public native FileDescriptor dup(FileDescriptor oldFd) throws ErrnoException;
    public native FileDescriptor dup2(FileDescriptor oldFd, int newFd) throws ErrnoException;
    public native String[] environ();
    public native void execv(String filename, String[] argv) throws ErrnoException;
    public native void execve(String filename, String[] argv, String[] envp) throws ErrnoException;
    public native void fchmod(FileDescriptor fd, int mode) throws ErrnoException;
    public native void fchown(FileDescriptor fd, int uid, int gid) throws ErrnoException;
    public native int fcntlVoid(FileDescriptor fd, int cmd) throws ErrnoException;
    public native int fcntlLong(FileDescriptor fd, int cmd, long arg) throws ErrnoException;
    public native int fcntlFlock(FileDescriptor fd, int cmd, StructFlock arg) throws ErrnoException;
    public native void fdatasync(FileDescriptor fd) throws ErrnoException;
    public native StructStat fstat(FileDescriptor fd) throws ErrnoException;
    public native StructStatFs fstatfs(FileDescriptor fd) throws ErrnoException;
    public native void fsync(FileDescriptor fd) throws ErrnoException;
    public native void ftruncate(FileDescriptor fd, long length) throws ErrnoException;
    public native String gai_strerror(int error);
    public native InetAddress[] getaddrinfo(String node, StructAddrinfo hints) throws GaiException;
    public native int getegid();
    public native int geteuid();
    public native int getgid();
    public native String getenv(String name);
    public native String getnameinfo(InetAddress address, int flags) throws GaiException;
    public native SocketAddress getpeername(FileDescriptor fd) throws ErrnoException;
    public native int getpid();
    public native int getppid();
    public native StructPasswd getpwnam(String name) throws ErrnoException;
    public native StructPasswd getpwuid(int uid) throws ErrnoException;
    public native SocketAddress getsockname(FileDescriptor fd) throws ErrnoException;
    public native int getsockoptByte(FileDescriptor fd, int level, int option) throws ErrnoException;
    public native InetAddress getsockoptInAddr(FileDescriptor fd, int level, int option) throws ErrnoException;
    public native int getsockoptInt(FileDescriptor fd, int level, int option) throws ErrnoException;
    public native StructLinger getsockoptLinger(FileDescriptor fd, int level, int option) throws ErrnoException;
    public native StructTimeval getsockoptTimeval(FileDescriptor fd, int level, int option) throws ErrnoException;
    public native StructUcred getsockoptUcred(FileDescriptor fd, int level, int option) throws ErrnoException;
    public native int getuid();
    public native String if_indextoname(int index);
    public native InetAddress inet_pton(int family, String address);
    public native InetAddress ioctlInetAddress(FileDescriptor fd, int cmd, String interfaceName) throws ErrnoException;
    public native int ioctlInt(FileDescriptor fd, int cmd, MutableInt arg) throws ErrnoException;
    public native boolean isatty(FileDescriptor fd);
    public native void kill(int pid, int signal) throws ErrnoException;
    public native void lchown(String path, int uid, int gid) throws ErrnoException;
    public native void listen(FileDescriptor fd, int backlog) throws ErrnoException;
    public native long lseek(FileDescriptor fd, long offset, int whence) throws ErrnoException;
    public native StructStat lstat(String path) throws ErrnoException;
    public native void mincore(long address, long byteCount, byte[] vector) throws ErrnoException;
    public native void mkdir(String path, int mode) throws ErrnoException;
    public native void mlock(long address, long byteCount) throws ErrnoException;
    public native long mmap(long address, long byteCount, int prot, int flags, FileDescriptor fd, long offset) throws ErrnoException;
    public native void msync(long address, long byteCount, int flags) throws ErrnoException;
    public native void munlock(long address, long byteCount) throws ErrnoException;
    public native void munmap(long address, long byteCount) throws ErrnoException;
    public native FileDescriptor open(String path, int flags, int mode) throws ErrnoException;
    public native FileDescriptor[] pipe() throws ErrnoException;
    public native int poll(StructPollfd[] fds, int timeoutMs) throws ErrnoException;
    public int pread(FileDescriptor fd, ByteBuffer buffer, long offset) throws ErrnoException {
        if (buffer.isDirect()) {
            return preadBytes(fd, buffer, buffer.position(), buffer.remaining(), offset);
        } else {
            return preadBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining(), offset);
        }
    }
    public int pread(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, long offset) throws ErrnoException {
        // This indirection isn't strictly necessary, but ensures that our public interface is type safe.
        return preadBytes(fd, bytes, byteOffset, byteCount, offset);
    }
// begin WITH_TAINT_TRACKING
    //private native int preadBytes(FileDescriptor fd, Object buffer, int bufferOffset, int byteCount, long offset) throws ErrnoException;
    private native int preadBytesImpl(FileDescriptor fd, Object buffer, int bufferOffset, int byteCount, long offset) throws ErrnoException;
    private int preadBytes(FileDescriptor fd, Object buffer, int bufferOffset, int byteCount, long offset) throws ErrnoException {
        if (buffer == null) {
            throw new NullPointerException();
        }
        int bytesRead = preadBytesImpl(fd, buffer, bufferOffset, byteCount, offset);
        int fdInt = fd.getDescriptor();
        int tag = Taint.getTaintFile(fdInt);
        if (tag != Taint.TAINT_CLEAR) {
            String dstr = new String((byte[])buffer, bufferOffset, ((byteCount > Taint.dataBytesToLog) ? Taint.dataBytesToLog : byteCount));
            // replace non-printable characters
            dstr = dstr.replaceAll("\\p{C}", ".");
            String tstr = "0x" + Integer.toHexString(tag);
            Taint.log("libcore.os.read(" + fdInt + ") reading with tag " + tstr + " data[" + dstr + "]");
            Taint.addTaintByteArray((byte[])buffer, tag);
        }
        return bytesRead;
    }
// end WITH_TAINT_TRACKING
    public int pwrite(FileDescriptor fd, ByteBuffer buffer, long offset) throws ErrnoException {
        if (buffer.isDirect()) {
// begin WITH_TAINT_TRACKING
            int tag = buffer.getDirectByteBufferTaint();
            if (tag != Taint.TAINT_CLEAR) {
                int fdInt = fd.getDescriptor();
                Taint.logPathFromFd(fdInt);
                String tstr = "0x" + Integer.toHexString(tag);
                Taint.log("libcore.os.pwrite(" + fdInt + ") writing a direct ByteBuffer with tag " + tstr);
                Taint.addTaintFile(fdInt, tag);
            }
// end WITH_TAINT_TRACKING
            return pwriteBytes(fd, buffer, buffer.position(), buffer.remaining(), offset);
        } else {
            return pwriteBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining(), offset);
        }
    }
    public int pwrite(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, long offset) throws ErrnoException {
        // This indirection isn't strictly necessary, but ensures that our public interface is type safe.
        return pwriteBytes(fd, bytes, byteOffset, byteCount, offset);
    }
// begin WITH_TAINT_TRACKING
    //private native int pwriteBytes(FileDescriptor fd, Object buffer, int bufferOffset, int byteCount, long offset) throws ErrnoException;
    private native int pwriteBytesImpl(FileDescriptor fd, Object buffer, int bufferOffset, int byteCount, long offset) throws ErrnoException;
    private int pwriteBytes(FileDescriptor fd, Object buffer, int bufferOffset, int byteCount, long offset) throws ErrnoException {
        if (buffer == null) {
            throw new NullPointerException();
        }

        if (buffer instanceof byte[]) {
            int fdInt = fd.getDescriptor();
            int tag = Taint.getTaintByteArray((byte[]) buffer);
            if (tag != Taint.TAINT_CLEAR) {
                String dstr = new String((byte[]) buffer, bufferOffset, ((byteCount > Taint.dataBytesToLog) ? Taint.dataBytesToLog : byteCount));
                // replace non-printable characters
                dstr = dstr.replaceAll("\\p{C}", ".");
                Taint.logPathFromFd(fdInt);
                String tstr = "0x" + Integer.toHexString(tag);
                Taint.log("libcore.os.pwrite(" + fdInt + ") writing with tag " + tstr + " data[" + dstr + "]");
                Taint.addTaintFile(fdInt, tag);
            }
        }
        int bytesWritten = pwriteBytesImpl(fd, buffer, bufferOffset, byteCount, offset);
        return bytesWritten;
    }
// end WITH_TAINT_TRACKING
    public int read(FileDescriptor fd, ByteBuffer buffer) throws ErrnoException {
        if (buffer.isDirect()) {
            return readBytes(fd, buffer, buffer.position(), buffer.remaining());
        } else {
            return readBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining());
        }
    }
    public int read(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount) throws ErrnoException {
        // This indirection isn't strictly necessary, but ensures that our public interface is type safe.
        return readBytes(fd, bytes, byteOffset, byteCount);
    }

// begin WITH_TAINT_TRACKING
    //private native int readBytes(FileDescriptor fd, Object buffer, int offset, int byteCount) throws ErrnoException;
    private native int readBytesImpl(FileDescriptor fd, Object buffer, int offset, int byteCount) throws ErrnoException;
    private int readBytes(FileDescriptor fd, Object buffer, int offset, int byteCount) throws ErrnoException {
        if (buffer == null) {
            throw new NullPointerException();
        }
        int bytesRead = readBytesImpl(fd, buffer, offset, byteCount);
        int fdInt = fd.getDescriptor();
        int tag = Taint.getTaintFile(fdInt);
        if (tag != Taint.TAINT_CLEAR) {
            String dstr = new String((byte[])buffer, offset, ((byteCount > Taint.dataBytesToLog) ? Taint.dataBytesToLog : byteCount));
            // replace non-printable characters
            dstr = dstr.replaceAll("\\p{C}", ".");
            String tstr = "0x" + Integer.toHexString(tag);
            Taint.log("libcore.os.read(" + fdInt + ") reading with tag " + tstr + " data[" + dstr + "]");
            Taint.addTaintByteArray((byte[])buffer, tag);
        }
        return bytesRead;
    }
// end WITH_TAINT_TRACKING
    public native int readv(FileDescriptor fd, Object[] buffers, int[] offsets, int[] byteCounts) throws ErrnoException;
    public int recvfrom(FileDescriptor fd, ByteBuffer buffer, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
        if (buffer.isDirect()) {
            return recvfromBytes(fd, buffer, buffer.position(), buffer.remaining(), flags, srcAddress);
        } else {
            return recvfromBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining(), flags, srcAddress);
        }
    }
    public int recvfrom(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
        // This indirection isn't strictly necessary, but ensures that our public interface is type safe.
        return recvfromBytes(fd, bytes, byteOffset, byteCount, flags, srcAddress);
    }

	/**
	 * returns taint that should be put on 
	 */
	private int recvfromBytes(FileDescriptor fd, Object buffer, int byteOffset, int byteCount, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException {
		int next = byteCount;

		SocketTaint st = incomingSocketTaint.get(fd.getDescriptor());
		if(st == null) {
			Taint.log("have to read new policy");
			long r = recvfromBytesPolicy(fd,flags,srcAddress);
			int taint = (int)r;
			int length = (int)(r>>32);
			if(r == -1) {
				return -1;
			} else if (r == -2) {
				throw new SocketException("policy layer left in invalid state");
			}
			st = new SocketTaint(taint, length);
			incomingSocketTaint.put(fd.getDescriptor(), st);
		}
		Taint.log("got policy " + st.taint + " for next " + st.byteCount + " bytes");
		if(st.byteCount < next)
			next = st.byteCount;

		int r = recvfromBytesImpl(fd, buffer, byteOffset, next, flags, srcAddress);
		Taint.log("read " + r + " bytes");
		if(r == -1)
			return -1;

		st.byteCount -= r;
		Taint.log("" + st.byteCount + " bytes remaining with current policy");
		if(st.byteCount == 0) {
			Taint.log("removing policy so we get a new one next time");
			incomingSocketTaint.remove(fd.getDescriptor());
		}
		Taint.addTaintByteArray((byte[])buffer, st.taint);
		return r;
	}
    private native int recvfromBytesImpl(FileDescriptor fd, Object buffer, int byteOffset, int byteCount, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException;
	private native long recvfromBytesPolicy(FileDescriptor fd, int flags, InetSocketAddress srcAddress) throws ErrnoException, SocketException;
    public native void remove(String path) throws ErrnoException;
    public native void rename(String oldPath, String newPath) throws ErrnoException;
    public native long sendfile(FileDescriptor outFd, FileDescriptor inFd, MutableLong inOffset, long byteCount) throws ErrnoException;
    public int sendto(FileDescriptor fd, ByteBuffer buffer, int flags, InetAddress inetAddress, int port) throws ErrnoException, SocketException {
        if (buffer.isDirect()) {
// begin WITH_TAINT_TRACKING
            int tag = buffer.getDirectByteBufferTaint();
            if (tag != Taint.TAINT_CLEAR) {
                String addr = (fd.hasName) ? fd.name : "unknown";
                String tstr = "0x" + Integer.toHexString(tag);
                Taint.log("libcore.os.sendto(" + addr + ") received a ByteBuffer with tag " + tstr);
            }
// end WITH_TAINT_TRACKING
            return sendtoBytes(fd, buffer, buffer.position(), buffer.remaining(), flags, inetAddress, port, tag);
        } else {
			byte[] bytes = NioUtils.unsafeArray(buffer);
			int tag = dalvik.agate.PolicyManagementModule.getPolicyByteArray(bytes);
            return sendtoBytes(fd, bytes, NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining(), flags, inetAddress, port, tag);
        }
    }
    public int sendto(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount, int flags, InetAddress inetAddress, int port) throws ErrnoException, SocketException {
        // This indirection isn't strictly necessary, but ensures that our public interface is type safe.
		int tag = dalvik.agate.PolicyManagementModule.getPolicyByteArray(bytes);
        return sendtoBytes(fd, bytes, byteOffset, byteCount, flags, inetAddress, port, tag);
    }

// begin WITH_TAINT_TRACKING
    //private native int sendtoBytes(FileDescriptor fd, Object buffer, int byteOffset, int byteCount, int flags, InetAddress inetAddress, int port) throws ErrnoException;
    private native int sendtoBytesImpl(FileDescriptor fd, Object buffer, int byteOffset, int byteCount, int flags, InetAddress inetAddress, int port, int taint) throws ErrnoException, SocketException;
    private int sendtoBytes(FileDescriptor fd, Object buffer, int byteOffset, int byteCount, int flags, InetAddress inetAddress, int port, int taint) throws ErrnoException, SocketException {

        //begin WITH_SAPPHIRE_AGATE

        /* Check if policy allows to send */
        int fd_policy = PolicyManagementModule.getPolicySocket(fd.getDescriptor());
        Taint.log("Policy on fd socket = 0x" + Integer.toHexString(fd_policy));
        Taint.log("sendtobytes with tag = 0x" + Integer.toHexString(taint));

        if (PolicyManagementModule.canFlow(taint, fd_policy) == false) {
            Taint.log("Cannot send over network;  from label = 0x" + Integer.toHexString(taint) +
                                                  " to label = 0x" + Integer.toHexString(fd_policy));
            throw new SocketException("Illegal data flow");
        }

        Taint.log("CAN send over network;");

		return sendtoBytesImpl(fd, buffer, byteOffset, byteCount, flags, inetAddress, port, taint);
    }
// end WITH_TAINT_TRACKING

    public native void setegid(int egid) throws ErrnoException;
    public native void setenv(String name, String value, boolean overwrite) throws ErrnoException;
    public native void seteuid(int euid) throws ErrnoException;
    public native void setgid(int gid) throws ErrnoException;
    public native int setsid() throws ErrnoException;
    public native void setsockoptByte(FileDescriptor fd, int level, int option, int value) throws ErrnoException;
    public native void setsockoptIfreq(FileDescriptor fd, int level, int option, String value) throws ErrnoException;
    public native void setsockoptInt(FileDescriptor fd, int level, int option, int value) throws ErrnoException;
    public native void setsockoptIpMreqn(FileDescriptor fd, int level, int option, int value) throws ErrnoException;
    public native void setsockoptGroupReq(FileDescriptor fd, int level, int option, StructGroupReq value) throws ErrnoException;
    public native void setsockoptLinger(FileDescriptor fd, int level, int option, StructLinger value) throws ErrnoException;
    public native void setsockoptTimeval(FileDescriptor fd, int level, int option, StructTimeval value) throws ErrnoException;
    public native void setuid(int uid) throws ErrnoException;
    public native void shutdown(FileDescriptor fd, int how) throws ErrnoException;
    public native FileDescriptor socket(int domain, int type, int protocol) throws ErrnoException;
    public native void socketpair(int domain, int type, int protocol, FileDescriptor fd1, FileDescriptor fd2) throws ErrnoException;
    public native StructStat stat(String path) throws ErrnoException;
    public native StructStatFs statfs(String path) throws ErrnoException;
    public native String strerror(int errno);
    public native String strsignal(int signal);
    public native void symlink(String oldPath, String newPath) throws ErrnoException;
    public native long sysconf(int name);
    public native void tcdrain(FileDescriptor fd) throws ErrnoException;
    public native void tcsendbreak(FileDescriptor fd, int duration) throws ErrnoException;
    public int umask(int mask) {
        if ((mask & 0777) != mask) {
            throw new IllegalArgumentException("Invalid umask: " + mask);
        }
        return umaskImpl(mask);
    }
    private native int umaskImpl(int mask);
    public native StructUtsname uname();
    public native void unsetenv(String name) throws ErrnoException;
    public native int waitpid(int pid, MutableInt status, int options) throws ErrnoException;
    public int write(FileDescriptor fd, ByteBuffer buffer) throws ErrnoException {
        if (buffer.isDirect()) {
// begin WITH_TAINT_TRACKING
            int tag = buffer.getDirectByteBufferTaint();
            if (tag != Taint.TAINT_CLEAR) {
                int fdInt = fd.getDescriptor();
                Taint.logPathFromFd(fdInt);
                String tstr = "0x" + Integer.toHexString(tag);
                Taint.log("libcore.os.write(" + fdInt + ") writing a direct ByteBuffer with tag " + tstr);
                Taint.addTaintFile(fdInt, tag);
            }
// end WITH_TAINT_TRACKING
            return writeBytes(fd, buffer, buffer.position(), buffer.remaining());
        } else {
            return writeBytes(fd, NioUtils.unsafeArray(buffer), NioUtils.unsafeArrayOffset(buffer) + buffer.position(), buffer.remaining());
        }
    }
    public int write(FileDescriptor fd, byte[] bytes, int byteOffset, int byteCount) throws ErrnoException {
        // This indirection isn't strictly necessary, but ensures that our public interface is type safe.
        return writeBytes(fd, bytes, byteOffset, byteCount);
    }

    //begin WITH_TAINT_TRACKING
    //private native int writeBytes(FileDescriptor fd, Object buffer, int offset, int byteCount) throws ErrnoException;
    private native int writeBytesImpl(FileDescriptor fd, Object buffer, int offset, int byteCount) throws ErrnoException;
    private int writeBytes(FileDescriptor fd, Object buffer, int offset, int byteCount) throws ErrnoException {
        if (buffer == null) {
            throw new NullPointerException();
        }
	
        if (buffer instanceof byte[]) {
            int fdInt = fd.getDescriptor();
            int tag = Taint.getTaintByteArray((byte[]) buffer);
            if (tag != Taint.TAINT_CLEAR) {
                //We only display at most Taint.dataBytesToLog characters of the data in logcat, to avoid the overflow
                String dstr = new String((byte[]) buffer, offset, ((byteCount > Taint.dataBytesToLog) ? Taint.dataBytesToLog : byteCount));
                // replace non-printable characters
                dstr = dstr.replaceAll("\\p{C}", ".");
                Taint.logPathFromFd(fdInt);
                String tstr = "0x" + Integer.toHexString(tag);
                Taint.log("libcore.os.write(" + fdInt + ") writing with tag " + tstr + " data[" + dstr + "]");
                Taint.addTaintFile(fdInt, tag);
            }
        }
        int bytesWritten = writeBytesImpl(fd, buffer, offset, byteCount);
        return bytesWritten;
    }
//end WITH_TAINT_TRACKING
    public native int writev(FileDescriptor fd, Object[] buffers, int[] offsets, int[] byteCounts) throws ErrnoException;
}
