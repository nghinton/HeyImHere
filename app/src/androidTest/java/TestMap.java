import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.heyimhere.MapActivity;
import com.example.heyimhere.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestMap {


    @Rule
    public ActivityTestRule<MapActivity> activityRule
            = new ActivityTestRule<>(
            MapActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False to customize the intent


    @Test
    public void intent() throws InterruptedException {

        //start intent
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        //input a test string
        onView(withId(R.id.places_autocomplete_edit_text))
                .perform(typeText("Tuscaloosa"));
        Espresso.pressBack();

        //choose first result and submit
        onView(withId(R.id.places_autocomplete_list)).perform(click());
        onView(withId(R.id.selectButton)).perform(click());

        //end activity and get results
        activityRule.finishActivity();
        Instrumentation.ActivityResult r = activityRule.getActivityResult();

        //check returned results with known values
        assertEquals(r.getResultData().getDoubleExtra("latitude", 0), 33.32276529999999);
        assertEquals(r.getResultData().getDoubleExtra("longitude", 0), -87.460397);

    }




}

