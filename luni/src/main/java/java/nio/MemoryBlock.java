/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package java.nio;

import dalvik.system.VMRuntime;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.FileChannel.MapMode;
import libcore.io.ErrnoException;
import libcore.io.Libcore;
import libcore.io.Memory;
import static libcore.io.OsConstants.*;

// begin WITH_SAPPHIRE_AGATE
import dalvik.agate.PolicyManagementModule;
import dalvik.agate.AgatePolicy;
// end WITH_SAPPHIRE_AGATE

class MemoryBlock {
// begin WITH_SAPPHIRE_AGATE
    protected AgatePolicy p;
// end WITH_SAPPHIRE_AGATE

    /**
     * Handles calling munmap(2) on a memory-mapped region.
     */
    private static class MemoryMappedBlock extends MemoryBlock {
        private MemoryMappedBlock(long address, long byteCount) {
            super(address, byteCount);
        }

        @Override public void free() {
            if (address != 0) {
                try {
                    Libcore.os.munmap(address, size);
                } catch (ErrnoException errnoException) {
                    // The RI doesn't throw, presumably on the assumption that you can't get into
                    // a state where munmap(2) could return an error.
                    throw new AssertionError(errnoException);
                }
                address = 0;
            }
        }

        @Override protected void finalize() throws Throwable {
            free();
        }
    }

    /**
     * Non-movable heap blocks are byte arrays on the Java heap that the GC
     * guarantees not to move. Used to implement DirectByteBuffer.
     *
     * Losing the strong reference to the array is sufficient
     * to allow the GC to reclaim the storage. No finalizer needed.
     */
    private static class NonMovableHeapBlock extends MemoryBlock {
        private byte[] array;

        private NonMovableHeapBlock(byte[] array, long address, long byteCount) {
            super(address, byteCount);
            this.array = array;
        }

        @Override public byte[] array() {
            return array;
        }

        @Override public void free() {
            array = null;
            address = 0;
        }
   
// begin WITH_SAPPHIRE_AGATE
        @Override public void addPolicy(int tag) {
            super.addPolicy(tag);
            PolicyManagementModule.addPolicyByteArray(array, tag);
        }
// end WITH_SAPPHIRE_AGATE
    }

    /**
     * Represents a block of memory we don't own. (We don't take ownership of memory corresponding
     * to direct buffers created by the JNI NewDirectByteBuffer function.)
     */
    private static class UnmanagedBlock extends MemoryBlock {
        private UnmanagedBlock(long address, long byteCount) {
            super(address, byteCount);
        }
    }

    protected long address;
    protected final long size;


    public static MemoryBlock mmap(FileDescriptor fd, long offset, long size, MapMode mapMode) throws IOException {
        if (size == 0) {
            // You can't mmap(2) a zero-length region, but Java allows it.
            return new MemoryBlock(0, 0);
        }
        // Check just those errors mmap(2) won't detect.
        if (offset < 0 || size < 0 || offset > Integer.MAX_VALUE || size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("offset=" + offset + " size=" + size);
        }
        int prot;
        int flags;
        if (mapMode == MapMode.PRIVATE) {
            prot = PROT_READ|PROT_WRITE;
            flags = MAP_PRIVATE;
        } else if (mapMode == MapMode.READ_ONLY) {
            prot = PROT_READ;
            flags = MAP_SHARED;
        } else { // mapMode == MapMode.READ_WRITE
            prot = PROT_READ|PROT_WRITE;
            flags = MAP_SHARED;
        }
        try {
            long address = Libcore.os.mmap(0L, size, prot, flags, fd, offset);
            return new MemoryMappedBlock(address, size);
        } catch (ErrnoException errnoException) {
            throw errnoException.rethrowAsIOException();
        }
    }

    public static MemoryBlock allocate(int byteCount) {
        VMRuntime runtime = VMRuntime.getRuntime();
        byte[] array = (byte[]) runtime.newNonMovableArray(byte.class, byteCount);
        long address = runtime.addressOf(array);
        return new NonMovableHeapBlock(array, address, byteCount);
    }

    public static MemoryBlock wrapFromJni(long address, long byteCount) {
        return new UnmanagedBlock(address, byteCount);
    }

    private MemoryBlock(long address, long size) {
        this.address = address;
        this.size = size;
        // begin WITH_SAPPHIRE_AGATE
        p = new AgatePolicy();
        // end WITH_SAPPHIRE_AGATE
    }

    // Used to support array/arrayOffset/hasArray for direct buffers.
    public byte[] array() {
        return null;
    }

    public void free() {
    }

    public final void pokeByte(int offset, byte value) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyByte(value));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeByte(address + offset, value);
    }

    public final void pokeByteArray(int offset, byte[] src, int srcOffset, int byteCount) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyByteArray(src));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeByteArray(address + offset, src, srcOffset, byteCount);
    }

    public final void pokeCharArray(int offset, char[] src, int srcOffset, int charCount, boolean swap) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyCharArray(src));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeCharArray(address + offset, src, srcOffset, charCount, swap);
    }

    public final void pokeDoubleArray(int offset, double[] src, int srcOffset, int doubleCount, boolean swap) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyDoubleArray(src));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeDoubleArray(address + offset, src, srcOffset, doubleCount, swap);
    }

    public final void pokeFloatArray(int offset, float[] src, int srcOffset, int floatCount, boolean swap) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyFloatArray(src));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeFloatArray(address + offset, src, srcOffset, floatCount, swap);
    }

    public final void pokeIntArray(int offset, int[] src, int srcOffset, int intCount, boolean swap) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyIntArray(src));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeIntArray(address + offset, src, srcOffset, intCount, swap);
    }

    public final void pokeLongArray(int offset, long[] src, int srcOffset, int longCount, boolean swap) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyLongArray(src));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeLongArray(address + offset, src, srcOffset, longCount, swap);
    }

    public final void pokeShortArray(int offset, short[] src, int srcOffset, int shortCount, boolean swap) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyShortArray(src));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeShortArray(address + offset, src, srcOffset, shortCount, swap);
    }

    public final byte peekByte(int offset) {
// begin WITH_SAPPHIRE_AGATE
//        return Memory.peekByte(address + offset);
        byte val = Memory.peekByte(address + offset);
        return PolicyManagementModule.addPolicyByte(val, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void peekByteArray(int offset, byte[] dst, int dstOffset, int byteCount) {
        Memory.peekByteArray(address + offset, dst, dstOffset, byteCount);
// begin WITH_SAPPHIRE_AGATE
        PolicyManagementModule.addPolicyByteArray(dst, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void peekCharArray(int offset, char[] dst, int dstOffset, int charCount, boolean swap) {
        Memory.peekCharArray(address + offset, dst, dstOffset, charCount, swap);
// begin WITH_SAPPHIRE_AGATE
        PolicyManagementModule.addPolicyCharArray(dst, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void peekDoubleArray(int offset, double[] dst, int dstOffset, int doubleCount, boolean swap) {
        Memory.peekDoubleArray(address + offset, dst, dstOffset, doubleCount, swap);
// begin WITH_SAPPHIRE_AGATE
        PolicyManagementModule.addPolicyDoubleArray(dst, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void peekFloatArray(int offset, float[] dst, int dstOffset, int floatCount, boolean swap) {
        Memory.peekFloatArray(address + offset, dst, dstOffset, floatCount, swap);
// begin WITH_SAPPHIRE_AGATE
        PolicyManagementModule.addPolicyFloatArray(dst, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void peekIntArray(int offset, int[] dst, int dstOffset, int intCount, boolean swap) {
        Memory.peekIntArray(address + offset, dst, dstOffset, intCount, swap);
// begin WITH_SAPPHIRE_AGATE
        PolicyManagementModule.addPolicyIntArray(dst, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void peekLongArray(int offset, long[] dst, int dstOffset, int longCount, boolean swap) {
        Memory.peekLongArray(address + offset, dst, dstOffset, longCount, swap);
// begin WITH_SAPPHIRE_AGATE
        PolicyManagementModule.addPolicyLongArray(dst, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void peekShortArray(int offset, short[] dst, int dstOffset, int shortCount, boolean swap) {
        Memory.peekShortArray(address + offset, dst, dstOffset, shortCount, swap);
// begin WITH_SAPPHIRE_AGATE
        PolicyManagementModule.addPolicyShortArray(dst, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void pokeShort(int offset, short value, ByteOrder order) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyShort(value));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeShort(address + offset, value, order.needsSwap);
    }

    public final short peekShort(int offset, ByteOrder order) {
// begin WITH_SAPPHIRE_AGATE
//        return Memory.peekShort(address + offset, order.needsSwap);
        short val = Memory.peekShort(address + offset, order.needsSwap);
        return PolicyManagementModule.addPolicyShort(val, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void pokeInt(int offset, int value, ByteOrder order) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyInt(value));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeInt(address + offset, value, order.needsSwap);
    }

    public final int peekInt(int offset, ByteOrder order) {
// begin WITH_SAPPHIRE_AGATE
//        return Memory.peekShort(address + offset, order.needsSwap);
        int val = Memory.peekInt(address + offset, order.needsSwap);
        return PolicyManagementModule.addPolicyInt(val, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final void pokeLong(int offset, long value, ByteOrder order) {
// begin WITH_SAPPHIRE_AGATE
        addPolicy(PolicyManagementModule.getPolicyLong(value));
// end WITH_SAPPHIRE_AGATE
        Memory.pokeLong(address + offset, value, order.needsSwap);
    }

    public final long peekLong(int offset, ByteOrder order) {
// begin WITH_SAPPHIRE_AGATE
//        return Memory.peekShort(address + offset, order.needsSwap);
        long val = Memory.peekLong(address + offset, order.needsSwap);
        return PolicyManagementModule.addPolicyLong(val, p.getPolicy());
// end WITH_SAPPHIRE_AGATE
    }

    public final long toLong() {
        return address;
    }

    public final String toString() {
        return getClass().getName() + "[" + address + "]";
    }

    public final long getSize() {
        return size;
    }

// begin WITH_SAPPHIRE_AGATE
    public void addPolicy(int tag) {
        int newTag = PolicyManagementModule.mergePolicies(getPolicy(), tag);
        p.addPolicy(newTag);
    }

    public int getPolicy() {
        return p.getPolicy();
    }
// end WITH_SAPPHIRE_AGATE
}
