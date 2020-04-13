package com.example.android.architecture.blueprints.todoapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.local.ToDoDatabase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class TaskDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ToDoDatabase

    @Before
    fun initDB() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                ToDoDatabase::class.java).build()
    }

    @After
    fun closeDB() {
        database.close()
    }

    @Test
    fun insertTaskAndGetById() = runBlockingTest {

        val task = Task("some", "task")
        database.taskDao().insertTask(task)

        val dbTask = database.taskDao().getTaskById(taskId = task.id)
        assertThat(dbTask).isNotNull()
        assertThat(dbTask?.id).isEqualTo(task.id)
        assertThat(dbTask?.title).isEqualTo(task.title)
        assertThat(dbTask?.description).isEqualTo(task.description)
        assertThat(dbTask?.isCompleted).isEqualTo(task.isCompleted)
    }

    @Test
    fun updateTaskAndGetById() = runBlockingTest{
        // 1. Insert a task into the DAO.
        val task = Task("some", "task")
        database.taskDao().insertTask(task)

        database.taskDao().updateTask(task.copy(title = "a new valye y'all", isCompleted = true))
        val dbTask: Task? = database.taskDao().getTaskById(task.id)

        assertThat(dbTask).isNotNull()
        assertThat(dbTask?.id).isEqualTo(task.id)
        assertThat(dbTask?.title).isEqualTo("a new valye y'all")
        assertThat(dbTask?.description).isEqualTo(task.description)
        assertThat(dbTask?.isCompleted).isEqualTo(true)
        // 2. Update the task by creating a new task with the same ID but different attributes.

        // 3. Check that when you get the task by its ID, it has the updated values.
    }
}