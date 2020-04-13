package com.example.android.architecture.blueprints.todoapp.data.source.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.succeeded
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class TasksLocalDataSourceTest {

    private lateinit var localDataSource: TasksLocalDataSource
    private lateinit var database: ToDoDatabase

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        // Using an in-memory database for testing, because it doesn't survive killing the process.
        database = Room.inMemoryDatabaseBuilder(
                        ApplicationProvider.getApplicationContext(),
                        ToDoDatabase::class.java
                )
                .allowMainThreadQueries()
                .build()

        localDataSource =
                TasksLocalDataSource(
                        database.taskDao(),
                        Dispatchers.Main
                )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    // runBlocking is used here because of https://github.com/Kotlin/kotlinx.coroutines/issues/1204
    @Test
    fun saveTask_retrievesTask() = runBlocking {
        // GIVEN - A new task saved in the database.
        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)

        // WHEN  - Task retrieved by ID.
        val result = localDataSource.getTask(newTask.id)

        // THEN - Same task is returned.
        assertThat(result.succeeded).isTrue()
        result as Result.Success
        assertThat(result.data.title).isEqualTo("title")
        assertThat(result.data.description).isEqualTo("description")
        assertThat(result.data.isCompleted).isFalse()
    }

    @Test
    fun completeTask_retrievedTaskIsComplete() = runBlocking{
        // 1. Save a new active task in the local data source.

        val newTask = Task("title", "description", false)
        localDataSource.saveTask(newTask)
        localDataSource.completeTask(newTask)

        // WHEN  - Task retrieved by ID.
        val result = localDataSource.getTask(newTask.id)
        assertThat(result.succeeded).isTrue()
        result as Result.Success
        assertThat(result.data.id).isEqualTo(newTask.id)
        assertThat(result.data.isCompleted).isTrue()
        // 2. Mark it as complete.
        // 3. Check that the task can be retrieved from the local data source and is complete.
    }

}