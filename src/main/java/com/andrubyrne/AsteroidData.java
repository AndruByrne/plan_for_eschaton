package com.andrubyrne;

import com.google.gson.annotations.SerializedName;

class AsteroidData {
    @SerializedName("offset") private int offset;
    @SerializedName("t_per_asteroid_cycle") private int tPerCycle;

    AsteroidData(int offset, int t_per_asteroid_cycle) {
        this.offset = offset;
        this.tPerCycle = t_per_asteroid_cycle;
    }

    public int getOffset() {
        return offset;
    }

    public int getTPerCycle() {
        return tPerCycle;
    }
}
