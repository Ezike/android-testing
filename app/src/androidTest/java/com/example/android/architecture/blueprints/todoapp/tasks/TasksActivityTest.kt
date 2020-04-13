package com.example.android.architecture.blueprints.todoapp.tasks

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.architecture.blueprints.todoapp.*
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TasksActivityTest {

    private lateinit var repository: TasksRepository

    @Before
    fun init() {
        repository = ServiceLocator.provideTaskRepository(getApplicationContext())
        runBlocking {
            repository.deleteAllTasks()
        }
    }

    @After
    fun reset() {
        ServiceLocator.resetRepository()
    }

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    /**
     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
     * are not scheduled in the main Looper (for example when executed on a different thread).
     */
    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }


    @Test
    fun editTask() = runBlocking {
        // Set initial state.
        repository.saveTask(Task("TITLE1", "DESCRIPTION"))
        repository.saveTask(Task("TITLE2", "UNDESCRIPTION"))
        repository.saveTask(Task("TITLE6", "INDESCRIPTION", isCompleted = true))


        // Start up Tasks screen.
        val activityScenario = ActivityScenario.launch(TasksActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario) // LOOK HERE


        onView(withText("TITLE6")).perform(click())
        onView(withId(R.id.task_detail_title_text)).check(matches(withText("TITLE6")))
        onView(withId(R.id.task_detail_description_text)).check(matches(withText("INDESCRIPTION")))
        onView(withId(R.id.task_detail_complete_checkbox)).check(matches((isChecked())))

        onView(withId(R.id.edit_task_fab)).perform(click())
        onView(withId(R.id.add_task_title_edit_text)).check(matches(isDisplayed()))
                .perform(replaceText("NEW TITLE"))
        onView(withId(R.id.add_task_description_edit_text)).check(matches(isDisplayed()))
                .perform(replaceText("NEW DESCRIPTION"))
        onView(withId(R.id.save_task_fab)).perform(click())

        onView(withText("NEW TITLE")).check(matches(isDisplayed()))
        onView(withText("TITLE6")).check(doesNotExist())

        // Espresso code will go here.

        // Make sure the activity is closed before resetting the db:
        activityScenario.close()
    }
}
