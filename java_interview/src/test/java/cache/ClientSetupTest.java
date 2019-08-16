package cache;

import org.junit.Test;

public class ClientSetupTest {

    @Test
    public void put() {
        ClientSetup setup = new ClientSetup();
        setup.put("a", "b");
        setup.put("a1", "b");
        setup.put("a2", "b");
        setup.put("a3", "b");
        System.out.println(setup.entrySet());


    }

    @Test
    public void getValue() {
    }
}