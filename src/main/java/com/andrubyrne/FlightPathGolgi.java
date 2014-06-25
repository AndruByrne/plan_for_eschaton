package com.andrubyrne;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

class FlightPathGolgi {
    private int[] derivedFlightPath = new int[9000];

    public FlightPathGolgi() {
        createFirstPath();
    }

    public void iterate(ConcurrentLinkedQueue<int[]> badPathsQueue) {
        jumpOver(badPathsQueue.poll());
        badPathsQueue.clear();
    }

    public void iterate() {
        synchronized (derivedFlightPath) {
            minusOne();
        }
    }

    private void createFirstPath() {
        Arrays.fill(derivedFlightPath, 1);
    }

    private void minusOne() {
        for (int i = EschatonActivity.SAFE_DISTANCE - 1; i > -1; i--) {
            switch (derivedFlightPath[i]){
                case 0: derivedFlightPath[i] = -1;
                    return;
                case 1: derivedFlightPath[i] = 0;
                    return;
                case -1: derivedFlightPath[i] =1;
            }
        }
    }

    public Integer getFirst(){
        synchronized (derivedFlightPath){
            return derivedFlightPath[0];
        }
    }

    public void jumpOver(int[] badFlightPath) {
        synchronized (derivedFlightPath) {
            System.arraycopy(badFlightPath, 0, derivedFlightPath, 0, badFlightPath.length);
            Arrays.fill(derivedFlightPath, badFlightPath.length, EschatonActivity.SAFE_DISTANCE -1, -1);
            minusOne();
        }
    }

    public int[] getDerivedFlightPath() {
        synchronized (derivedFlightPath) {
            return derivedFlightPath;
        }
    }
}
