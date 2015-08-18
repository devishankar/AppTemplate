package my.project.template.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.facebook.widget.ProfilePictureView;
import my.project.template.App;
import my.project.template.R;
import my.project.template.activity.HomeActivity;
import my.project.template.activity.UserProfileActivity;
import my.project.template.adapters.NavDrawerListAdapter;
import my.project.template.models.NavDrawerBean;
import my.project.template.utils.AppConstants;
import my.project.template.utils.Logger;
import my.project.template.utils.Utils;

import java.util.ArrayList;

/**
 * @author Devishankar
 */

public class NavigationDrawerFragment extends Fragment implements View.OnClickListener {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String TAG = "NavigationDrawerFragment";
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = Utils.getSharedPref(getActivity());
        mUserLearnedDrawer = sp.getBoolean(AppConstants.PresConstants.PROPERTY_USER_LEARNED_DRAWER, false);
        Logger.d(TAG, "mUserLearnedDrawer on create " + mUserLearnedDrawer);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        RelativeLayout rlUserProfile = (RelativeLayout) view.findViewById(R.id.rlUserProfile);
        rlUserProfile.setOnClickListener(this);
        ProfilePictureView ivProfileIcon = (ProfilePictureView) view.findViewById(R.id.ivProfileIcon);
        ivProfileIcon.setVisibility(View.GONE);
        ImageView ivProfileIconOthers = (ImageView) view.findViewById(R.id.ivProfileIconOthers);

        TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        TextView tvUserEmail = (TextView) view.findViewById(R.id.tvUserEmail);

        if (Utils.getUserRegType(getActivity()) == 2 && !Utils.getFbId(getActivity()).equals("")) {
            ivProfileIcon.setProfileId(Utils.getFbId(getActivity()));
            ivProfileIcon.setPresetSize(ProfilePictureView.NORMAL);
            ivProfileIcon.setVisibility(View.VISIBLE);
            ivProfileIconOthers.setVisibility(View.GONE);
        } else if (Utils.getUserRegType(getActivity()) == 3 && !Utils.getGooglePlusUri(getActivity()).equals("")) {
            ivProfileIcon.setVisibility(View.GONE);
            ivProfileIconOthers.setVisibility(View.VISIBLE);
            App.imageLoader.displayImage(Utils.getGooglePlusUri(getActivity()), ivProfileIconOthers);
        }

        tvUserName.setText(Utils.getUserName(getActivity().getApplicationContext()));
        tvUserEmail.setText(Utils.getUserEmail(getActivity()));
        tvUserName.setText("Devishankar");
        tvUserEmail.setText("devishankargru@gmail.com");


        mDrawerListView = (ListView) view.findViewById(R.id.lvDrawer);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        rlUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
                getActivity().startActivity(new Intent(getActivity(), UserProfileActivity.class));
            }
        });
        ArrayList<NavDrawerBean> navDrawerItems = new ArrayList<>();
        String[] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        navDrawerItems.add(new NavDrawerBean(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        navDrawerItems.add(new NavDrawerBean(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        navDrawerItems.add(new NavDrawerBean(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        navDrawerItems.add(new NavDrawerBean(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerBean(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        navDrawerItems.add(new NavDrawerBean(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
        navDrawerItems.add(new NavDrawerBean(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
        navDrawerItems.add(new NavDrawerBean(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));

        mDrawerListView.setAdapter(new NavDrawerListAdapter(getActivity().getApplicationContext(), navDrawerItems));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        navMenuIcons.recycle();
        return view;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu();
                if (((HomeActivity) getActivity()).getSupportActionBar() != null)
                    ((HomeActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.title_activity_home));
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = Utils.getSharedPref(getActivity());
                    sp.edit().putBoolean(AppConstants.PresConstants.PROPERTY_USER_LEARNED_DRAWER, true).apply();
                    Logger.d(TAG, "mUserLearnedDrawer on create " + sp.getBoolean(AppConstants.PresConstants.PROPERTY_USER_LEARNED_DRAWER, false));
                }
                if (((HomeActivity) getActivity()).getSupportActionBar() != null)
                    ((HomeActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.app_name));
                getActivity().supportInvalidateOptionsMenu();
            }
        };
        Logger.d(TAG, "mFromSavedInstanceState " + mFromSavedInstanceState);
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rlUserProfile) {
            Logger.d(TAG, "User profile clicked");
        }
    }

    public interface NavigationDrawerCallbacks {
        void onNavigationDrawerItemSelected(int position);
    }
}
