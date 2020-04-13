package com.example.android.architecture.blueprints.todoapp.tasks

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.*
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.P])
class TasksViewModelTest {

    @get:Rule
    var instantExecutorRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var tasksViewModel: TasksViewModel

    private lateinit var tasksRepository: FakeRepository

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel() {
        tasksRepository = FakeRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        tasksRepository.addTasks(task1, task2, task3)

        tasksViewModel = TasksViewModel(tasksRepository)
    }

    @Test
    fun `add new task sets new task event`() {
        tasksViewModel.addNewTask()
        val value = tasksViewModel.newTaskEvent.getOrAwaitValue()
        assertThat(value.getContentIfNotHandled()) notEqualTo null
    }

    @Test
    fun `setFilterAllTasks tasksAddViewVisible returns true`() {
        // When the filter type is ALL_TASKS
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)
        // Then the "Add task" action is visible
        val visible = tasksViewModel.tasksAddViewVisible.getOrAwaitValue()
        assertThat(visible) equalTo true
    }

    @Test
    fun completeTask_dataAndSnackbarUpdated() {
        // Create an active task and add it to the repository.
        val task = Task("Title", "Description")
        tasksRepository.addTasks(task)

        // Mark the task as complete task.
        tasksViewModel.completeTask(task, true)

        // Verify the task is completed.
        assertThat(tasksRepository.tasksServiceData[task.id]?.isCompleted).isTrue()

        // Assert that the snackbar has been updated with the correct text.
        val snackbarText: Event<Int> = tasksViewModel.snackbarText.getOrAwaitValue()
        assertThat(snackbarText.getContentIfNotHandled()).isEqualTo(R.string.task_marked_complete)
    }

    @Test
    fun `filter tasks returns filtered list for all tasks`(): Unit = mainCoroutineRule.runBlockingTest {
        // Create an active task and add it to the repository.
        tasksViewModel.setFiltering(TasksFilterType.ALL_TASKS)
        val tasks = tasksRepository.observeTasks().getOrAwaitValue()
        val filtered = tasksViewModel.filterTasks(tasks).getOrAwaitValue()
        assertThat(tasks).isInstanceOf(Result.Success::class.java)
        tasks as Result.Success
        assertThat(tasks.data) equalTo filtered
    }

    @Test
    fun `filter tasks returns filtered list for active tasks`(): Unit = mainCoroutineRule.runBlockingTest {
        // Create an active task and add it to the repository.
        tasksViewModel.setFiltering(TasksFilterType.ACTIVE_TASKS)
        val tasks = tasksRepository.observeTasks().getOrAwaitValue()
        val filtered = tasksViewModel.filterTasks(tasks).getOrAwaitValue()
        assertThat(tasks).isInstanceOf(Result.Success::class.java)
        tasks as Result.Success
        assertThat(tasks.data) notEqualTo filtered
    }


    @Test
    fun `filter tasks returns filtered list for completed tasks`(): Unit = mainCoroutineRule.runBlockingTest {
        // Create an active task and add it to the repository.
        tasksViewModel.setFiltering(TasksFilterType.COMPLETED_TASKS)
        val tasks = tasksRepository.observeTasks().getOrAwaitValue()
        val filtered = tasksViewModel.filterTasks(tasks).getOrAwaitValue()
        assertThat(tasks).isInstanceOf(Result.Success::class.java)
        tasks as Result.Success
        assertThat(tasks.data) notEqualTo filtered
    }


    @Test
    fun `filterTasks fails on error`(): Unit = mainCoroutineRule.runBlockingTest {
        // Create an active task and add it to the repository.
        tasksRepository.setReturnError(true)
        val tasks = tasksRepository.observeTasks().getOrAwaitValue()
        val filtered = tasksViewModel.filterTasks(tasks).getOrAwaitValue()
        assertThat(tasks).isInstanceOf(Result.Error::class.java)
        tasks as Result.Error
        assertThat(tasks.exception.message).isNotNull()
        assertThat(tasks.exception.message) equalTo "Test exception"
        assertThat(filtered).isEmpty()
    }

    @Test
    fun `filter tasks returns empty list on loading`(): Unit = mainCoroutineRule.runBlockingTest {
        // Create an active task and add it to the repository.
        tasksRepository.setReturnLoading(true)
        val tasks = tasksRepository.observeTasks().getOrAwaitValue()
        val filtered = tasksViewModel.filterTasks(tasks).getOrAwaitValue()
        assertThat(tasks).isInstanceOf(Result.Loading::class.java)
        tasks as Result.Loading
        assertThat(filtered).isEmpty()
    }
}