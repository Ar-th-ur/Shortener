package org.example.strategy;

public class FileStorageStrategy implements StorageStrategy {
    private static final int DEFAULT_INITIAL_CAPACITY  = 16;
    private static final int DEFAULT_BUCKET_SIZE_LIMIT = 10_000;

    private long bucketSizeLimit = DEFAULT_BUCKET_SIZE_LIMIT;

    private long         maxBucketSize;
    private FileBucket[] table;
    private int          size;


    public FileStorageStrategy() {
        init();
    }

    private void init() {
        table = new FileBucket[DEFAULT_INITIAL_CAPACITY];
        for (int i = 0; i < DEFAULT_INITIAL_CAPACITY; i++) {
            table[i] = new FileBucket();
        }
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    public void resize(int newCapacity) {
        FileBucket[] newTable = new FileBucket[newCapacity];
        for (int i = 0; i < newCapacity; i++) {
            newTable[i] = new FileBucket();
        }
        transfer(newTable);

        for (FileBucket bucket : table) {
            bucket.remove();
        }
        table = newTable;
    }

    public void transfer(FileBucket[] newTable) {
        int newCapacity = newTable.length;
        maxBucketSize = 0;

        for (FileBucket fileBucket : newTable) {
            for (Entry entry = fileBucket.getEntry(); entry != null; entry = entry.next) {
                int index = indexFor(entry.getKey().hashCode(), newCapacity);
                entry.next = newTable[index].getEntry();
                newTable[index].putEntry(entry);
            }

            long bucketSize = fileBucket.getFileSize();
            maxBucketSize = Math.max(maxBucketSize, bucketSize);
        }
    }

    @Override
    public boolean containsKey(Long key) {
        return getEntry(key) != null;
    }

    @Override
    public boolean containsValue(String value) {
        for (FileBucket fileBucket : table) {
            for (Entry entry = fileBucket.getEntry(); entry != null; entry = entry.next) {
                if (entry.getValue().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void put(Long key, String value) {
        int index = indexFor(key.hashCode(), table.length);
        for (Entry entry = table[index].getEntry(); entry != null; entry = entry.next) {
            if (entry.getKey().equals(key)) {
                entry.value = value;
                return;
            }
        }
        addEntry(hashCode(), key, value, index);
    }

    private void addEntry(int hash, Long key, String value, int bucketIndex) {
        if (maxBucketSize > bucketSizeLimit) {
            resize(2 * table.length);
            bucketIndex = indexFor(key.hashCode(), table.length);
        }
        createEntry(hash, key, value, bucketIndex);
    }

    private void createEntry(int hash, Long key, String value, int bucketIndex) {
        Entry e = table[bucketIndex].getEntry();
        table[bucketIndex].putEntry(new Entry(key, value, e, hash));
        size++;

        long bucketSize = table[bucketIndex].getFileSize();
        maxBucketSize = Math.max(bucketSize, maxBucketSize);
    }

    public Entry getEntry(Long key) {
        if (size > 0) {
            for (FileBucket fileBucket : table) {
                for (Entry entry = fileBucket.getEntry(); entry != null; entry = entry.next) {
                    if (entry.getKey().equals(key)) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Long getKey(String value) {
        for (FileBucket fileBucket : table) {
            for (Entry entry = fileBucket.getEntry(); entry != null; entry = entry.next) {
                if (entry.getValue().equals(value)) {
                    return entry.getKey();
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

    public long getBucketSizeLimit() {
        return bucketSizeLimit;
    }

    public void setBucketSizeLimit(long bucketSizeLimit) {
        this.bucketSizeLimit = bucketSizeLimit;
    }
}
