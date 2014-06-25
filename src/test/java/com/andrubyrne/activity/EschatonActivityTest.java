package com.andrubyrne.activity;

import com.andrubyrne.EschatonActivity;
import com.andrubyrne.R;
import com.andrubyrne.support.EschatonTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.util.ActivityController;

import static org.fest.assertions.api.ANDROID.assertThat;

@RunWith(EschatonTestRunner.class)
public class EschatonActivityTest {

    private EschatonActivity subject;
    private ActivityController<EschatonActivity> activityController;

    @Before
    public void setUp() throws Exception {
        activityController = Robolectric.buildActivity(EschatonActivity.class);
        subject = activityController.create().resume().get();
    }

    @Test
    public void runWithOutFlightPlan() {
        activityController.start().visible();

        assertThat(subject.findViewById(R.id.bad_day_0)).isNotVisible();

        subject.runSimulations(subject.findViewById(R.id.bad_day_0));

    }

    @Test
    public void sendAnEmail() {
        activityController.start().visible();

        subject.sendNotice(subject.findViewById(R.id.good_day));
    }
}