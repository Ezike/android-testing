package com.example.android.architecture.blueprints.todoapp.taskdetail

import FakeAndroidTestRepository
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ServiceLocator
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class TaskDetailFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun setup() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @Test
    fun activeTaskDetails_DisplayedInUi(): Unit = runBlockingTest {
        // GIVEN - Add active (incomplete) task to the DB
        val activeTask = Task("Active Task", "AndroidX Rocks", false)
        val activTask = Task("Active ", "AndroidX locks", false)
        val actveTask = Task(" Task", "Android Rocks", false)
        repository.saveTask(activeTask)
        repository.saveTask(activTask)
        repository.saveTask(actveTask)
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
        Thread.sleep(2000)
    }

    @Test
    fun activeTaskDetails_DisplayInUi() {
        runBlockingTest {
            // GIVEN - Add active (incomplete) task to the DB
            val activeTask = Task("Active Task", "AndroidX Rocks", false)
            repository.saveTask(activeTask)

            // WHEN - Details fragment launched to display task
            val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
            launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

            // THEN - Task details are displayed on the screen
            // make sure that the title/description are both shown and correct
            onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
            onView(withId(R.id.task_detail_title_text)).check(matches(withText("Active Task")))
            onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
            onView(withId(R.id.task_detail_description_text)).check(matches(withText("AndroidX Rocks")))
            // and make sure the "active" checkbox is shown unchecked
            onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
            onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))
        }
    }

    @Test
    fun completedTaskDetails_DisplayedInUi() = runBlockingTest {
        // GIVEN - Add completed task to the DB
        val activeTask = Task("Some task", "AndroidX locks", true)
        repository.saveTask(activeTask)

        // WHEN - Details fragment launched to display task
        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

        // THEN - Task details are displayed on the screen
        // make sure that the title/description are both shown and correct
        onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("Some task")))
        onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("AndroidX locks")))
        // and make sure the "active" checkbox is shown unchecked
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isChecked()))
    }

    @After
    fun reset(): Unit = runBlockingTest {
        ServiceLocator.resetRepository()
    }
}