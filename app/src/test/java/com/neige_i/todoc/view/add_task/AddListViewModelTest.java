package com.neige_i.todoc.view.add_task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.todoc.R;
import com.neige_i.todoc.data.database.TaskDao;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.util.DefaultProjects;
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
import java.util.List;

import static com.neige_i.todoc.util.LiveDataTestUtil.awaitForValue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddListViewModelTest {

    // ------------------------------------ TEST RULE VARIABLE -------------------------------------

    // Avoid error when MutableLiveData.setValue() is called in source code
    @Rule
    public final InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private AddTaskViewModel addTaskViewModel;

    private TaskRepository taskRepository;

    @Mock
    private TaskDao mockTaskDao;

    @NonNull
    private final Clock clock = Clock.fixed(
        ZonedDateTime.of(2020, 12, 4, 15, 7, 45, 345, ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );

    private final MutableLiveData<List<Project>> projectsMutableLiveData = new MutableLiveData<>();
    private final Project selectedProject = DefaultProjects.getList().get(0);

    // --------------------------------------- SETUP METHODS ---------------------------------------

    @Before
    public void setUp() {
        // Init mocks
        doReturn(projectsMutableLiveData).when(mockTaskDao).getAllProjects();
        doReturn(selectedProject).when(mockTaskDao).getProjectById(1);

        taskRepository = Mockito.spy(new TaskRepository(mockTaskDao, new SynchronousExecutorService()));

        // Init ViewModel
        addTaskViewModel = new AddTaskViewModel(taskRepository, clock);
    }

    // --------------------------------------- TEST METHODS ----------------------------------------

    @Test
    public void getProjectList() throws InterruptedException {
        // Given:
        projectsMutableLiveData.setValue(DefaultProjects.getList());

        // When:
        final List<Project> projectList = awaitForValue(addTaskViewModel.getProjectList());

        // Then:
        assertEquals(DefaultProjects.getList(), projectList);

        verify(taskRepository).getProjects();
        verify(mockTaskDao).getAllProjects();
    }

    @Test
    public void addNamedTask() throws InterruptedException {
        // Given
        addTaskViewModel.onTaskNameChanged("Do chores");
        addTaskViewModel.onProjectSelected(selectedProject);

        // When
        addTaskViewModel.onPositiveButtonClicked();
        awaitForValue(addTaskViewModel.getDismissDialogEvent());

        // Then
        verify(taskRepository).getProjectById(eq(1L), any());
        verify(mockTaskDao).getProjectById(eq(1L));
        verify(taskRepository).addTask(new Task(1, "Do chores", Instant.now(clock).toEpochMilli()));
    }

    @Test
    public void addEmptyNamedTask() throws InterruptedException {
        addWronglyNamedTask("     ");
    }

    @Test
    public void addNullNamedTask() throws InterruptedException {
        addWronglyNamedTask(null);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private void addWronglyNamedTask(@Nullable String taskName) throws InterruptedException {
        // Given
        if (taskName != null)
            addTaskViewModel.onTaskNameChanged(taskName);
        addTaskViewModel.onProjectSelected(selectedProject);

        // When
        addTaskViewModel.onPositiveButtonClicked();
        int errorMessageRes = awaitForValue(addTaskViewModel.getErrorMessageEvent());

        // Then
        assertEquals(R.string.empty_task_name, errorMessageRes);

        verify(taskRepository, never()).getProjectById(anyLong(), any());
        verify(taskRepository, never()).addTask(any());
    }
}