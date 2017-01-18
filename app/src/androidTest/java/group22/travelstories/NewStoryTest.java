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
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NewStoryTest {

    @Rule
    public ActivityTestRule<PreviousStoriesActivity> mActivityTestRule = new ActivityTestRule<>(PreviousStoriesActivity.class);

    @Test
    public void newStoryTest() {
        int existingStoriesCount = mActivityTestRule.getActivity().stories.size();

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.add_story), withContentDescription("addStory"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.trackToggle), withText("Let's go!"), isDisplayed()));
        toggleButton.perform(click());

        ViewInteraction toggleButton2 = onView(
                allOf(withId(R.id.trackToggle), withText("That's it!"), isDisplayed()));
        toggleButton2.perform(click());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.add_entry), withContentDescription("Add"), isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.editFromTime), withText("Enter a Starting Time"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        withParent(allOf(withClassName(is("com.android.internal.widget.ButtonBarLayout")),
                                withParent(withClassName(is("android.widget.LinearLayout"))))),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.editEndTime), withText("Enter a End Time"), isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        withParent(allOf(withClassName(is("com.android.internal.widget.ButtonBarLayout")),
                                withParent(withClassName(is("android.widget.LinearLayout"))))),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(R.id.editFromDate), withText("Enter a Starting Date"), isDisplayed()));
        appCompatTextView3.perform(click());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        withParent(allOf(withClassName(is("com.android.internal.widget.ButtonBarLayout")),
                                withParent(withClassName(is("android.widget.LinearLayout"))))),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction appCompatTextView4 = onView(
                allOf(withId(R.id.editEndDate), withText("Enter a End Date"), isDisplayed()));
        appCompatTextView4.perform(click());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        withParent(allOf(withClassName(is("com.android.internal.widget.ButtonBarLayout")),
                                withParent(withClassName(is("android.widget.LinearLayout"))))),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.addEntry), withText("Add"), isDisplayed()));
        appCompatButton5.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edittext_title),
                        withParent(allOf(withId(R.id.display_toolbar),
                                withParent(withId(R.id.activity_display_story)))),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edittext_title),
                        withParent(allOf(withId(R.id.display_toolbar),
                                withParent(withId(R.id.activity_display_story)))),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("test"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edittext_title), withText("test"),
                        withParent(allOf(withId(R.id.display_toolbar),
                                withParent(withId(R.id.activity_display_story)))),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Navigate up"),
                        withParent(allOf(withId(R.id.display_toolbar),
                                withParent(withId(R.id.activity_display_story)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        assertEquals(existingStoriesCount + 1, mActivityTestRule.getActivity().stories.size());
    }

}
