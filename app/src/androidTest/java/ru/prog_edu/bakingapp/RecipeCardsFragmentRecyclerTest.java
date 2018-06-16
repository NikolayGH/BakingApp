package ru.prog_edu.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class RecipeCardsFragmentRecyclerTest {


    @Rule
    public final ActivityTestRule<RecipeListActivity> mActivityTestRule = new ActivityTestRule<>(RecipeListActivity.class);

    @Before
    public void yourSetUPFragment() {mActivityTestRule.getActivity ()
            .getFragmentManager().beginTransaction ();
    }

    @Test
    public void listItemClick() {
        IdlingResource idlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(R.id.catalog_recycler))
                .perform(actionOnItemAtPosition(0, click()));

        onView(withId(R.id.tv_ingredients)).check(matches(withText("INGREDIENTS:")));
        Espresso.unregisterIdlingResources(idlingResource);
    }
}
