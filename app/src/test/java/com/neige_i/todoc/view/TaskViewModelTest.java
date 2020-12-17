package com.neige_i.todoc.view;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.util.LiveDataTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.neige_i.todoc.view.TaskViewModel.OrderBy.DATE_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.DATE_DESC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.PROJECT_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.PROJECT_NAME_DESC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.TASK_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.TASK_NAME_DESC;
import static org.mockito.Mockito.doReturn;
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

    @NonNull
    private final Clock clock = Clock.fixed(
        ZonedDateTime.of(2020, 12, 4, 15, 7, 45, 345, ZoneId.systemDefault()).toInstant(),
        ZoneId.systemDefault()
    );

//    private MutableLiveData<List<Task>> tasksLiveData;
//    private MutableLiveData<List<Project>> projectsLiveData;

    // --------------------------------------- SETUP METHODS ---------------------------------------

    @Before
    public void setUp() throws InterruptedException {
        // Init mock BEFORE init ViewModel in THAT ORDER
//        tasksLiveData = new MutableLiveData<>();
//        projectsLiveData = new MutableLiveData<>();
//        doReturn(tasksLiveData).when(mockTaskRepository).getTasks();
        doReturn(/*projectsLiveData*/new MutableLiveData<>()).when(mockTaskRepository).getProjects();
//        doReturn(mock(Looper.class)).when()

        taskViewModel = new TaskViewModel(mockTaskRepository, clock);

        // Wait for LiveData value to be available
        LiveDataTestUtil.awaitForValue(taskViewModel.getUiState());
    }

    // --------------------------------------- TEST METHODS ----------------------------------------

    @Test
    public void testDefaultSort() {
        // Given:
//        tasksLiveData.setValue(new ArrayList<>()/*mock(List<Task>.class)*/);
//        projectsLiveData.setValue(new ArrayList<>()/*mock(List<Task>.class)*/);

        // Then:
        verify(mockTaskRepository).getProjects();
        verify(mockTaskRepository).getTasks();
    }

    @Test
    public void testSortByNameAsc() {
        // When:
        taskViewModel.setSortType(TASK_NAME_ASC);

        // Then:
        verify(mockTaskRepository).getTasksByNameAsc();
    }

    @Test
    public void testSortByNameDesc() {
        // When:
        taskViewModel.setSortType(TASK_NAME_DESC);

        // Then:
        verify(mockTaskRepository).getTasksByNameDesc();
    }

    @Test
    public void testSortByProjectNameAsc() {
        // When:
        taskViewModel.setSortType(PROJECT_NAME_ASC);

        // Then:
        verify(mockTaskRepository).getTasksByProjectNameAsc();
    }

    @Test
    public void testSortByProjectNameDesc() {
        // When:
        taskViewModel.setSortType(PROJECT_NAME_DESC);

        // Then:
        verify(mockTaskRepository).getTasksByProjectNameDesc();
    }

    @Test
    public void testSortByDateAsc() {
        // When:
        taskViewModel.setSortType(DATE_ASC);

        // Then:
        verify(mockTaskRepository).getTasksByDateAsc();
    }

    @Test
    public void testSortByDateDesc() {
        // When:
        taskViewModel.setSortType(DATE_DESC);

        // Then:
        verify(mockTaskRepository).getTasksByDateDesc();
    }

    @Test
    public void testRemoveTask() {
        // When:
        taskViewModel.removeTask(3L);

        // Then:
        verify(mockTaskRepository).deleteTask(3L);
    }

    @Test
    public void testCheckTask() {
        taskViewModel.checkTask("Task", 1L);

//        // Given:
//        MutableLiveData<Project> mutableLiveData = new MutableLiveData<>();
//        mutableLiveData.setValue(mock(Project.class));
//        doReturn(mutableLiveData).when(mockTaskRepository).getProjectById(1);
//
//        // When:
//        taskViewModel.checkTask("task", 1);
//
//
//        // Then:
//        verify(mockTaskRepository).getProjectById(1);
//        verify(mockTaskRepository).addTask(eq(new Task(1, "task", clock.instant().toEpochMilli())));
    }
}