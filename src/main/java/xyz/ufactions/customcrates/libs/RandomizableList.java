package xyz.ufactions.customcrates.libs;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class RandomizableList<T> {

    public class Entry {

        public final T object;
        private final Double insertionWeight;
        public final Double weight;

        public Entry(T object, Double insertionWeight, Double weight) {
            this.object = object;
            this.insertionWeight = insertionWeight;
            this.weight = weight;
        }
    }

    private final TreeMap<Double, Entry> entries = new TreeMap<>();
    private final Random random = new Random();
    private double accumulatedWeight = 0;

    public synchronized void add(T object, double weight) {
        entries.put(accumulatedWeight += weight, new Entry(object, accumulatedWeight, weight));
    }

    public synchronized boolean contain(T object) {
        for (Entry entry : entries.values()) {
            if(entry.object == object) {
                return true;
            }
        }
        return false;
    }

    public synchronized void remove(T object) {
        for (Map.Entry<Double, Entry> entry : entries.entrySet()) {
            if(entry.getValue().object == object) {
                accumulatedWeight -= entry.getValue().weight;
                entries.remove(entry.getValue().insertionWeight);
            }
        }
    }

    public synchronized T get() {
        return entries.ceilingEntry(random.nextDouble() * accumulatedWeight).getValue().object;
    }

    public double getAccumulatedWeight() {
        return accumulatedWeight;
    }

    public int size() {
        return entries.size();
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public TreeMap<Double, Entry> getEntries() {
        return entries;
    }
}