package com.neige_i.todoc.view;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.neige_i.todoc.util.LiveDataTestUtil.awaitForValue;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.DATE_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.DATE_DESC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.PROJECT_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.PROJECT_NAME_DESC;
import static com.neige_i.todoc.view.TaskViewModel.OrderBy.TASK_NAME_DESC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaskViewModelTest {

    private static final int PROJECTS_COUNT = 5;

    // ------------------------------------ TEST RULE VARIABLE -------------------------------------

    // Avoid error when MutableLiveData.setValue() is called in source code
    @Rule
    public final InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private TaskViewModel taskViewModel;

    @Mock
    private TaskRepository taskRepository;

    private MutableLiveData<List<Task>> tasksMutableLiveData;
    private MutableLiveData<List<Task>> tasksByNameAscMutableLiveData;
    private MutableLiveData<List<Project>> projectsMutableLiveData;

    // --------------------------------------- SETUP METHODS ---------------------------------------

    @Before
    public void setUp() {
        tasksMutableLiveData = new MutableLiveData<>();
        tasksByNameAscMutableLiveData = new MutableLiveData<>();
        projectsMutableLiveData = new MutableLiveData<>();

        Mockito.doReturn(tasksMutableLiveData).when(taskRepository).getTasks();
        Mockito.doReturn(tasksByNameAscMutableLiveData).when(taskRepository).getTasksByNameAsc();
        Mockito.doReturn(projectsMutableLiveData).when(taskRepository).getProjects();

        taskViewModel = new TaskViewModel(taskRepository);
    }

    // --------------------------------------- TEST METHODS ----------------------------------------

    @Test
    public void testDefaultSort() throws InterruptedException {
        // Given
        tasksMutableLiveData.setValue(new ArrayList<>());
        projectsMutableLiveData.setValue(getDefaultProjectList());

        // When
        MainUiModel uiState = awaitForValue(taskViewModel.getUiState());

        // Then
        // getProjects() is called inside ViewModel's constructor
        verify(taskRepository).getProjects();

        // As OrderBy.NONE is set in the ViewModel's constructor, the default getTasks() method is also called
        verify(taskRepository).getTasks();

        assertEquals(0, uiState.getTaskUiModels().size());
        assertTrue(uiState.isNoTaskVisible());
        assertEquals(PROJECTS_COUNT, uiState.getProjectList().size());
    }

    @Test
    public void testSortByNameAsc() throws InterruptedException {
        // Given
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(2, 1, "AZ", 777));
        tasks.add(new Task(1, 1, "ZA", 777));

        tasksByNameAscMutableLiveData.setValue(tasks);
        projectsMutableLiveData.setValue(getDefaultProjectList());

        // When
        taskViewModel.setSortType(TaskViewModel.OrderBy.TASK_NAME_ASC);
        MainUiModel uiState = awaitForValue(taskViewModel.getUiState());

        // Then
        // getProjects() is called inside ViewModel's constructor
        verify(taskRepository).getProjects();

        // As OrderBy.NONE is set in the ViewModel's constructor, the default getTasks() method is also called
        verify(taskRepository).getTasksByNameAsc();

        assertEquals(2, uiState.getTaskUiModels().size());
        assertFalse(uiState.isNoTaskVisible());
        assertEquals(PROJECTS_COUNT, uiState.getProjectList().size());
    }

//    @Test
//    public void testSortByNameDesc() throws InterruptedException {
//        // When: sort tasks by their name in descending order
//        taskViewModel.setSortType(TASK_NAME_DESC);
//        awaitForValue(taskViewModel.getUiState());
//
//        // Then: the appropriate repository method is called
//        verify(taskRepository).getTasksByNameDesc();
//    }
//
//    @Test
//    public void testSortByProjectNameAsc() throws InterruptedException {
//        // When: sort tasks by their project's name in ascending order
//        taskViewModel.setSortType(PROJECT_NAME_ASC);
//        awaitForValue(taskViewModel.getUiState());
//
//        // Then: the appropriate repository method is called
//        verify(taskRepository).getTasksByProjectNameAsc();
//    }
//
//    @Test
//    public void testSortByProjectNameDesc() throws InterruptedException {
//        // When: sort tasks by their project name in descending order
//        taskViewModel.setSortType(PROJECT_NAME_DESC);
//        awaitForValue(taskViewModel.getUiState());
//
//        // Then: the appropriate repository method is called
//        verify(taskRepository).getTasksByProjectNameDesc();
//    }
//
//    @Test
//    public void testSortByDateAsc() throws InterruptedException {
//        // When: sort tasks by their date in ascending order
//        taskViewModel.setSortType(DATE_ASC);
//        awaitForValue(taskViewModel.getUiState());
//
//        // Then: the appropriate repository method is called
//        verify(taskRepository).getTasksByDateAsc();
//    }
//
//    @Test
//    public void testSortByDateDesc() throws InterruptedException {
//        // When: sort tasks by their date in descending order
//        taskViewModel.setSortType(DATE_DESC);
//        awaitForValue(taskViewModel.getUiState());
//
//        // Then: the appropriate repository method is called
//        verify(taskRepository).getTasksByDateDesc();
//    }
//
//    @Test
//    public void testRemoveTask() throws InterruptedException {
//        // When: remove the task with ID 3L
//        taskViewModel.removeTask(3L);
//        awaitForValue(taskViewModel.getUiState());
//
//        // Then: the appropriate repository method is called with the correct argument
//        verify(taskRepository).deleteTask(3L);
//    }

    private List<Project> getDefaultProjectList() {
        List<Project> projects = new ArrayList<>();

        for (int i = 0; i < PROJECTS_COUNT; i++) {
            projects.add(new Project(i, "Project " + i, i));
        }

        return projects;
    }
}