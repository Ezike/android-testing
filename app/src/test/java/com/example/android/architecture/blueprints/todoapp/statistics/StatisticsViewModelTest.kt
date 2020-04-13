package com.example.android.architecture.blueprints.todoapp.statistics

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.android.architecture.blueprints.todoapp.MainCoroutineRule
import com.example.android.architecture.blueprints.todoapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.todoapp.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StatisticsViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var statisticsViewModel: StatisticsViewModel

    // Use a fake repository to be injected into the view model.
    private lateinit var tasksRepository: FakeRepository

    @Before
    fun setupStatisticsViewModel() {
        // Initialise the repository with no tasks.
        tasksRepository = FakeRepository()
        statisticsViewModel = StatisticsViewModel(tasksRepository)
    }

    @Test
    fun loadTasks_loading() {
        mainCoroutineRule.pauseDispatcher()
        // Load the task in the view model.
        statisticsViewModel.refresh()
        // Then progress indicator is shown.
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue()).isTrue()
        // Then progress indicator is hidden.
        mainCoroutineRule.resumeDispatcher()
        assertThat(statisticsViewModel.dataLoading.getOrAwaitValue()).isFalse()
    }

    @Test
    fun loadStatisticsWhenTasksAreUnavailable_callErrorToDisplay() {
        // Make the repository return errors.
        tasksRepository.setReturnError(true)
        statisticsViewModel.refresh()

        // Then empty and error are true (which triggers an error message to be shown).
        assertThat(statisticsViewModel.empty.getOrAwaitValue()).isTrue()
        assertThat(statisticsViewModel.error.getOrAwaitValue()).isTrue()
    }
}