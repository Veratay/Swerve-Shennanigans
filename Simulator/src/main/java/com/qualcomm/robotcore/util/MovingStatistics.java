//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.qualcomm.robotcore.util;

import java.util.LinkedList;
import java.util.Queue;

public class MovingStatistics {
    final Statistics statistics;
    final int capacity;
    final Queue<Double> samples;

    public MovingStatistics(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("MovingStatistics capacity must be positive");
        } else {
            this.statistics = new Statistics();
            this.capacity = capacity;
            this.samples = new LinkedList();
        }
    }

    public int getCount() {
        return this.statistics.getCount();
    }

    public double getMean() {
        return this.statistics.getMean();
    }

    public double getVariance() {
        return this.statistics.getVariance();
    }

    public double getStandardDeviation() {
        return this.statistics.getStandardDeviation();
    }

    public void clear() {
        this.statistics.clear();
        this.samples.clear();
    }

    public void add(double x) {
        this.statistics.add(x);
        this.samples.add(x);
        if (this.samples.size() > this.capacity) {
            this.statistics.remove((Double)this.samples.remove());
        }

    }
}
