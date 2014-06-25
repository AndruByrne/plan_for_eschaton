package com.andrubyrne;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class Topography {
    @SerializedName("t_per_blast_move") public final int tPerBlastMove;
    @SerializedName("asteroids") public final List<AsteroidData> asteroids;

    public Topography(int tPerBlastMove, List<AsteroidData> asteroids) {
        this.tPerBlastMove = tPerBlastMove;
        this.asteroids = asteroids;
    }

    public void addEschatonAsteroidHack() {
        asteroids.add(0, getDummy(0)); //creates an asteroid at position 0 that cannot be witnessed
    }

    public static AsteroidData getDummy(Integer garbage) {
        return new AsteroidData(-1000, 8000); //creates a dummy asteroid at a position  that cannot be witnessed
    }
}
