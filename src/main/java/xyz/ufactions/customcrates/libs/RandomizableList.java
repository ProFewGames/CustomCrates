package xyz.ufactions.customcrates.libs;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public final class RandomizableList<T> implements Iterable<RandomizableList<T>.Entry> {

    public class Entry {

        @Getter
        private final T object;
        @Getter
        private final Double weight;

        public Entry(T object, Double weight) {
            this.object = object;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "object=" + object +
                    ", weight=" + weight +
                    '}';
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

    public synchronized void add(T object, double weight) {
        Objects.requireNonNull(object);

        this.lock.lock();
        try {
            entries.put(accumulatedWeight += weight, new Entry(object, weight));
        } finally {
            this.lock.unlock();
        }
    }

    public synchronized boolean contain(T object) {
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

    public synchronized void remove(T object) {
        Objects.requireNonNull(object);

        this.lock.lock();
        try {
            Iterator<Map.Entry<Double, Entry>> iterator = entries.entrySet().iterator();
            Map.Entry<Double, Entry> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (entry.getValue().object == object) {
                    accumulatedWeight -= entry.getValue().weight;
                    iterator.remove();
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    public synchronized T get() {
        this.lock.lock();
        try {
            return entries.ceilingEntry(random.nextDouble() * accumulatedWeight).getValue().object;
        } finally {
            this.lock.unlock();
        }
    }

    public synchronized double getAccumulatedWeight() {
        this.lock.lock();
        try {
            return accumulatedWeight;
        } finally {
            this.lock.unlock();
        }
    }

    public synchronized int size() {
        this.lock.lock();
        try {
            return entries.size();
        } finally {
            this.lock.unlock();
        }
    }

    public synchronized boolean isEmpty() {
        this.lock.lock();
        try {
            return entries.isEmpty();
        } finally {
            this.lock.unlock();
        }
    }

    public synchronized Collection<Entry> getEntries() {
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

    @Override
    public String toString() {
        return "RandomizableList{" +
                "entries=" + entries +
                ", lock=" + lock +
                ", random=" + random +
                ", accumulatedWeight=" + accumulatedWeight +
                '}';
    }
}