package xyz.ufactions.customcrates.libs;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public final class RandomizableList<T> implements Iterable<RandomizableList<T>.Entry> {

    public class Entry {

        @Getter
        private final T object;
        private final Double insertionWeight;
        @Getter
        private final Double weight;

        public Entry(T object, Double insertionWeight, Double weight) {
            this.object = object;
            this.insertionWeight = insertionWeight;
            this.weight = weight;
        }
    }

    private final TreeMap<Double, Entry> entries;
    private final ReentrantLock lock;
    private final Random random;

    private double accumulatedWeight = 0;

    public RandomizableList() {
        this.lock = new ReentrantLock();
        this.random = new Random();
        this.entries = new TreeMap<>();
    }

    public void add(T object, double weight) {
        Objects.requireNonNull(object);

        this.lock.lock();
        try {
            entries.put(accumulatedWeight += weight, new Entry(object, accumulatedWeight, weight));
        } finally {
            this.lock.unlock();
        }
    }

    public boolean contain(T object) {
        Objects.requireNonNull(object);

        this.lock.lock();
        try {
            for (Entry entry : entries.values()) {
                if (entry.object == object) {
                    return true;
                }
            }
        } finally {
            this.lock.unlock();
        }
        return false;
    }

    public void remove(T object) {
        Objects.requireNonNull(object);

        this.lock.lock();
        try {
            for (Map.Entry<Double, Entry> entry : entries.entrySet()) {
                if (entry.getValue().object == object) {
                    accumulatedWeight -= entry.getValue().weight;
                    entries.remove(entry.getValue().insertionWeight);
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    public T get() {
        this.lock.lock();
        try {
            return entries.ceilingEntry(random.nextDouble() * accumulatedWeight).getValue().object;
        } finally {
            this.lock.unlock();
        }
    }

    public double getAccumulatedWeight() {
        this.lock.lock();
        try {
            return accumulatedWeight;
        } finally {
            this.lock.unlock();
        }
    }

    public int size() {
        this.lock.lock();
        try {
            return entries.size();
        } finally {
            this.lock.unlock();
        }
    }

    public boolean isEmpty() {
        this.lock.lock();
        try {
            return entries.isEmpty();
        } finally {
            this.lock.unlock();
        }
    }

    public Collection<Entry> getEntries() {
        this.lock.lock();
        try {
            return entries.values();
        } finally {
            this.lock.unlock();
        }
    }

    @NotNull
    @Override
    public Iterator<Entry> iterator() {
        return getEntries().iterator();
    }
}