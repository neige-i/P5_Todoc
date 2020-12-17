package com.neige_i.todoc.view;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.todoc.R;
import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.neige_i.todoc.util.LiveDataTestUtil.awaitForValue;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.DATE_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.DATE_DESC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.PROJECT_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.PROJECT_NAME_DESC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.TASK_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.TASK_NAME_DESC;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaskViewModelTest {

    // ------------------------------------ TEST RULE VARIABLE -------------------------------------

    // Avoid error when MutableLiveData.setValue() is called in source code
    @Rule
    public final InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private TaskViewModel taskViewModel;

    @Mock
    private TaskRepository mockTaskRepository;
    @Mock
    private Handler mockHandler;
    @NonNull
    private final Clock clock = Clock.fixed(
        ZonedDateTime.of(2020, 12, 4, 15, 7, 45, 345, ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );

    // --------------------------------------- SETUP METHODS ---------------------------------------

    @Before
    public void setUp() throws InterruptedException {
        // Init mock BEFORE init ViewModel in THAT ORDER
        doReturn(new MutableLiveData<>()).when(mockTaskRepository).getProjects();
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(mockHandler).post(any(Runnable.class));

        taskViewModel = new TaskViewModel(mockTaskRepository, clock, mockHandler);

        // Wait for LiveData value to be available
        awaitForValue(taskViewModel.getUiState());
    }

    // --------------------------------------- TEST METHODS ----------------------------------------

    @Test
    public void testDefaultSort() {
        // getProjects() is called inside ViewModel's constructor
        verify(mockTaskRepository).getProjects();

        // As OrderBy.NONE is set in the ViewModel's constructor, the default getTasks() method is also called
        verify(mockTaskRepository).getTasks();
    }

    @Test
    public void testSortByNameAsc() {
        // When: sort tasks by their name in ascending order
        taskViewModel.setSortType(TASK_NAME_ASC);

        // Then: the appropriate repository method is called
        verify(mockTaskRepository).getTasksByNameAsc();
    }

    @Test
    public void testSortByNameDesc() {
        // When: sort tasks by their name in descending order
        taskViewModel.setSortType(TASK_NAME_DESC);

        // Then: the appropriate repository method is called
        verify(mockTaskRepository).getTasksByNameDesc();
    }

    @Test
    public void testSortByProjectNameAsc() {
        // When: sort tasks by their project's name in ascending order
        taskViewModel.setSortType(PROJECT_NAME_ASC);

        // Then: the appropriate repository method is called
        verify(mockTaskRepository).getTasksByProjectNameAsc();
    }

    @Test
    public void testSortByProjectNameDesc() {
        // When: sort tasks by their project name in descending order
        taskViewModel.setSortType(PROJECT_NAME_DESC);

        // Then: the appropriate repository method is called
        verify(mockTaskRepository).getTasksByProjectNameDesc();
    }

    @Test
    public void testSortByDateAsc() {
        // When: sort tasks by their date in ascending order
        taskViewModel.setSortType(DATE_ASC);

        // Then: the appropriate repository method is called
        verify(mockTaskRepository).getTasksByDateAsc();
    }

    @Test
    public void testSortByDateDesc() {
        // When: sort tasks by their date in descending order
        taskViewModel.setSortType(DATE_DESC);

        // Then: the appropriate repository method is called
        verify(mockTaskRepository).getTasksByDateDesc();
    }

    @Test
    public void testRemoveTask() {
        // When: remove the task with ID 3L
        taskViewModel.removeTask(3L);

        // Then: the appropriate repository method is called with the correct argument
        verify(mockTaskRepository).deleteTask(3L);
    }

    @Test
    public void testCheckTaskEmpty() throws InterruptedException {
        // When: check an empty task
        taskViewModel.checkTask("", 1L);

        // Then: getProjectById() is called but not addTask() and the SingleLiveDate is set
        verify(mockTaskRepository).getProjectById(1L);
        verify(mockTaskRepository, never()).addTask(any(Task.class));
        assertEquals(R.string.empty_task_name, awaitForValue(taskViewModel.getErrorMessageEvent()).intValue());
    }

    @Test
    public void testCheckTaskWithCorrectProject() {
        // Given: return a dummy project when getProjectById() is called with an existing ID
        final long existingId = 1;
        doReturn(new Project(1, "Project", 0)).when(mockTaskRepository).getProjectById(existingId);

        // When: check a task with an existing project ID
        taskViewModel.checkTask("Task", existingId);

        // Then: getProjectById() is called and also addTask() with the appropriate arguments
        verify(mockTaskRepository).getProjectById(existingId);
        verify(mockTaskRepository).addTask(new Task(existingId, "Task", clock.instant().toEpochMilli()));
    }

    @Test
    public void testCheckTaskWithIncorrectProject() {
        // Given: return a null project when getProjectById() is called with a missing ID
        final long missingId = -1;
        doReturn(null).when(mockTaskRepository).getProjectById(missingId); // ASKME: not mocked automatically returns null

        // When: check a task with a missing project ID
        taskViewModel.checkTask("Task", missingId);

        // Then: getProjectById() is called but not addTask()
        verify(mockTaskRepository).getProjectById(missingId);
        verify(mockTaskRepository, never()).addTask(any(Task.class));
    }
}