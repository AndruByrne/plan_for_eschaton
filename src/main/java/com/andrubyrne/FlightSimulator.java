package com.andrubyrne;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Range;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.BlockingObservable;
import rx.observables.GroupedObservable;

public class FlightSimulator {
    private static final ImmutableSortedSet<Integer> TIME = ContiguousSet
            .create(Range.closedOpen(0, 10000), DiscreteDomain.integers());

    public static Observable<Integer> checkForSurvival(final int[] accelerations, final Topography topography) {
        return Observable
                .from(TIME)
                .groupBy(new Func1<Integer, Integer>() { //position
                    @Override
                    public Integer call(final Integer time) {
                        return BlockingObservable.from(Observable // position at time
                                .from(TIME.subSet(0, true, time, true))
                                .scan(0, new Func2<Integer, Integer, Integer>() {
                                    @Override
                                    public Integer call(Integer accum, Integer now) {
                                        return accum + BlockingObservable.from(Observable
                                                .from(TIME.subSet(0, true, now, true))
                                                .scan(0, new Func2<Integer, Integer, Integer>() {
                                                    @Override
                                                    public Integer call(Integer accum, Integer now) {
                                                        return accum + accelerations[now];
                                                    }
                                                })).last();
                                    }
                                })).last();
                    }
                })
                .serialize()
                .flatMap(new Func1<GroupedObservable<Integer, Integer>, Observable<Integer>>() {
                             @Override
                             public Observable<Integer> call(GroupedObservable<Integer, Integer> timesGroupedByPosition) {
                                 final Integer position = timesGroupedByPosition.getKey();
                                 return timesGroupedByPosition
                                         .scan(0, new Func2<Integer, Integer, Integer>() {
                                             @Override
                                             public Integer call(Integer fate, Integer time) {
                                                 if (position < 0) {
                                                     return -time;//Hit Eschaton
                                                 }
                                                 int blast = ((int) time / topography.tPerBlastMove) - 1;//pending Blast
                                                 if (blast >= position)
                                                     return -time; //Blast!
                                                 AsteroidData asteroidData = topography.asteroids.get(position);
                                                 int asteroids = (time //asteroid collision
                                                         + asteroidData.getOffset())
                                                         % asteroidData.getTPerCycle();
                                                 if (asteroids == 0 )//Pow!
                                                     return -time;
                                                 if (position > topography.asteroids.size())
                                                     return time;
                                                 return 0;
                                             }
                                         })
                                         .filter(new Func1<Integer, Boolean>() {
                                                     @Override
                                                     public Boolean call(Integer fate) {
                                                         if (fate != 0) return true;
                                                         else return false;
                                                     }
                                                 }
                                         );
                             }
                         }
                );
    }

}
