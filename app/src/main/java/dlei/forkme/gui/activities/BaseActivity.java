package dlei.forkme.gui.activities;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import dlei.forkme.R;
import dlei.forkme.gui.activities.github.MergeMeActivity;
import dlei.forkme.gui.activities.github.TrendingRepositoriesActivity;
import dlei.forkme.gui.activities.github.YourRepositoriesActivity;
import dlei.forkme.gui.activities.github.YourStarsActivity;
import dlei.forkme.helpers.NetworkAsyncCheck;
import dlei.forkme.helpers.NetworkHelper;
import dlei.forkme.state.AppSettings;

// TODO: Move network checks to onFail() Http calls.
/**
 * BaseActivity for that application with a navigation drawer, all activities that require a navigation
 * drawer should extend BaseActivity.
 */
public class BaseActivity extends AppCompatActivity {

    // Subclasses have access to mNavDrawer.
    protected Drawer mNavDrawer = null;

    private PrimaryDrawerItem mStars;
    private PrimaryDrawerItem mTrending;
    private PrimaryDrawerItem mFindPeople;
    private PrimaryDrawerItem mYourRepositories;
    private PrimaryDrawerItem mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Log.d("BaseActivity: ", "created");

        mStars = new PrimaryDrawerItem()
                .withIdentifier(1)
                .withName("Stars")
                .withIcon(ActivityCompat.getDrawable(this, R.drawable.ic_grade_48px));

        mTrending = new PrimaryDrawerItem()
                .withIdentifier(2)
                .withName("Trending")
                .withIcon(ActivityCompat.getDrawable(this, R.drawable.ic_trending_up_48px));

        mFindPeople = new PrimaryDrawerItem()
                .withIdentifier(3)
                .withName("Find people")
                .withIcon(ActivityCompat.getDrawable(this, R.drawable.ic_group_48px));

        mYourRepositories = new PrimaryDrawerItem()
                .withIdentifier(4)
                .withName("Your repositories")
                .withIcon(ActivityCompat.getDrawable(this, R.drawable.ic_folder_48px));

        mSettings = new PrimaryDrawerItem()
                .withIdentifier(5)
                .withName("Settings")
                .withIcon(ActivityCompat.getDrawable(this, R.drawable.ic_settings_48px));
    }

    public void inflateNavDrawer(Bundle savedInstanceState) {
        Log.d("BaseActivity: ", "inflateNavDrawer: called");
        Toolbar toolbar = (Toolbar) findViewById(R.id.nav_drawer_toolbar);
        setSupportActionBar(toolbar);
        NetworkAsyncCheck n = NetworkHelper.checkNetworkConnection(toolbar);
        if (n != null) {
            n.execute();
        }

        mNavDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .addDrawerItems(
                        new SectionDrawerItem().withName(R.string.drawer_section_github),
                        mStars,
                        mTrending,
                        mYourRepositories,
                        new SectionDrawerItem().withName(R.string.drawer_section_people),
                        mFindPeople,
                        new SectionDrawerItem().withName(R.string.action_settings),
                        mSettings
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == mStars) {
                            Intent intent = new Intent(getBaseContext(), YourStarsActivity.class);
                            startActivity(intent);
                        } else if (drawerItem == mTrending) {
                            Intent intent = new Intent(getBaseContext(), TrendingRepositoriesActivity.class);
                            startActivity(intent);

                        } else if (drawerItem == mFindPeople) {
                            Intent intent = new Intent(getBaseContext(), MergeMeActivity.class);
                            startActivity(intent);

                        } else if (drawerItem == mYourRepositories) {
                            Intent intent = new Intent(getBaseContext(), YourRepositoriesActivity.class);
                            startActivity(intent);

                        } else if (drawerItem == mSettings) {
                            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                            startActivity(intent);

                        }
                        Log.d("Menu clicked: ", "position: " + position + " item: " + drawerItem);
                        return true;
                    }
                })

                .withSavedInstance(savedInstanceState)
                .build();
        Log.d("BaseActivity: ", "inflateNavDrawer: " + mNavDrawer);
    }


    public void inflateNavDrawer(Bundle savedInstanceState, String currentActivity) {
        Log.d("BaseActivity: ", "inflateNavDrawer: called, currentActivity: " + currentActivity);
        this.inflateNavDrawer(savedInstanceState);

        // Disable clicking on current activity.
        if (currentActivity.equals(YourStarsActivity.class.getSimpleName())) {
            mStars.withSelectable(false);
            mNavDrawer.setSelection(mStars, false);
        } else if (currentActivity.equals(TrendingRepositoriesActivity.class.getSimpleName())) {
            mTrending.withSelectable(false);
            mNavDrawer.setSelection(mTrending, false);
        } else if (currentActivity.equals(MergeMeActivity.class.getSimpleName())) {
            mFindPeople.withSelectable(false);
            mNavDrawer.setSelection(mFindPeople, false);
        } else if (currentActivity.equals(YourRepositoriesActivity.class.getSimpleName())) {
            mYourRepositories.withSelectable(false);
            mNavDrawer.setSelection(mYourRepositories, false);
        } else if (currentActivity.equals(AppSettings.class.getSimpleName())) {
            mSettings.withSelectable(false);
            mNavDrawer.setSelection(mSettings, false);
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("BaseActivity: ", "onSavedInstanceState: called");
        // Add the values which need to be saved from the drawer to the bundle.
        outState = mNavDrawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Log.d("BaseActivity: ", "Back");
        if (mNavDrawer != null) {
            Log.d("BaseActivity: ", "isDrawerOpen: " + mNavDrawer.isDrawerOpen());
            Log.d("BaseActivity: ", "mNavDrawer: " + mNavDrawer);
        }
        // Prioritize closing drawer.
        if (mNavDrawer != null && mNavDrawer.isDrawerOpen()) {
            mNavDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

}
