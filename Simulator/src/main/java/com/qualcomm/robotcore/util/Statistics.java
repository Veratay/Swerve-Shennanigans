//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.qualcomm.robotcore.util;

public class Statistics {
    int n;
    double mean;
    double m2;

    public Statistics() {
        this.clear();
    }

    public int getCount() {
        return this.n;
    }

    public double getMean() {
        return this.mean;
    }

    public double getVariance() {
        return this.m2 / (double)(this.n - 1);
    }

    public double getStandardDeviation() {
        return Math.sqrt(this.getVariance());
    }

    public void clear() {
        this.n = 0;
        this.mean = 0.0D;
        this.m2 = 0.0D;
    }

    public void add(double x) {
        ++this.n;
        double delta = x - this.mean;
        this.mean += delta / (double)this.n;
        this.m2 += delta * (x - this.mean);
    }

    public void remove(double x) {
        int nPrev = this.n - 1;
        if (nPrev == 0) {
            this.clear();
        } else {
            double delta = x - this.mean;
            double deltaPrev = (double)this.n * delta / (double)nPrev;
            this.m2 -= deltaPrev * delta;
            this.mean = (this.mean * (double)this.n - x) / (double)nPrev;
            this.n = nPrev;
        }

    }
}
