package xyz.ufactions.customcrates.libs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WeightedList<T> {

    public class Entry {
        public T object;
        public double accumulatedWeight;
    }

    private final List<Entry> entries = new ArrayList<>();
    private final Random random = new Random();
    private double accumulatedWeight;

    public void add(T object, double weight) {
        accumulatedWeight += weight;
        Entry entry = new Entry();
        entry.accumulatedWeight = accumulatedWeight;
        entry.object = object;
        entries.add(entry);
    }

    public T get() {
        double random = this.random.nextDouble() * accumulatedWeight;

        List<Entry> availableEntries = new ArrayList<>();
        for (Entry entry : entries) {
            if (entry.accumulatedWeight >= random) {
                availableEntries.add(entry);
            }
        }
        if (availableEntries.isEmpty()) return null;
        if (availableEntries.size() == 1) return availableEntries.get(0).object;
        return availableEntries.get(this.random.nextInt(availableEntries.size())).object;
    }

    public T get(int index) {
        return entries.get(index).object;
    }

    public int size() {
        return entries.size();
    }

    public List<Entry> entries() {
        return entries;
    }
}