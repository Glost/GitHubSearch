/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jetbrains.githubsearch.connection.RequestMethods;
import com.jetbrains.githubsearch.models.AdditionalUserInformation;
import com.jetbrains.githubsearch.models.User;

import org.json.JSONException;

import java.io.IOException;

/**
 * The activity representing the user information card.
 */
public class UserActivity extends AppCompatActivity {

    /**
     * The current UserAdditionalInformationLoad AsyncTask instance.
     * This static field helps to save stability during the device's orientation change.
     */
    private static UserAdditionalInformationLoad userLoad;

    /**
     * The current ImageLoad AsyncTask instance.
     * This static field helps to save stability during the device's orientation change.
     */
    private static ImageLoad imageLoad;

    /**
     * The user whose information is displaying.
     */
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportActionBar().setTitle(getString(R.string.actionbar_title));

        user = Storage.getCurrentUser();
        if(user.getAdditionalUserInformation() == null) {
            if(userLoad == null) {
                userLoad = new UserAdditionalInformationLoad(this);
                userLoad.execute(user);
            } else {
                try {
                    userLoad.setCurrentUserActivity(this);
                } catch (NullPointerException e) { }
            }
        } else {
            displayUserInfo();
        }
        if(user.getAvatar() == null) {
            if(imageLoad == null) {
                imageLoad = new ImageLoad(user, null, this);
                imageLoad.execute(user.getAvatarUrl());
            } else {
                try {
                    imageLoad.setCurrentUserActivity(this);
                } catch (NullPointerException e) { }
            }
        } else {
            displayUserAvatar();
        }
    }

    /**
     * Displays the user information on TextView elements.
     */
    private void displayUserInfo() {
        TextView loginTextView = (TextView) findViewById(R.id.loginTextView);
        loginTextView.setText(user.getLogin());

        AdditionalUserInformation additionalUserInformation = user.getAdditionalUserInformation();

        TextView nameTextView = (TextView) findViewById(R.id.nameTextView);
        nameTextView.setText(handleNull(additionalUserInformation.getName()));

        TextView companyTextView = (TextView) findViewById(R.id.companyTextView);
        companyTextView.setText(handleNull(additionalUserInformation.getCompany()));

        TextView locationTextView = (TextView) findViewById(R.id.locationTextView);
        locationTextView.setText(handleNull(additionalUserInformation.getLocation()));

        TextView emailTextView = (TextView) findViewById(R.id.emailTextView);
        emailTextView.setText(handleNull(additionalUserInformation.getEmail()));

        TextView publicReposTextView = (TextView) findViewById(R.id.publicReposTextView);
        publicReposTextView.setText("" + additionalUserInformation.getPublicRepos());

        TextView publicGistsTextView = (TextView) findViewById(R.id.publicGistsTextView);
        publicGistsTextView.setText("" + additionalUserInformation.getPublicGists());

        TextView followersTextView = (TextView) findViewById(R.id.followersTextView);
        followersTextView.setText("" + additionalUserInformation.getFollowers());

        TextView followingTextView = (TextView) findViewById(R.id.followingTextView);
        followingTextView.setText("" + additionalUserInformation.getFollowing());

        TextView bioTextView = (TextView) findViewById(R.id.bioTextView);
        String bio = additionalUserInformation.getBio();
        bioTextView.setText(bio != null && !bio.equals("") && !bio.equals("null") ? bio : getString(R.string.nobio));

        TextView adminTextView = (TextView) findViewById(R.id.adminTextView);
        if(additionalUserInformation.isSiteAdmin()) {
            adminTextView.setVisibility(View.VISIBLE);
        } else {
            adminTextView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Handles string that can be equal to "null" (not null reference).
     * @param text The string for handling.
     * @return The string result of handling for displaying on the TextView.
     */
    String handleNull(String text) {
        return !text.equals("null") ? text : "Hidden or not specified";
    }

    /**
     * Displays user's avatar on the ImageVIew.
     */
    void displayUserAvatar() {
        ImageView avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
        avatarImageView.setImageBitmap(user.getAvatar());
    }

    /**
     * Sets the ImageLoad instance (the imageLoad static field) the null reference.
     */
    void setImageLoadNull() {
        imageLoad = null;
    }

    /**
     * Represents the AsyncTask process of loading user's additional information (company, location, etc.).
     */
    private class UserAdditionalInformationLoad extends AsyncTask<User, Void, User> {

        /**
         * The current active instance of UserActivity.
         */
        private UserActivity currentUserActivity;

        /**
         * Constructor.
         * @param currentUserActivity The current active instance of UserActivity.
         */
        public UserAdditionalInformationLoad(UserActivity currentUserActivity) {
            this.currentUserActivity = currentUserActivity;
        }

        @Override
        protected User doInBackground(User... params) {
            User loadedUser = params[0];
            try {
                loadedUser = RequestMethods.getUser(loadedUser.getLogin(), loadedUser);
                return loadedUser;
            } catch (IOException | JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(User user) {
            super.onPostExecute(user);

            userLoad = null;

            if(user == null) {
                Snackbar.make(findViewById(android.R.id.content),
                        getResources().getString(R.string.error_message),
                        Snackbar.LENGTH_SHORT).show();

                finish();
            }

            currentUserActivity.displayUserInfo();
        }

        /**
         * Sets the current active instance of UserActivity.
         * @param currentUserActivity The current active instance of UserActivity.
         */
        public void setCurrentUserActivity(UserActivity currentUserActivity) {
            this.currentUserActivity = currentUserActivity;
        }
    }
}
