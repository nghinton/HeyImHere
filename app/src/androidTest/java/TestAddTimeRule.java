import android.app.Instrumentation;
import android.content.Intent;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.heyimhere.AddTimeRuleActivity;
import com.example.heyimhere.R;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestAddTimeRule {

    TextView t;

    @Rule
    public ActivityTestRule<AddTimeRuleActivity> activityRule
            = new ActivityTestRule<>(
            AddTimeRuleActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False to customize the intent


    @Test
    public void intent() throws InterruptedException {


        //start intent
        Intent intent = new Intent();
        activityRule.launchActivity(intent);

        //input a date
        onView(withId(R.id.btnSetDate)).perform(click());
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName()))).perform(PickerActions.setDate(2020, 10, 10));
        onView(withText("OK")).perform(click());

        //input a time
        onView(withId(R.id.btnSetTime)).perform(click());
        onView(withClassName(Matchers.equalTo(TimePicker.class.getName()))).perform(PickerActions.setTime(12, 12));
        onView(withText("OK")).perform(click());

        //end activity and get results
        activityRule.finishActivity();
        Instrumentation.ActivityResult r = activityRule.getActivityResult();


        //check returned results with expected values
        //assertEquals((EditText) R.id.txtEditDate, "2131231097");

    }




}

