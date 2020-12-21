package com.neige_i.todoc.view.list_task;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;
import com.neige_i.todoc.data.repository.TaskRepository;
import com.neige_i.todoc.util.DefaultProjects;
import com.neige_i.todoc.view.list_task.ListUiModel;
import com.neige_i.todoc.view.list_task.ListViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.neige_i.todoc.util.LiveDataTestUtil.awaitForValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ListViewModelTest {

    // ------------------------------------ TEST RULE VARIABLE -------------------------------------

    // Avoid error when MutableLiveData.setValue() is called in source code
    @Rule
    public final InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    // ------------------------------------- OBJECT UNDER TEST -------------------------------------

    private ListViewModel listViewModel;

    @Mock
    private TaskRepository mockTaskRepository;

    private final MutableLiveData<List<Task>> tasksMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Project>> projectsMutableLiveData = new MutableLiveData<>();

    // --------------------------------------- SETUP METHODS ---------------------------------------

    @Before
    public void setUp() {
        // Init mock
        doReturn(tasksMutableLiveData).when(mockTaskRepository).getTasks();
        doReturn(tasksMutableLiveData).when(mockTaskRepository).getTasksByNameAsc();
        doReturn(tasksMutableLiveData).when(mockTaskRepository).getTasksByNameDesc();
        doReturn(tasksMutableLiveData).when(mockTaskRepository).getTasksByProjectNameAsc();
        doReturn(tasksMutableLiveData).when(mockTaskRepository).getTasksByProjectNameDesc();
        doReturn(tasksMutableLiveData).when(mockTaskRepository).getTasksByDateAsc();
        doReturn(tasksMutableLiveData).when(mockTaskRepository).getTasksByDateDesc();
        doReturn(projectsMutableLiveData).when(mockTaskRepository).getProjects();

        // Init ViewModel
        listViewModel = new ListViewModel(mockTaskRepository);
    }

    // --------------------------------------- TEST METHODS ----------------------------------------

    @Test
    public void testDefaultSort() throws InterruptedException {
        // Given
        tasksMutableLiveData.setValue(new ArrayList<>());
        projectsMutableLiveData.setValue(DefaultProjects.getList());

        // When
        final ListUiModel uiState = awaitForValue(listViewModel.getUiState());

        // Then
        // getProjects() is called inside ViewModel's constructor
        verify(mockTaskRepository).getProjects();

        // As OrderBy.NONE is set in the ViewModel's constructor, the default getTasks() method is also called
        verify(mockTaskRepository).getTasks();

        assertEquals(0, uiState.getTaskUiModels().size());
        assertTrue(uiState.isNoTaskVisible());
    }

    @Test
    public void testSortByNameAsc() throws InterruptedException {
        testSorting(ListViewModel.OrderBy.TASK_NAME_ASC);
    }

    @Test
    public void testSortByNameDesc() throws InterruptedException {
        testSorting(ListViewModel.OrderBy.TASK_NAME_DESC);
    }

    @Test
    public void testSortByProjectNameAsc() throws InterruptedException {
        testSorting(ListViewModel.OrderBy.PROJECT_NAME_ASC);
    }

    @Test
    public void testSortByProjectNameDesc() throws InterruptedException {
        testSorting(ListViewModel.OrderBy.PROJECT_NAME_DESC);
    }

    @Test
    public void testSortByDateAsc() throws InterruptedException {
        testSorting(ListViewModel.OrderBy.DATE_ASC);
    }

    @Test
    public void testSortByDateDesc() throws InterruptedException {
        testSorting(ListViewModel.OrderBy.DATE_DESC);
    }

    @Test
    public void testRemoveTask() {
        // When
        listViewModel.onTaskRemoved(1);

        // Then
        verify(mockTaskRepository).getProjects();
        verify(mockTaskRepository).deleteTask(1);
    }

    // --------------------------------------- UTIL METHODS ----------------------------------------

    private List<Task> getDefaultTaskList() {
        return Arrays.asList(new Task(2, 1, "AZ", 777), new Task(1, 1, "ZA", 777));
    }

    private void testSorting(ListViewModel.OrderBy orderBy) throws InterruptedException {
        // Given
        tasksMutableLiveData.setValue(getDefaultTaskList());
        projectsMutableLiveData.setValue(DefaultProjects.getList());

        // When
        listViewModel.onSortingSelected(orderBy);
        final ListUiModel uiState = awaitForValue(listViewModel.getUiState());

        // Then
        verify(mockTaskRepository).getProjects();
        verifyRepositoryMethodCall(orderBy);

        assertEquals(2, uiState.getTaskUiModels().size());
        assertFalse(uiState.isNoTaskVisible());
    }

    private void verifyRepositoryMethodCall(ListViewModel.OrderBy orderBy) {
        switch (orderBy) {
            case TASK_NAME_ASC:
                verify(mockTaskRepository).getTasksByNameAsc();
                break;
            case TASK_NAME_DESC:
                verify(mockTaskRepository).getTasksByNameDesc();
                break;
            case PROJECT_NAME_ASC:
                verify(mockTaskRepository).getTasksByProjectNameAsc();
                break;
            case PROJECT_NAME_DESC:
                verify(mockTaskRepository).getTasksByProjectNameDesc();
                break;
            case DATE_ASC:
                verify(mockTaskRepository).getTasksByDateAsc();
                break;
            case DATE_DESC:
                verify(mockTaskRepository).getTasksByDateDesc();
                break;
        }
    }
}