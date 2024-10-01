package com.learningapp;

import java.util.List;

public class KMeans {
    private int numClusters;
    private int maxIterations;

    public KMeans(int numClusters, int maxIterations) {
        this.numClusters = numClusters;
        this.maxIterations = maxIterations;
    }

    public void fit(List<float[]> data) {

    }

    public int predict(float[] dataPoint) {

        return 0;
    }
}