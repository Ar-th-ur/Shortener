import org.example.Helper;
import org.example.Shortener;
import org.example.strategy.*;
import org.junit.Assert;
import org.junit.Test;

public class FunctionalTest {

    @Test
    public void testHashMapStorageStrategy() {
        Shortener shortener = new Shortener(new HashMapStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testOurHashMapStorageStrategy() {
        Shortener shortener = new Shortener(new OurHashMapStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testOurHashBiMapStorageStrategy() {
        Shortener shortener = new Shortener(new HashBiMapStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testDualHashBidiMapStorageStrategy() {
        Shortener shortener = new Shortener(new DualHashBidiMapStorageStrategy());
        testStorage(shortener);
    }

    @Test
    public void testFileStorageStrategy() {
        Shortener shortener = new Shortener(new FileStorageStrategy());
        testStorage(shortener);
    }

    public void testStorage(Shortener shortener) {
        String s1origin = Helper.generateRandomString();
        String s2origin = Helper.generateRandomString();
        String s3origin = s1origin;

        long id1 = shortener.getId(s1origin);
        long id2 = shortener.getId(s2origin);
        long id3 = shortener.getId(s3origin);
        Assert.assertNotEquals(id1, id2);
        Assert.assertNotEquals(id3, id2);
        Assert.assertEquals(id1, id3);

        String s1 = shortener.getString(id1);
        String s2 = shortener.getString(id2);
        String s3 = shortener.getString(id3);
        Assert.assertEquals(s1, s1origin);
        Assert.assertEquals(s2, s2origin);
        Assert.assertEquals(s3, s3origin);
    }
}
