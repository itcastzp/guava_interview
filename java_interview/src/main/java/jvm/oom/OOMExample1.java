package jvm.oom;

import org.junit.Test;

import java.util.ArrayList;

public class OOMExample1 {
    static class OOMObject {
    }

    /**
     * 堆内存溢出演示
     * vm-arg:  -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
     *
     * @throws Exception
     */
    @Test
    public void testHeapOOM() throws Exception {
        ArrayList<OOMObject> oomObjects = new ArrayList<OOMObject>();

        for (; ; ) {
            oomObjects.add(new OOMObject());

        }

    }


}
