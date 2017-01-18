package group22.travelstories;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewEntryAndDeleteEntryTest {

    @Rule
    public ActivityTestRule<DisplayStoryActivity> mActivityTestRule = new ActivityTestRule<>(DisplayStoryActivity.class);

    @Test
    public void newEntryAndDeleteEntryTest() {

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.add_entry), withContentDescription("Add"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.addEntry), withText("Add"), isDisplayed()));
        appCompatButton.perform(click());

        assertEquals(1, mActivityTestRule.getActivity().timeline.size());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.add_entry), withContentDescription("Add"), isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.addEntry), withText("Add"), isDisplayed()));
        appCompatButton2.perform(click());

        assertEquals(2, mActivityTestRule.getActivity().timeline.size());

        ViewInteraction actionMenuItemView3 = onView(
                allOf(withId(R.id.add_entry), withContentDescription("Add"), isDisplayed()));
        actionMenuItemView3.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.addEntry), withText("Add"), isDisplayed()));
        appCompatButton3.perform(click());

        assertEquals(3, mActivityTestRule.getActivity().timeline.size());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.rv), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(1, click()));

        ViewInteraction actionMenuItemView4 = onView(
                allOf(withId(R.id.delete_entry), withContentDescription("Add"), isDisplayed()));
        actionMenuItemView4.perform(click());

        assertEquals(2, mActivityTestRule.getActivity().timeline.size());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.rv), isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction actionMenuItemView5 = onView(
                allOf(withId(R.id.delete_entry), withContentDescription("Add"), isDisplayed()));
        actionMenuItemView5.perform(click());

        assertEquals(1, mActivityTestRule.getActivity().timeline.size());

    }

}
