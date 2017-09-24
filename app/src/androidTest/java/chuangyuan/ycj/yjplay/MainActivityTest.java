package chuangyuan.ycj.yjplay;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by yangc on 2017/9/16.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest   {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class);



    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    @Test
    public void onCreate() throws Exception {
       // onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.button2)).perform(click());

    }



}