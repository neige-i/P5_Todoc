package com.neige_i.todoc.view;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.neige_i.todoc.data.repository.TaskRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.DATE_ASC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.DATE_DESC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.PROJECT_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.PROJECT_NAME_DESC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.TASK_NAME_ASC;
import static com.neige_i.todoc.view.TaskViewModel.ORDER_BY.TASK_NAME_DESC;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaskViewModelTest {

    // ------------------------------------ TEST RULE VARIABLE -------------------------------------

    // Avoid error when MutableLiveData.setValue() is called in source code
    @Rule
    public final InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    TaskViewModel taskViewModel;

    @Mock
    TaskRepository mockTaskRepository;

    Observer<MainUiModel> mainUiModelObserver;
//    Observer<Void> fakeLiveDataObserver;
//    Observer<List<Project>> projectListObserver;

    // -------------------------------- SETUP AND TEARDOWN METHODS ---------------------------------

    @Before
    public void setUp() {
        // Init ViewModel and observe LiveData forever
        taskViewModel = new TaskViewModel(mockTaskRepository);
        taskViewModel.getUiState().observeForever(mainUiModelObserver);
    }

    @After
    public void tearDown() {
        // Remove observers
        taskViewModel.getUiState().removeObserver(mainUiModelObserver);
    }

    // --------------------------------------- TEST METHODS ----------------------------------------

//    @Test
//    public void testProjectList() {
//        taskViewModel.getProjectList().observeForever(projectListObserver);
//        verify(mockTaskRepository).getProjects();
//        taskViewModel.getProjectList().removeObserver(projectListObserver);
//    }

    @Test
    public void testDefaultSort() {
        verify(mockTaskRepository).getTasks();
    }

    @Test
    public void testSortByNameAsc() {
        taskViewModel.setSortType(TASK_NAME_ASC);
        verify(mockTaskRepository).getTasksByNameAsc();
    }

    @Test
    public void testSortByNameDesc() {
        taskViewModel.setSortType(TASK_NAME_DESC);
        verify(mockTaskRepository).getTasksByNameDesc();
    }

    @Test
    public void testSortByProjectNameAsc() {
        taskViewModel.setSortType(PROJECT_NAME_ASC);
        verify(mockTaskRepository).getTasksByProjectNameAsc();
    }

    @Test
    public void testSortByProjectNameDesc() {
        taskViewModel.setSortType(PROJECT_NAME_DESC);
        verify(mockTaskRepository).getTasksByProjectNameDesc();
    }

    @Test
    public void testSortByDateAsc() {
        taskViewModel.setSortType(DATE_ASC);
        verify(mockTaskRepository).getTasksByDateAsc();
    }

    @Test
    public void testSortByDateDesc() {
        taskViewModel.setSortType(DATE_DESC);
        verify(mockTaskRepository).getTasksByDateDesc();
    }

    @Test
    public void testRemoveTask() {
        taskViewModel.removeTask(3L);
        verify(mockTaskRepository).deleteTask(3L);
    }

//    @Test
//    public void testCheckTask() {
//        taskViewModel.getFakeLiveData().observeForever(fakeLiveDataObserver);
//        taskViewModel.checkTask("task", 1);
//        verify(mockTaskRepository).getProjectById(1);
////        verify(mockTaskRepository).addTask(new Task(1, "task", 1));
//    }
}