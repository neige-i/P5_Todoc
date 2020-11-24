package com.neige_i.todoc;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.view.MainActivity;

import org.junit.Before;
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
import static com.neige_i.todoc.TestUtils.withRecyclerView;
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

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // ASKME: use another DB
        // Clear all tasks before running UI tests
        rule.getScenario().onActivity(activity -> new TaskRepository(activity.getApplication()).deleteAllTasks());
    }

    @Test
    public void addAndRemoveTask() {
        // When: click on the FAB, type a text inside the TextInputEditText and click on the positive button
        onView(withId(R.id.fab_add_task)).perform(click());
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

    @Test
    public void sortTasks() {
        // Given: add 3 tasks
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("aaa Task example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("zzz Task example"));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("hhh Task example"));
        onView(withId(android.R.id.button1)).perform(click());

        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));

        // When: sort alphabetical
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));

        // When: sort alphabetical inverted
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical_invert)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));

        // When: sort old first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_oldest_first)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));

        // When: sort recent first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_recent_first)).perform(click());

        // Then: the list is sorted accordingly
        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
    }

    private ViewAssertion withItemCount(int count) {
        return (view, noViewFoundException) -> {
            if (noViewFoundException != null)
                throw noViewFoundException;

            assertThat(((RecyclerView) view).getAdapter().getItemCount(), is(count));
        };
    }
}
