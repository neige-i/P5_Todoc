package com.neige_i.todoc.view.add_task;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.neige_i.todoc.R;
import com.neige_i.todoc.data.database.TaskDao;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.util.SynchronousExecutorService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.neige_i.todoc.util.LiveDataTestUtil.awaitForValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddTaskViewModelTest {
    @Rule
    public final InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    private AddTaskViewModel addTaskViewModel;

    private TaskRepository taskRepository;

    @Mock
    private TaskDao mockTaskDao;

    @NonNull
    private final Clock clock = Clock.fixed(
        ZonedDateTime.of(2020, 12, 4, 15, 7, 45, 345, ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );

    // --------------------------------------- SETUP METHODS ---------------------------------------

    @Before
    public void setUp() {
        taskRepository = Mockito.spy(new TaskRepository(mockTaskDao, new SynchronousExecutorService()));
        addTaskViewModel = new AddTaskViewModel(taskRepository, clock);
    }

    @Test
    public void testCheckTaskEmpty() throws InterruptedException {
        // Given
        addTaskViewModel.onTaskNameChanged("");
        addTaskViewModel.onProjectSelected(new Project(1, "Tartampion", 0xFFEADAD1));

        // When
        addTaskViewModel.onPositiveButtonClicked();
        int errorMessageRes = awaitForValue(addTaskViewModel.getErrorMessageEvent());

        // Then
        assertEquals(R.string.empty_task_name, errorMessageRes);

        verify(taskRepository, never()).getProjectById(anyLong(), any());
    }

    @Test
    public void nominal_case() throws InterruptedException {
        // Given
        Mockito.doReturn(new Project(1, "Tartampion", 0xFFEADAD1)).when(mockTaskDao).getProjectById(1);

        addTaskViewModel.onTaskNameChanged("Do chores");
        addTaskViewModel.onProjectSelected(new Project(1, "Tartampion", 0xFFEADAD1));

        // When
        addTaskViewModel.onPositiveButtonClicked();
        awaitForValue(addTaskViewModel.getDismissDialogEvent());

        // Then
        verify(taskRepository).getProjectById(eq(1L), any());
        verify(mockTaskDao).getProjectById(eq(1L));
        verify(taskRepository).addTask(new Task(1, "Do chores", Instant.now(clock).toEpochMilli()));
    }
}