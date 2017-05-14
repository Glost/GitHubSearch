/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch;

import com.jetbrains.githubsearch.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the static storage for keeping data during the application's running.
 */
public class Storage {
    /**
     * The list of the found users.
     */
    private static List<User> users = new ArrayList<User>();

    /**
     * The list of the favourites.
     */
    private static List<User> favourites = new ArrayList<User>();

    /**
     * The current user for displaying information in UserActivity.
     */
    private static User currentUser;

    /**
     * The total amount of the found GitHub's users.
     */
    private static int totalUsersAmount;

    /**
     * The list of the search suggestions.
     */
    private static List<String> suggestions = new ArrayList<String>();

    /**
     * Returns the list of the search suggestions.
     * @return The list of the search suggestions.
     */
    public static List<String> getSuggestions() {
        return suggestions;
    }

    /**
     * Sets the list of the search suggestions.
     * @param suggestions The list of the search suggestions.
     */
    public static void setSuggestions(List<String> suggestions) {
        Storage.suggestions = suggestions;
    }

    /**
     * Returns the list of the found users.
     * @return The list of the found users.
     */
    public static List<User> getUsers() {
        return users;
    }

    /**
     * Sets the list of the found users.
     * @param users The list of the found users.
     */
    public static void setUsers(List<User> users) {
        Storage.users = users;
    }

    /**
     * Returns the list of the favourites.
     * @return The list of the favourites.
     */
    public static List<User> getFavourites() {
        return favourites;
    }

    /**
     * Set the list of the favourites.
     * @param favourites The list of the favourites.
     */
    public static void setFavourites(List<User> favourites) {
        Storage.favourites = favourites;
    }

    /**
     * Returns the current user for displaying information in UserActivity.
     * @return The current user for displaying information in UserActivity.
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user for displaying information in UserActivity.
     * @param currentUser The current user for displaying information in UserActivity.
     */
    public static void setCurrentUser(User currentUser) {
        Storage.currentUser = currentUser;
    }

    /**
     * Returns the total amount of the found GitHub's users.
     * @return The total amount of the found GitHub's users.
     */
    public static int getTotalUsersAmount() {
        return totalUsersAmount;
    }

    /**
     * Sets the total amount of the found GitHub's users.
     * @param totalUsersAmount The total amount of the found GitHub's users.
     */
    public static void setTotalUsersAmount(int totalUsersAmount) {
        Storage.totalUsersAmount = totalUsersAmount;
    }
}
