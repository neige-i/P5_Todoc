package com.neige_i.todoc.data.database;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.neige_i.todoc.data.model.Project;
import com.neige_i.todoc.data.model.Task;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static com.neige_i.todoc.util.LiveDataTestUtil.getValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
public class TaskDaoTest {

    private TaskDatabase taskDatabase;
    private TaskDao taskDao;

    private final Project project1 = new Project(5, "Projet Tartampion", 0xFFEADAD1);
    private final Project project2 = new Project(6, "Projet Lucidia", 0xFFB4CDBA);
    private final Project project3 = new Project(7, "Projet Circus", 0xFFA3CED2);
    private final Task task1 = new Task(1, 5, "fff", 123);
    private final Task task2 = new Task(2, 6, "aaa", 124);
    private final Task task3 = new Task(3, 7, "ddd", 125);
    private final Task task4 = new Task(4, 6, "HHH", 126);
    private final Task task5 = new Task(5, 6, "hha", 127);

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDbAndAddInitialProjects() {
        taskDatabase = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), TaskDatabase.class).build();
        taskDao = taskDatabase.taskDao();

        taskDao.insert(project1);
        taskDao.insert(project2);
        taskDao.insert(project3);
    }

    @After
    public void closeDb() {
        taskDatabase.close();
    }

    @Test
    public void getTasksTest() throws InterruptedException {
        assertThat(getValue(taskDao.getAllTasks()), is(empty()));
    }

    @Test
    public void insertTasksTest() throws InterruptedException {
        // When: insert the tasks into the DB
        insert5Tasks();

        // Then: the task list equals the expected one
        assertThat(getValue(taskDao.getAllTasks()), is(Arrays.asList(task1, task2, task3, task4, task5)));
    }

    @Test
    public void deleteTasksTest() throws InterruptedException {
        // Given: insert the tasks into the DB
        insert5Tasks();

        // When: delete 3 tasks by their ID
        taskDao.delete(3);
        taskDao.delete(1);
        taskDao.delete(5);

        // Then: the task list contains the 2 remaining ones
        assertThat(getValue(taskDao.getAllTasks()), is(Arrays.asList(task2, task4)));
    }

    @Test
    public void clearTaskListTest() throws InterruptedException {
        // Given: insert the tasks into the DB
        insert5Tasks();

        // When: delete all the tasks
        taskDao.clearAllTasks();

        // Then: the task list is empty
        assertThat(getValue(taskDao.getAllTasks()), is(empty()));
    }

    @Test
    public void getTasksByNameAscTest() throws InterruptedException {
        // When: insert the tasks into the DB
        insert5Tasks();

        // Then: the task list equals the expected one
        assertThat(getValue(taskDao.getTasksByNameAsc()), is(Arrays.asList(task2, task3, task1, task5, task4)));
    }

    @Test
    public void getTasksByNameDescTest() throws InterruptedException {
        // When: insert the tasks into the DB
        insert5Tasks();

        // Then: the task list equals the expected one
        assertThat(getValue(taskDao.getTasksByNameDesc()), is(Arrays.asList(task4, task5, task1, task3, task2)));
    }

    @Test
    public void getTasksByProjectNameAscTest() throws InterruptedException {
        // When: insert the tasks into the DB
        insert5Tasks();

        // Then: the task list equals the expected one
        assertThat(getValue(taskDao.getTasksByProjectNameAsc()), is(Arrays.asList(task3, task2, task5, task4, task1)));
    }

    @Test
    public void getTasksByProjectNameDescTest() throws InterruptedException {
        // When: insert the tasks into the DB
        insert5Tasks();

        // Then: the task list equals the expected one
        assertThat(getValue(taskDao.getTasksByProjectNameDesc()), is(Arrays.asList(task1, task2, task5, task4, task3)));
    }

    @Test
    public void getTasksByDateAscTest() throws InterruptedException {
        // When: insert the tasks into the DB
        insert5Tasks();

        // Then: the task list equals the expected one
        assertThat(getValue(taskDao.getTasksByDateAsc()), is(Arrays.asList(task1, task2, task3, task4, task5)));
    }

    @Test
    public void getTasksByDateDescTest() throws InterruptedException {
        // When: insert the tasks into the DB
        insert5Tasks();

        // Then: the task list equals the expected one
        assertThat(getValue(taskDao.getTasksByDateDesc()), is(Arrays.asList(task5, task4, task3, task2, task1)));
    }

    @Test
    public void getProjectsTest() throws InterruptedException {
        assertThat(getValue(taskDao.getAllProjects()), is(Arrays.asList(project1, project2, project3)));
    }

    @Test
    public void getProjectByIdTest() throws InterruptedException {
        assertThat(getValue(taskDao.getProjectById(5)), is(project1));
        assertThat(getValue(taskDao.getProjectById(6)), is(project2));
        assertThat(getValue(taskDao.getProjectById(7)), is(project3));
    }

    @Test
    public void getProjectByNameTest() throws InterruptedException {
        assertThat(getValue(taskDao.getProjectByName("Projet Tartampion")), is(project1));
        assertThat(getValue(taskDao.getProjectByName("Projet Lucidia")), is(project2));
        assertThat(getValue(taskDao.getProjectByName("Projet Circus")), is(project3));
    }

    private void insert5Tasks() {
        taskDao.insert(task1);
        taskDao.insert(task2);
        taskDao.insert(task3);
        taskDao.insert(task4);
        taskDao.insert(task5);
    }
}
