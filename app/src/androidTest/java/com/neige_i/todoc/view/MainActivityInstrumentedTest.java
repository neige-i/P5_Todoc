package com.neige_i.todoc.view;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.neige_i.todoc.R;
import com.neige_i.todoc.util.RecyclerViewMatcher;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @author Gaëtan HERFRAY
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    // ---------------------------------- ACTIVITY RULE VARIABLE -----------------------------------

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    // --------------------------------------- TEST METHODS ----------------------------------------

    @Test
    public void addAndRemoveTask() {
        // When: click on the FAB, type a text inside the TextInputEditText and click on the positive button
        onView(ViewMatchers.withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("Task example"));
        onView(withId(android.R.id.button1)).perform(click());

        // Then: the TextInputEditText is not displayed anymore and the list contains only 1 element
        onView(withId(R.id.input_task_name)).check(doesNotExist());
        onView(withId(R.id.list_task)).check(withItemCount(1));

        // When: click on the delete button
        onView(withId(R.id.img_delete)).perform(click());

        // Then: the list is empty and the 'empty state' label is displayed
        onView(withId(R.id.list_task)).check(withItemCount(0));
        onView(withId(R.id.lbl_no_task)).check(matches(isDisplayed()));
    }

    // ASKME: split method
    @Test
    public void sortTasks() {
        // Given: add 3 tasks
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("fff Task example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("aaa Task example"));
        onView(withId(R.id.input_project_name)).perform(click());
        onView(withText("Projet Lucidia")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("ddd Task example"));
        onView(withId(R.id.input_project_name)).perform(click());
        onView(withText("Projet Circus")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("HHH Task example"));
        onView(withId(R.id.input_project_name)).perform(click());
        onView(withText("Projet Lucidia")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("hha Task example"));
        onView(withId(R.id.input_project_name)).perform(click());
        onView(withText("Projet Lucidia")).inRoot(RootMatchers.isPlatformPopup()).perform(click());
        onView(withId(android.R.id.button1)).perform(click());

        // Check initial state, before sorting
        onView(withRecyclerView().atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("fff Task example")));
        onView(withRecyclerView().atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView().atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("ddd Task example")));
        onView(withRecyclerView().atPositionOnView(3, R.id.lbl_task_name))
            .check(matches(withText("HHH Task example")));
        onView(withRecyclerView().atPositionOnView(4, R.id.lbl_task_name))
            .check(matches(withText("hha Task example")));

        // When: sort task name alphabetical
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical_task)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView().atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView().atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("ddd Task example")));
        onView(withRecyclerView().atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("fff Task example")));
        onView(withRecyclerView().atPositionOnView(3, R.id.lbl_task_name))
            .check(matches(withText("hha Task example")));
        onView(withRecyclerView().atPositionOnView(4, R.id.lbl_task_name))
            .check(matches(withText("HHH Task example")));

        // When: sort task name alphabetical inverted
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical_invert_task)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView().atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("HHH Task example")));
        onView(withRecyclerView().atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("hha Task example")));
        onView(withRecyclerView().atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("fff Task example")));
        onView(withRecyclerView().atPositionOnView(3, R.id.lbl_task_name))
            .check(matches(withText("ddd Task example")));
        onView(withRecyclerView().atPositionOnView(4, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));

        // When: sort project name alphabetical
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical_project)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView().atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("ddd Task example")));
        onView(withRecyclerView().atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView().atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("hha Task example")));
        onView(withRecyclerView().atPositionOnView(3, R.id.lbl_task_name))
            .check(matches(withText("HHH Task example")));
        onView(withRecyclerView().atPositionOnView(4, R.id.lbl_task_name))
            .check(matches(withText("fff Task example")));

        // When: sort project name alphabetical inverted
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical_invert_project)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView().atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("fff Task example")));
        onView(withRecyclerView().atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView().atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("hha Task example")));
        onView(withRecyclerView().atPositionOnView(3, R.id.lbl_task_name))
            .check(matches(withText("HHH Task example")));
        onView(withRecyclerView().atPositionOnView(4, R.id.lbl_task_name))
            .check(matches(withText("ddd Task example")));

        // When: sort old first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_oldest_first)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView().atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("fff Task example")));
        onView(withRecyclerView().atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView().atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("ddd Task example")));
        onView(withRecyclerView().atPositionOnView(3, R.id.lbl_task_name))
            .check(matches(withText("HHH Task example")));
        onView(withRecyclerView().atPositionOnView(4, R.id.lbl_task_name))
            .check(matches(withText("hha Task example")));

        // When: sort recent first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_recent_first)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView().atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("hha Task example")));
        onView(withRecyclerView().atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("HHH Task example")));
        onView(withRecyclerView().atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("ddd Task example")));
        onView(withRecyclerView().atPositionOnView(3, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView().atPositionOnView(4, R.id.lbl_task_name))
            .check(matches(withText("fff Task example")));
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private ViewAssertion withItemCount(int count) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null)
                throw noViewFoundException;

            //noinspection ConstantConditions
            assertThat(((RecyclerView) view).getAdapter().getItemCount(), is(count));
        };
    }

    private RecyclerViewMatcher withRecyclerView() {
        return new RecyclerViewMatcher(R.id.list_task);
    }
}
