package org.example;

import org.example.strategy.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static void main(String[] args) {
        int elementsNumber = 10000;
        testStrategy(new HashMapStorageStrategy(), elementsNumber);
        Helper.printMessage("-------------------------------");
        testStrategy(new OurHashMapStorageStrategy(), elementsNumber);
        Helper.printMessage("-------------------------------");
        testStrategy(new HashBiMapStorageStrategy(), elementsNumber);
        Helper.printMessage("-------------------------------");
        testStrategy(new DualHashBidiMapStorageStrategy(), elementsNumber);
    }

    public static void testStrategy(StorageStrategy storageStrategy, long elementsNumber) {
        Helper.printMessage(storageStrategy.getClass().getSimpleName() + ":");

        Set<String> testSet = new HashSet<>();
        for (int i = 0; i < elementsNumber; i++) {
            testSet.add(Helper.generateRandomString());
        }

        Shortener shortener = new Shortener(storageStrategy);
        Date date = new Date();
        Set<Long> ids = getIds(shortener, testSet);
        System.out.println("Время получения id: " + (new Date().getTime() - date.getTime()));

        date = new Date();
        Set<String> strings = getStrings(shortener, ids);
        System.out.println("Время получения строк: " + (new Date().getTime() - date.getTime()));

        boolean isTestCompleted = strings.equals(testSet);
        Helper.printMessage(isTestCompleted ? "Тест пройден" : "Тест не пройден");
    }

    public static Set<Long> getIds(Shortener shortener, Set<String> strings) {
        HashSet<Long> ids = new HashSet<>();
        for (String string : strings) {
            ids.add(shortener.getId(string));
        }
        return ids;
    }

    public static Set<String> getStrings(Shortener shortener, Set<Long> keys) {
        HashSet<String> strings = new HashSet<>();
        for (Long key : keys) {
            strings.add(shortener.getString(key));
        }
        return strings;
    }
}
