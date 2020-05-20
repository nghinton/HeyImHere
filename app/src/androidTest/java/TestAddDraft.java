import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.heyimhere.AddContactActivity;
import com.example.heyimhere.AddDraftActivity;
import com.example.heyimhere.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestAddDraft {


    @Rule
    public ActivityTestRule<AddDraftActivity> activityRule
            = new ActivityTestRule<>(
            AddDraftActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False to customize the intent


    @Test
    public void intent() throws InterruptedException {

        //start intent
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        //input a test string
        onView(withId(R.id.txtEditNumber))
                .perform(typeText("1234567890"));
        Espresso.pressBack();

        onView(withId(R.id.txtEditMessage))
                .perform(typeText("Sample draft message"));
        Espresso.pressBack();

        //click save button
        onView(withId(R.id.btnSave)).perform(click());

        //end activity and get results
        activityRule.finishActivity();
        Instrumentation.ActivityResult r = activityRule.getActivityResult();

        //check returned results with expected values
        assertEquals(r.getResultData().getStringExtra("DraftNumber"), "1234567890");
        assertEquals(r.getResultData().getStringExtra("DraftMessage"), "Sample draft message");

    }




}

