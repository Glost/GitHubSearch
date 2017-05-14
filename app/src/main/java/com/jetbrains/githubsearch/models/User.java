/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch.models;

import android.graphics.Bitmap;

/**
 * Represents the GitHub's user.
 */
public class User {
    /**
     * The user's login.
     */
    private String login;

    /**
     * The user's id.
     */
    private int id;

    /**
     * The user's avatar's URL.
     */
    private String avatarUrl;

    /**
     * The user's avatar image.
     */
    private Bitmap avatar;

    /**
     * The user's additional information (company, location, etc.).
     */
    private AdditionalUserInformation additionalUserInformation;

    /**
     * Constructor.
     * @param login The user's login.
     * @param id The user's id.
     * @param avatarUrl The user's avatar's URL.
     */
    public User(String login, int id, String avatarUrl) {
        this.login = login;
        this.id = id;
        this.avatarUrl = avatarUrl;
    }

    /**
     * Constructor.
     * @param login The user's login.
     * @param id The user's id.
     * @param avatarUrl The user's avatar's URL.
     * @param additionalUserInformation The user's additional information (company, location, etc.).
     */
    public User(String login, int id, String avatarUrl, AdditionalUserInformation additionalUserInformation) {
        this(login, id, avatarUrl);
        this.additionalUserInformation = additionalUserInformation;
    }

    /**
     * Returns the user's login.
     * @return The user's login.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Returns The user's id.
     * @return The user's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the user's avatar's URL.
     * @return The user's avatar's URL.
     */
    public String getAvatarUrl() {
        return avatarUrl;
    }

    /**
     * Returns the user's avatar image.
     * @return The user's avatar image.
     */
    public Bitmap getAvatar() {
        return avatar;
    }

    /**
     * Sets the user's avatar image.
     * @param avatar The user's avatar image.
     */
    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    /**
     * Returns the user's additional information (company, location, etc.).
     * @return The user's additional information (company, location, etc.).
     */
    public AdditionalUserInformation getAdditionalUserInformation() {
        return additionalUserInformation;
    }

    /**
     * Sets the user's additional information (company, location, etc.).
     * @param additionalUserInformation The user's additional information (company, location, etc.).
     */
    public void setAdditionalUserInformation(AdditionalUserInformation additionalUserInformation) {
        this.additionalUserInformation = additionalUserInformation;
    }
}
