package com.neige_i.todoc;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.neige_i.todoc.view.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.neige_i.todoc.TestUtils.withRecyclerView;
import static org.hamcrest.Matchers.equalTo;
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
//    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addAndRemoveTask() {
//        MainActivity activity = rule.getActivity();
        final MainActivity[] mainActivity = new MainActivity[1];
        rule.getScenario().onActivity(activity -> mainActivity[0] = activity);
        TextView lblNoTask = mainActivity[0].findViewById(R.id.lbl_no_task);
        RecyclerView listTasks = mainActivity[0].findViewById(R.id.list_task);

        onView(withId(R.id.fab_add_task)).perform(click());
        onView(withId(R.id.input_task_name)).perform(replaceText("Task example"));
        onView(withId(android.R.id.button1)).perform(click());

        // Check that lblTask is not displayed anymore
        assertThat(lblNoTask.getVisibility(), equalTo(View.GONE));
        // Check that recyclerView is displayed
        assertThat(listTasks.getVisibility(), equalTo(View.VISIBLE));
        // Check that it contains one element only
        assertThat(listTasks.getAdapter().getItemCount(), equalTo(1));

        onView(withId(R.id.img_delete)).perform(click());

        // Check that lblTask is displayed
        assertThat(lblNoTask.getVisibility(), equalTo(View.VISIBLE));
    }

    @Test
    public void sortTasks() {
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

        // Sort alphabetical
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical)).perform(click());
        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));

        // Sort alphabetical inverted
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_alphabetical_invert)).perform(click());
        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));

        // Sort old first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_oldest_first)).perform(click());
        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));

        // Sort recent first
        onView(withId(R.id.action_filter)).perform(click());
        onView(withText(R.string.sort_recent_first)).perform(click());
        onView(withRecyclerView(R.id.list_task).atPositionOnView(0, R.id.lbl_task_name))
            .check(matches(withText("hhh Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(1, R.id.lbl_task_name))
            .check(matches(withText("zzz Task example")));
        onView(withRecyclerView(R.id.list_task).atPositionOnView(2, R.id.lbl_task_name))
            .check(matches(withText("aaa Task example")));
    }
}
