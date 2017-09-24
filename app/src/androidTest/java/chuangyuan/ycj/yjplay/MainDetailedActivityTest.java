package chuangyuan.ycj.yjplay;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

/**
 * Created by yangc on 2017/9/16.
 * E-Mail:yangchaojiang@outlook.com
 * Deprecated:
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainDetailedActivityTest {

    @Rule
    public ActivityTestRule<MainDetailedActivity> mActivityRule = new ActivityTestRule<>(
            MainDetailedActivity.class);

    @Test
    public void onCreate() throws Exception {
    }


    @Test
    public void onResume() throws Exception {

    }

    @Test
    public void onPause() throws Exception {

    }

    @Test
    public void onDestroy() throws Exception {

    }

    @Test
    public void onConfigurationChanged() throws Exception {

    }

    @Test
    public void onBackPressed() throws Exception {

    }

    @Test
    public void onPictureInPictureModeChanged() throws Exception {

    }

}