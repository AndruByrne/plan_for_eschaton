package com.andrubyrne;

//Written by Andrew Byrne GPLv3

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.BlockingObservable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EschatonActivity extends RoboActivity {

    public static final int SAFE_DISTANCE = 9000;
    public static final String ESCHATON_TAG = "EschatonTAG";

    @InjectView(R.id.bad_day_0) ImageView badDay0;
    @InjectView(R.id.bad_day_1) ImageView badDay1;
    @InjectView(R.id.bad_day_2) ImageView badDay2;
    @InjectView(R.id.bad_day_3) ImageView badDay3;
    @InjectView(R.id.bad_day_4) ImageView badDay4;
    @InjectView(R.id.bad_day_5) ImageView badDay5;
    @InjectView(R.id.bad_day_6) ImageView badDay6;
    @InjectView(R.id.bad_day_7) ImageView badDay7;
    @InjectView(R.id.bad_day_8) ImageView badDay8;
    @InjectView(R.id.bad_day_9) ImageView badDay9;
    @InjectView(R.id.bad_day_10) ImageView badDay10;
    @InjectView(R.id.bad_day_11) ImageView badDay11;
    @InjectView(R.id.bad_day_12) ImageView badDay12;
    @InjectView(R.id.bad_day_13) ImageView badDay13;
    @InjectView(R.id.bad_day_14) ImageView badDay14;
    @InjectView(R.id.bad_day_15) ImageView badDay15;
    @InjectView(R.id.bad_day_16) ImageView badDay16;
    @InjectView(R.id.good_day) ImageView goodDay;
    @InjectView(R.id.shortest_time) TextView shortestTime;
    @InjectView(R.id.longest_life) TextView longestLife;

    private Topography topography;
    private Gson gson = new Gson();
    private Random rand = new Random();
    private Random rand_0 = new Random();
    private Map<Integer, int[]> sucessStories = new TreeMap<Integer, int[]>();
    final private ConcurrentLinkedQueue<int[]> badPathsQueue = new ConcurrentLinkedQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eschaton);
        try {
            topography = gson.fromJson(
                    new JsonReader(new InputStreamReader(getAssets().open("topography.json"), "UTF-8")),
                    Topography.class);
            topography.addEschatonAsteroidHack();
            Toast.makeText(this, "Asteroid Data Loaded!", Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Poorly Formed Asteroid Data!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Problems Loading Asteroid Data!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        Toast.makeText(this, "Testing Simulator...", Toast.LENGTH_SHORT).show();
        passSimulatorTestsThenEnableSimulation();
    }

    private void passSimulatorTestsThenEnableSimulation() {
        final boolean[] simulatorReady = {false};
        //can die from blast
        FlightSimulator.checkForSurvival(new int[]{
                0, 0, 0, 0, 0, 0, 0, 0
        }, topography).first().filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                if (integer == -2) return true;
                else return false;
            }
        }).subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                //can die from flying backwards too far
                FlightSimulator.checkForSurvival(new int[]{
                        -1, 0, 0, 0, 0, 0, 0, 0
                }, topography).first().filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        if (integer == -1) return true;
                        else return false;
                    }
                }).subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        //can be hit by Asteroids
                        FlightSimulator.checkForSurvival(new int[]{
                                0, 1, 0, 0, 0, 0, 0, 0
                        }, topography).first().filter(new Func1<Integer, Boolean>() {
                            @Override
                            public Boolean call(Integer integer) {
                                if (integer == -2) return true;
                                else return false;
                            }
                        }).subscribe(
                                new Action1<Integer>() {
                                    @Override
                                    public void call(Integer integer) {
                                        Toast.makeText(EschatonActivity.this, "Simulator Ready", Toast.LENGTH_SHORT).show();
                                        simulatorReady[0] = true;
                                        findViewById(R.id.run_simulation).setVisibility(View.VISIBLE);
                                        //                                runSimulations(findViewById(R.id.run_simulation));
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        Toast.makeText(EschatonActivity.this, "Error in Simulator: " + throwable.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        if (simulatorReady[0] == false)
                                            Toast.makeText(EschatonActivity.this, "Failure in Simulator", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                });
            }
        });
    }

    public void runSimulations(View v) {
        try {
            getAssets().open("flight_plan.json");
        } catch (IOException e) {
            runFullSimulation();
        }
    }


    private void runFullSimulation() {
        findViewById(R.id.run_simulation).setVisibility(View.INVISIBLE);
        final FlightPathGolgi flightPathGolgi = new FlightPathGolgi();
        Observable
                .create(new Observable.OnSubscribe<int[]>() {
                    @Override
                    public void call(Subscriber<? super int[]> subscriber) {
                        try {
                            if (false == subscriber.isUnsubscribed()) {
                                while (flightPathGolgi.getFirst() != -1) {
                                    if (badPathsQueue.isEmpty()) flightPathGolgi.iterate();
                                    else flightPathGolgi.iterate(badPathsQueue);
                                    subscriber.onNext(flightPathGolgi.getDerivedFlightPath());
                                }
                            } else return;
                            if (false == subscriber.isUnsubscribed()) {
                                subscriber.onCompleted();
                            }
                        } catch (Throwable t) {
                            if (false == subscriber.isUnsubscribed()) {
                                subscriber.onError(t);
                            }
                        }
                    }
                })
                .filter(new Func1<int[], Boolean>() {
                    @Override
                    public Boolean call(int[] ints) {
                        if (ints[0] == -1)
                            return false;
                        else return true;
                    }
                })
                .parallel(new Func1<Observable<int[]>, Observable<Story>>() {
                    @Override
                    public Observable<Story> call(Observable<int[]> listObservable) {
                        return listObservable
                                .map(new Func1<int[], Story>() {
                                    @Override
                                    public Story call(int[] integers) {
                                        Integer time = BlockingObservable.from(FlightSimulator.checkForSurvival(integers, topography))
                                                .first();
                                        if (0 < time) return new Story(time, -1, integers);
                                        else return new Story(time, rand.nextInt(200000), integers);
                                    }
                                });
                    }
                })
                .filter(new Func1<Story, Boolean>() {
                    @Override
                    public Boolean call(Story story) {
                        if (18 > story.chance) return true;
                        if (199900 < story.chance) return true;
                        else return false;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Story>() {

                            private ImageView previousLitView = badDay0;

                            @Override
                            public void call(Story story) {
                                if (story.chance > -1) {
                                    switch (story.chance) {
                                        case 0:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            previousLitView = badDay0;
                                            badDay0.setVisibility(View.VISIBLE);
                                            break;
                                        case 1:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            previousLitView = badDay1;
                                            badDay1.setVisibility(View.VISIBLE);
                                            break;
                                        case 2:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay2.setVisibility(View.VISIBLE);
                                            previousLitView = badDay2;
                                            break;
                                        case 3:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay3.setVisibility(View.VISIBLE);
                                            previousLitView = badDay3;
                                            break;
                                        case 4:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay4.setVisibility(View.VISIBLE);
                                            previousLitView = badDay4;
                                            break;
                                        case 5:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay5.setVisibility(View.VISIBLE);
                                            previousLitView = badDay5;
                                            break;
                                        case 6:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay6.setVisibility(View.VISIBLE);
                                            previousLitView = badDay6;
                                            break;
                                        case 7:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay7.setVisibility(View.VISIBLE);
                                            previousLitView = badDay7;
                                            break;
                                        case 8:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay8.setVisibility(View.VISIBLE);
                                            previousLitView = badDay8;
                                            break;
                                        case 9:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay9.setVisibility(View.VISIBLE);
                                            previousLitView = badDay9;
                                            break;
                                        case 10:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay10.setVisibility(View.VISIBLE);
                                            previousLitView = badDay10;
                                            break;
                                        case 11:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay11.setVisibility(View.VISIBLE);
                                            previousLitView = badDay11;
                                            break;
                                        case 12:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay12.setVisibility(View.VISIBLE);
                                            previousLitView = badDay12;
                                            break;
                                        case 13:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay13.setVisibility(View.VISIBLE);
                                            previousLitView = badDay13;
                                            break;
                                        case 14:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay14.setVisibility(View.VISIBLE);
                                            previousLitView = badDay14;
                                            break;
                                        case 15:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay15.setVisibility(View.VISIBLE);
                                            previousLitView = badDay15;
                                            break;
                                        case 16:
                                            previousLitView.setVisibility(View.INVISIBLE);
                                            badDay16.setVisibility(View.VISIBLE);
                                            previousLitView = badDay16;
                                            break;
                                    }
                                    if (story.chance > 199900) {
                                        if (0==longestLife.getText().length()) longestLife.setText(""+Math.abs(story.time));
                                        else if (Integer.parseInt(longestLife.getText().toString()) < Math.abs(story.time));
                                        longestLife.setText("" + story.time);
                                    }
                                    else badPathsQueue
                                            .offer(Arrays.copyOfRange(story.integers, 0, Math.abs(story.time)));
                                } else {
                                    Log.i("EschatonTAG", "Good Route: " + story.integers);
                                    goodDay.setVisibility(View.VISIBLE);
                                    goodDay.setClickable(true);
                                    sucessStories.put(story.time, story.integers);
                                    if (0 == shortestTime.getText().length()) shortestTime.setText(""+story.time);
                                    else if (Integer.parseInt(shortestTime.getText().toString()) < story.time)
                                        shortestTime.setText(""+story.time);
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {

                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                findViewById(R.id.run_simulation).setVisibility(View.VISIBLE);
                                Log.i("EschatonTAG", "Routes investigated");
                                badDay16.setVisibility(View.VISIBLE);
                                badDay1.setVisibility(View.VISIBLE);
                                badDay4.setVisibility(View.VISIBLE);
                                badDay13.setVisibility(View.VISIBLE);

                                if (sucessStories.size() > 0) {
                                    goodDay.setVisibility(View.VISIBLE);
                                    sendNotice(gson.toJson(sucessStories));
                                }
                            }
                        }
                );
    }

    public void sendNotice(View v) {
        if (sucessStories.size() > 0)
            sendNotice(gson.toJson(sucessStories));
    }

    private void sendNotice(String json) {
        Intent email = new Intent(Intent.ACTION_SEND);

        email.setData(Uri.parse("mailto:"));
        email.setType("text/plain");
        email.putExtra(Intent.EXTRA_EMAIL, "anthropic.android@gmail.com");
        email.putExtra(Intent.EXTRA_SUBJECT, "Successful plans organised by time");
        email.putExtra(Intent.EXTRA_TEXT, json);

        try {
            startActivity(Intent.createChooser(email, "Send mail..."));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(EschatonActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private class Story {
        public final Integer time;
        public final int chance;
        public final int[] integers;

        public Story(final Integer time, final int chance, final int[] flightPath) {
            this.time = time;
            this.chance = chance;
            this.integers = flightPath;
        }
    }
}
