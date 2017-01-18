package group22.travelstories;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UITest {

    private String mStringToBetyped;

    @Rule
    public ActivityTestRule<EditStoryActivity> mActivityRule = new ActivityTestRule<>(EditStoryActivity.class, true, false);

//    @Rule
//    public IntentsTestRule<EditStoryActivity> intentRule = new IntentsTestRule<>(EditStoryActivity.class);


    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("group22.travelstories", appContext.getPackageName());
    }

    @Before
    public void initValidString() {
        // Specify a valid string.
        mStringToBetyped = "EntryFormTitle";
    }

    @Test
    public void changeText_EditActivity() {
//        // Type text and then press the button.
//        onView(withId(R.id.locationname))
//                .perform(typeText(mStringToBetyped), closeSoftKeyboard());
//
//        // Check that the text was changed.
//        onView(withId(R.id.locationname))
//                .check(matches(withText(mStringToBetyped)));
        Intent intent = new Intent();

        Location l = new Location("");
        GregorianCalendar s = new GregorianCalendar(2017, 1, 1);
        GregorianCalendar e = new GregorianCalendar(2017, 1, 2);
        TimeLineEntry t = new TimeLineEntry(l, s, e);
        t.photos = new ArrayList<>();
        t.setAddress("Hello World");
        intent.putExtra("photos", t);
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.locationname))
                .perform(click())
                .perform(clearText())
                .perform(typeText(mStringToBetyped), closeSoftKeyboard());

        onView(withId(R.id.locationname))
                .check(matches(withText(mStringToBetyped)));
    }

    @Test
    public void appendText_EditActivity() {
        Intent intent = new Intent();

        Location l = new Location("");
        GregorianCalendar s = new GregorianCalendar(2017, 1, 1);
        GregorianCalendar e = new GregorianCalendar(2017, 1, 2);
        TimeLineEntry t = new TimeLineEntry(l, s, e);
        t.photos = new ArrayList<>();
        t.setAddress("Hello World");
        intent.putExtra("photos", t);
        mActivityRule.launchActivity(intent);

        onView(withId(R.id.locationname))
                .perform(click())
                .perform(typeText(mStringToBetyped), closeSoftKeyboard());

        onView(withId(R.id.locationname))
                .check(matches(withText("Hello World" + mStringToBetyped)));
    }

}
