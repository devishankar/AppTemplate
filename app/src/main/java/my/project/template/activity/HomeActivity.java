package my.project.template.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import my.project.template.App;
import my.project.template.R;
import my.project.template.fragments.NavigationDrawerFragment;
import my.project.template.utils.AppConstants;
import my.project.template.utils.Logger;
import my.project.template.utils.Utils;

public class HomeActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "HomeActivity";
    SharedPreferences pref;
    private GoogleApiClient mGoogleApiClient;
    //private NavigationDrawerFragment mDrawerFmt;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setTitle(toolbar, R.string.title_activity_home);
        if (getSupportActionBar() != null)
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        pref = Utils.getSharedPref(this);
        mGoogleApiClient = buildGoogleApiClient();
        App.googleApiClient = mGoogleApiClient;

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.nav_settings:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Logger.d(TAG, "selected position " + position);
        String[] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        String menu = navMenuTitles[position];
        Logger.d(TAG, "selected menu " + menu);

        switch (menu) {
            case "About":
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case "Settings":
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case "Logout":
                int regType = pref.getInt(AppConstants.PresConstants.PROPERTY_USER_REG_TYPE, 0);
                if (regType == 3) {
                    try {
                        if (mGoogleApiClient.isConnected()) {
                            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                            mGoogleApiClient.disconnect();
                            mGoogleApiClient.connect();
                            Logger.d(TAG, "g+ Session Closing");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                logOut();
                break;
            default:
                break;
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Logger.d(TAG, "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Logger.d(TAG, "onConnectionSuspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
