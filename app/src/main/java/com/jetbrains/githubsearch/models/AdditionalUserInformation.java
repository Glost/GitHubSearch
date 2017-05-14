/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch.models;

/**
 * Represents the user's additional information (company, location, etc.).
 */
public class AdditionalUserInformation {

    /**
     * Shows whether the user is the site admin or not.
     */
    private boolean isSiteAdmin;

    /**
     * The user's name.
     */
    private String name;

    /**
     * The user's company.
     */
    private String company;

    /**
     * The user's location.
     */
    private String location;

    /**
     * The user's email.
     */
    private String email;

    /**
     * The user's bio.
     */
    private String bio;

    /**
     * The number of the user's public repositories.
     */
    private int publicRepos;

    /**
     * The number of the user's public gists.
     */
    private int publicGists;

    /**
     * The number of the user's followers.
     */
    private int followers;

    /**
     * The number of users followed by the current user.
     */
    private int following;

    /**
     * Constructor.
     * @param isSiteAdmin Shows whether the user is the site admin or not.
     * @param name The user's name.
     * @param company The user's company.
     * @param location The user's location.
     * @param email The user's email.
     * @param bio The user's bio.
     * @param publicRepos The number of the user's public repositories.
     * @param publicGists The number of the user's public gists.
     * @param followers The number of the user's followers.
     * @param following The number of users followed by the current user.
     */
    public AdditionalUserInformation(boolean isSiteAdmin, String name,
                                     String company, String location, String email, String bio,
                                     int publicRepos, int publicGists, int followers, int following) {
        this.isSiteAdmin = isSiteAdmin;
        this.name = name;
        this.company = company;
        this.location = location;
        this.email = email;
        this.bio = bio;
        this.publicRepos = publicRepos;
        this.publicGists = publicGists;
        this.followers = followers;
        this.following = following;
    }

    /**
     * Shows whether the user is the site admin or not.
     * @return true if the user is the site admin, false otherwise.
     */
    public boolean isSiteAdmin() {
        return isSiteAdmin;
    }

    /**
     * Returns the user's name.
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the user's company.
     * @return The user's company.
     */
    public String getCompany() {
        return company;
    }

    /**
     * Returns the user's location.
     * @return The user's location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the user's email.
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's bio.
     * @return The user's bio.
     */
    public String getBio() {
        return bio;
    }

    /**
     * Returns the number of the user's public repositories.
     * @return The number of the user's public repositories.
     */
    public int getPublicRepos() {
        return publicRepos;
    }

    /**
     * Returns the number of the user's public gists.
     * @return The number of the user's public gists.
     */
    public int getPublicGists() {
        return publicGists;
    }

    /**
     * Returns the number of the user's followers.
     * @return The number of the user's followers.
     */
    public int getFollowers() {
        return followers;
    }

    /**
     * Returns the number of users followed by the current user.
     * @return The number of users followed by the current user.
     */
    public int getFollowing() {
        return following;
    }
}
