/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.jetbrains.githubsearch.connection.Request;
import com.jetbrains.githubsearch.models.User;

import java.io.IOException;

/**
 * Represents the image loading AsyncTask process.
 */
class ImageLoad extends AsyncTask<String, Void, Bitmap> {
    /**
     * The user which is waiting loading the image by this ImageLoad instance.
     */
    private User currentUser;

    /**
     * ViewHolder which has called this AsyncTask. If it was called not by ViewHolder, currentViewHolder equals null.
     */
    private UserRecyclerViewAdapter.ViewHolder currentViewHolder;

    /**
     * The current active instance of UserActivity. If it was called not by UserActivity, currentViewHolder equals null.
     */
    private UserActivity currentUserActivity;

    /**
     * Constructor.
     * @param currentUser The user which is waiting loading the image by this ImageLoad instance.
     * @param currentViewHolder ViewHolder which has called this AsyncTask. If it was called not by ViewHolder, currentViewHolder equals null.
     * @param currentUserActivity The current active instance of UserActivity. If it was called not by UserActivity, currentViewHolder equals null.
     */
    public ImageLoad(User currentUser, UserRecyclerViewAdapter.ViewHolder currentViewHolder, UserActivity currentUserActivity) {
        this.currentUser = currentUser;
        this.currentViewHolder = currentViewHolder;
        this.currentUserActivity = currentUserActivity;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            String urlString = params[0];
            Bitmap bitmap = Request.makeBitmapRequest(urlString);
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if(bitmap == null) {
            return;
        }

        // Applies changes to the UI elements:
        currentUser.setAvatar(bitmap);
        if(currentViewHolder != null) {
            currentViewHolder.loadAvatar();
        }
        if(currentUserActivity != null) {
            currentUserActivity.displayUserAvatar();
            currentUserActivity.setImageLoadNull();
        }
    }

    /**
     * Sets the current active instance of UserActivity.
     * @param currentUserActivity The current active instance of UserActivity.
     */
    public void setCurrentUserActivity(UserActivity currentUserActivity) {
        this.currentUserActivity = currentUserActivity;
    }
}