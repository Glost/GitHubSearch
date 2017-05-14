/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.jetbrains.githubsearch.connection.RequestMethods;
import com.jetbrains.githubsearch.models.User;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents the main activity of the application
 * which contains a lot of important elements including
 * SearchView and UserFragment.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UserFragment.OnListFragmentInteractionListener,
        InfoFragment.OnFragmentInteractionListener{

    /**
     * The array for initializing MatrixCursor instance (we need it for using the search suggestions).
     */
    private static final String[] SUGGESSTIONS_COLUMNS = new String[] {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1
    };

    /**
     * The current UserFragment instance.
     * This static field helps to save stability during the device's orientation change.
     */
    private static UserFragment userFragment;

    /**
     * The current InfoFragment instance.
     * This static field helps to save stability during the device's orientation change.
     */
    private static InfoFragment infoFragment;

    /**
     * The current active Fragment instance.
     * This static field helps to save stability during the device's orientation change.
     */
    private static Fragment currentFragment;

    /**
     * The SearchView instance.
     * Used for searching in GitHub's users and favourites.
     * This static field helps to save stability during the device's orientation change.
     */
    private static SearchView searchView;

    /**
     * The current UsersSearch AsyncTask instance.
     * This static field helps to save stability during the device's orientation change.
     */
    private static UsersSearch usersSearch;

    /**
     * The last loaded search page.
     * This static field helps to save stability during the device's orientation change.
     */
    private static int lastPage;

    /**
     * The last done search query.
     * This static field helps to save stability during the device's orientation change.
     */
    private static String lastQuery;

    /**
     * The activity's FragmentManager instance.
     */
    private FragmentManager fragmentManager;

    /**
     * The DBHelper instance.
     * Used for creating or loading the application's SQLite database.
     */
    private DBHelper dbHelper;

    /**
     * The application's SQLite database.
     */
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        loadData();

        searchView = (SearchView) findViewById(R.id.searchView);

        final SimpleCursorAdapter suggestionsAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1,
                null,
                new String[] {SearchManager.SUGGEST_COLUMN_TEXT_1},
                new int[] {android.R.id.text1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        searchView.setSuggestionsAdapter(suggestionsAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(Storage.getSuggestions().contains(query)) {
                    return true;
                }

                Storage.getSuggestions().add(query);
                saveSuggestions();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                MatrixCursor matrixCursor = new MatrixCursor(SUGGESSTIONS_COLUMNS);
                List<String> suggestions = Storage.getSuggestions();
                for(int i = 0; i < suggestions.size(); i++) {
                    if(suggestions.get(i).toLowerCase().contains(newText.toLowerCase())) {
                        matrixCursor.addRow(new Object[] {i, suggestions.get(i)});
                    }
                }
                suggestionsAdapter.changeCursor(matrixCursor);
                suggestionsAdapter.notifyDataSetChanged();

                switch (userFragment.getUserFragmentType()) {
                    case SEARCH_RESULTS:
                        if(lastQuery != null && lastQuery.equals(newText)) {
                            break;
                        }

                        lastQuery = newText;

                        if(usersSearch != null) {
                            try {
                                usersSearch.cancel(false);
                            } catch (NullPointerException e) { }
                        }

                        if(newText.equals("")) {
                            Storage.getUsers().clear();
                            showInfoFragment(getString(R.string.welcome_info));
                            break;
                        }

                        usersSearch = new UsersSearch(MainActivity.this, 1);
                        usersSearch.execute(newText);
                        lastPage = 1;

                        break;

                    case FAVOURITES:
                        searchInFavourites(newText);

                        break;
                }

                return true;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {


            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) suggestionsAdapter.getItem(position);
                String query = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchView.setQuery(query, true);
                return true;
            }
        });

        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItems = recyclerView.getLayoutManager().getChildCount();
                int allItems = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                if(userFragment.getUserFragmentType() == UserFragment.UserFragmentType.SEARCH_RESULTS
                        && firstVisibleItem + visibleItems >= allItems
                        && (usersSearch == null || lastPage == 1)
                        && Storage.getTotalUsersAmount() > recyclerView.getAdapter().getItemCount()) {
                    usersSearch = new UsersSearch(MainActivity.this, ++lastPage);
                    usersSearch.execute(searchView.getQuery().toString());
                }
            }
        };

        fragmentManager = getSupportFragmentManager();

        if(userFragment == null) {
            userFragment = UserFragment.newInstance(1);
            userFragment.setOnScrollListener(onScrollListener);
        }

        if(infoFragment == null) {
            infoFragment = InfoFragment.newInstance("", "");
        }

        if(currentFragment == null) {
            infoFragment.setInfoText(getString(R.string.welcome_info));
            currentFragment = infoFragment;
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainUserListContainer, currentFragment);
        fragmentTransaction.commit();

        if(usersSearch != null && lastPage != 0) {
            usersSearch.setCurrentMainActivity(this);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        boolean needShow = false;

        UserFragment.UserFragmentType newUserFragmentType = null;

        switch (id) {
            case R.id.nav_home:
                if(userFragment.getUserFragmentType() != UserFragment.UserFragmentType.SEARCH_RESULTS) {
                    newUserFragmentType = UserFragment.UserFragmentType.SEARCH_RESULTS;
                    Storage.getUsers().clear();
                    needShow = true;
                }

                break;

            case R.id.nav_favourites:
                if(userFragment.getUserFragmentType() != UserFragment.UserFragmentType.FAVOURITES) {
                    newUserFragmentType = UserFragment.UserFragmentType.FAVOURITES;
                    needShow = true;
                }

                break;
        }

        if(needShow) {
            showUserFragment(newUserFragmentType);
            searchView.setQuery("", false);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) { }

    @Override
    public void onListFragmentInteraction(User item) { }

    /**
     * Shows the UserFragment with content of the given "type" if it is necassary.
     * @param userFragmentType The given "type" - the UserFragmentType instance.
     */
    private void showUserFragment(UserFragment.UserFragmentType userFragmentType) {
        boolean canShow = true;

        switch (userFragmentType) {
            case SEARCH_RESULTS:
                if(Storage.getUsers().size() == 0) {
                    showInfoFragment(getString(R.string.welcome_info));
                    canShow = false;
                }

                break;

            case FAVOURITES:
                if(Storage.getFavourites().size() == 0) {
                    showInfoFragment(getString(R.string.nofavs_info));
                    canShow = false;
                }

                break;
        }

        userFragment.setUserFragmentType(userFragmentType);
        if(canShow) {
            showFragment(userFragment);
        }
    }

    /**
     * Shows the InfoFragment with the given text information.
     * @param infoText The given text.
     */
    private void showInfoFragment(String infoText) {
        infoFragment.setInfoText(infoText);
        showFragment(infoFragment);
    }

    /**
     * Shows the given fragment.
     * @param fragment The given fragment.
     */
    private void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainUserListContainer, fragment);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }

    /**
     * Makes the search in the list of favourites by the given keyword.
     * @param keyword The given keyword.
     */
    private void searchInFavourites(String keyword) {
        List<User> favourites = Storage.getFavourites();

        if(favourites.size() == 0) {
            return;
        }

        if(keyword.equals("")) {
            userFragment.setUserFragmentType(UserFragment.UserFragmentType.FAVOURITES);
            return;
        }

        List<User> filteredFavs = new ArrayList<User>();
        for(User user : favourites) {
            if(checkIfUserFiltered(user, keyword)) {
                filteredFavs.add(user);
            }
        }

        userFragment.setUserFragmentList(filteredFavs);
    }

    /**
     * Checks whether the user's login satisfies the given search filter or not.
     * @param user The user for login checking.
     * @param filter The given search filter.
     * @return true if the user's login satisfies the given search filter, false otherwise.
     */
    private boolean checkIfUserFiltered(User user, String filter) {
        String login = user.getLogin();
        return login.toLowerCase().contains(filter.toLowerCase());
    }

    /**
     * Adds the user to favourites.
     * @param user The user for adding to favourites.
     */
    void addFavourite(User user) {
        Storage.getFavourites().add(user);

        ContentValues contentValues = new ContentValues();
        contentValues.put("login", user.getLogin());
        contentValues.put("githubId", user.getId());
        contentValues.put("avatarUrl", user.getAvatarUrl());
        database.insert("favourites", null, contentValues);
    }

    /**
     * Removes the user from favourites.
     * @param user The user for removing from favourites.
     */
    void removeFavourite(User user) {
        List<User> favourites = Storage.getFavourites();

        for(int i = 0; i < favourites.size(); i++) {
            if(favourites.get(i).getLogin().equals(user.getLogin())) {
                favourites.remove(i);
                break;
            }
        }

        database.delete("favourites", "login = \"" + user.getLogin() + "\"", null);
    }

    /**
     * Check if the user with a such login already exists in the favourites list.
     * @param user The user for login checking.
     * @return true, if the user with a such login already exists in the favourites list, false otherwise.
     */
    boolean checkFavourite(User user) {
        List<User> favourites = Storage.getFavourites();

        for(User favourite : favourites) {
            if(favourite.getLogin().equals(user.getLogin())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Saves the suggestions' list into the application's SharedPreferences.
     */
    void saveSuggestions() {
        SharedPreferences.Editor preferences = getSharedPreferences(getResources().getString(R.string.preferences_file_name), MODE_PRIVATE).edit();
        preferences.clear();

        preferences.putStringSet("suggestions", new HashSet<String>(Storage.getSuggestions()));
        preferences.commit();
    }

    /**
     * Loads the data (suggestions and favourites) from the application's SharedPreferences
     * and the application's SQLite database.
     */
    private void loadData() {
        SharedPreferences preferences = getSharedPreferences(getResources().getString(R.string.preferences_file_name), MODE_PRIVATE);

        if(Storage.getSuggestions().size() == 0) {
            Set<String> suggestionsSet = preferences.getStringSet("suggestions", null);
            if(suggestionsSet != null) {
                Storage.setSuggestions(new ArrayList<String>(suggestionsSet));
            }
        }

        if(Storage.getFavourites().size() != 0) {
            return;
        }

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query("favourites", null, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            int loginColIndex = cursor.getColumnIndex("login");
            int githubIdColIndex = cursor.getColumnIndex("githubId");
            int avatarUrlColIndex = cursor.getColumnIndex("avatarUrl");

            do {
                Storage.getFavourites().add(new User(cursor.getString(loginColIndex),
                        cursor.getInt(githubIdColIndex), cursor.getString(avatarUrlColIndex)));
            } while (cursor.moveToNext());
        }
    }

    /**
     * Helps to create or load the application's SQLite database.
     */
    private class DBHelper extends SQLiteOpenHelper {
        /**
         * Constructor.
         * @param context The Context instance.
         */
        public DBHelper(Context context) {
            super(context, getResources().getString(R.string.preferences_file_name), null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table favourites (id integer primary key autoincrement, login text, " +
                    "githubId integer, avatarUrl text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
    }

    /**
     * Represents the AsyncTask process of searching the GitHub's users.
     */
    private class UsersSearch extends AsyncTask<String, Void, List<User>> {
        /**
         * The current MainActivity instance.
         */
        private MainActivity currentMainActivity;

        /**
         * The number of search's page for loading.
         */
        private int page;

        /**
         * Constructor.
         * @param currentMainActivity The current MainActivity instance.
         * @param page The number of search's page for loading.
         */
        public UsersSearch(MainActivity currentMainActivity, int page) {
            this.currentMainActivity = currentMainActivity;
            this.page = page;
        }

        @Override
        protected List<User> doInBackground(String... params) {
            String keyword = params[0];
            try {
                List<User> users = RequestMethods.searchUsers(keyword, page);
                return users;
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(page > 1) {
                Snackbar.make(currentFragment.getView(),
                        getString(R.string.loading_info),
                        Snackbar.LENGTH_SHORT).show();
            } else {
                showInfoFragment(getString(R.string.loading_info));
            }
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);

            if(users == null) {
                Snackbar.make(currentFragment.getView(),
                        getString(R.string.error_message),
                        Snackbar.LENGTH_SHORT).show();

                return;
            }

            if(page == 1) {
                Storage.setUsers(users);
                Storage.setTotalUsersAmount(RequestMethods.getTotalUsersAmount());
                if(users.size() == 0) {
                    showInfoFragment(getString(R.string.notfound_info));
                } else if(userFragment.getUserFragmentType() != UserFragment.UserFragmentType.FAVOURITES) {
                    currentMainActivity.showUserFragment(UserFragment.UserFragmentType.SEARCH_RESULTS);
                }
            } else {
                Storage.getUsers().addAll(users);
                userFragment.getAdapter().notifyDataSetChanged();
                usersSearch = null;
            }
        }

        /**
         * Sets the current MainActivity instance.
         * @param currentMainActivity The current MainActivity instance.
         */
        public void setCurrentMainActivity(MainActivity currentMainActivity) {
            this.currentMainActivity = currentMainActivity;
        }
    }
}
