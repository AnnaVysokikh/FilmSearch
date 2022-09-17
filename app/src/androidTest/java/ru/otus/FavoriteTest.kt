package ru.otus

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.otus.presentation.MainActivity

@RunWith(AndroidJUnit4::class)
@LargeTest
class FavoriteTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun addToFavoriteTest () {
        Espresso.onView(ViewMatchers.withId(R.id.recycler))
            .perform(RecyclerViewActions
                .actionOnItemAtPosition<RecyclerView.ViewHolder>(0, clickOnViewChild(R.id.favorite_film)))

        Espresso.onView(ViewMatchers.withSubstring("favorites"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun clickOnViewChild(viewId: Int) = object : ViewAction {
        override fun getConstraints() = null

        override fun getDescription() = "Click on a child view with specified id."

        override fun perform(uiController: UiController, view: View) = ViewActions.click()
            .perform(uiController, view.findViewById<View>(viewId))
    }
}