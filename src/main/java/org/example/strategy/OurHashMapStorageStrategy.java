package org.example.strategy;

public class OurHashMapStorageStrategy implements StorageStrategy {
    private static final int   DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR      = 0.75f;
    private              float loadFactor               = DEFAULT_LOAD_FACTOR;

    private Entry[] table     = new Entry[DEFAULT_INITIAL_CAPACITY];
    private int     threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    private int     size;

    public int hash(Long k) {
        return k.hashCode();
    }

    public static int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    public Entry getEntry(Long key) {
        if (size == 0) {
            return null;
        }

        int hash = hash(key);
        int index = indexFor(hash, table.length);
        for (Entry e = table[index]; e != null; e = e.next) {
            if (e.hash == hash) {
                return e;
            }
        }
        return null;
    }

    public void resize(int newCapacity) {
        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    public void transfer(Entry[] newTable) {
        int newCapacity = newTable.length;

        for (Entry tableElement : table) {
            for (Entry e = tableElement; e != null; e = e.next) {
                int hash = hash(e.getKey());
                int index = indexFor(hash, newCapacity);
                e.next = newTable[index];
                newTable[index] = e;
            }
        }
    }

    @Override
    public void put(Long key, String value) {
        int hash = hash(key);
        int index = indexFor(hash, table.length);
        for (Entry e = table[index]; e != null; e = e.next) {
            if (e.getKey().equals(key)) {
                e.value = value;
                return;
            }
        }

        addEntry(hash, key, value, index);
    }

    public void addEntry(int hash, Long key, String value, int bucketIndex) {
        if (size >= threshold) {
            resize(table.length * 2);
            hash = hash(key);
            bucketIndex = indexFor(hash, table.length);
        }
        createEntry(hash, key, value, bucketIndex);
    }

    public void createEntry(int hash, Long key, String value, int bucketIndex) {
        Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(key, value, e, hash);
        size++;
    }

    @Override
    public boolean containsKey(Long key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(String value) {
        for (Entry tableElement : table) {
            for (Entry e = tableElement; e != null; e = e.next) {
                if (e.getValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Long getKey(String value) {
        for (Entry tableElement : table) {
            for (Entry e = tableElement; e != null; e = e.next) {
                if (e.getValue().equals(value)) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    @Override
    public String getValue(Long key) {
        Entry e = getEntry(key);
        return e == null ? null : e.getValue();
    }
}
