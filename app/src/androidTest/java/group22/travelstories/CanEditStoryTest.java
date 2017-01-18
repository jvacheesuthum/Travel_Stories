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
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CanEditStoryTest {

    @Rule
    public ActivityTestRule<DisplayStoryActivity> mActivityTestRule = new ActivityTestRule<>(DisplayStoryActivity.class);

    @Test
    public void canEditStoryTest() {

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

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.display_toolbar),
                                withParent(withId(R.id.activity_display_story)))),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction recyclerView = onView(
                allOf(withId(R.id.previous_stories_recycler), isDisplayed()));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        assertEquals(3, mActivityTestRule.getActivity().timeline.size());

        ViewInteraction recyclerView2 = onView(
                allOf(withId(R.id.rv), isDisplayed()));
        recyclerView2.perform(actionOnItemAtPosition(0, click()));

        ViewInteraction actionMenuItemView4 = onView(
                allOf(withId(R.id.delete_entry), withContentDescription("Add"), isDisplayed()));
        actionMenuItemView4.perform(click());

        assertEquals(3, mActivityTestRule.getActivity().timeline.size());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.display_toolbar),
                                withParent(withId(R.id.activity_display_story)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction recyclerView3 = onView(
                allOf(withId(R.id.previous_stories_recycler), isDisplayed()));
        recyclerView3.perform(actionOnItemAtPosition(0, click()));

        assertEquals(3, mActivityTestRule.getActivity().timeline.size());


    }

}
