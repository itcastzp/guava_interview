/**
 * <a href="http://www.cpupk.com/decompiler">Eclipse Class Decompiler</a> plugin, Copyright (c) 2017 Chen Chao.
 */
package cache;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

public class ClientSetup extends HashMap<Object, Object> {
    private static final long serialVersionUID = 6491528715050979463L;
    private File file = null;

    ClientSetup() {
        super();
    }

    File getFile() {
        return file;
    }

    void setFilePath(File file) {
        this.file = file;
    }

    public void putInt(String key, int i) {
        put(key, Integer.valueOf(i));
    }

    public int getInt(String key, int defaultValue) {
        int i = defaultValue;
        Object o = get(key);
        if (o != null && o instanceof Integer) {
            i = ((Integer) o).intValue();
        }
        return i;
    }

    public void putBoolean(String key, boolean b) {
        put(key, new Boolean(b));
    }

    public Object put(Object key, Object value) {
        if (key == null) {
            throw new RuntimeException("key can't be null !");
        }
        if (value != null && !(value instanceof Serializable)) {
            throw new RuntimeException("value must be Serializable!");
        }
        if (!(key instanceof Serializable)) {
            throw new RuntimeException("key must be Serializable!");
        }
        return super.put(key, value);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        boolean b = defaultValue;
        Object o = get(key);
        if (o != null && o instanceof Boolean) {
            b = ((Boolean) o).booleanValue();
        }
        return b;
    }

    public Object getValue(Object key, Object defaultValue) {
        Object retrValue = defaultValue;
        if (key != null && containsKey(key)) {
            retrValue = get(key);
        }
        return retrValue;
    }

}
