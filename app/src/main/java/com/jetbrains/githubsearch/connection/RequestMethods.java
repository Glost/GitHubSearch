/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch.connection;

import com.jetbrains.githubsearch.models.*;

import org.json.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the static methods for making "GET" requests to GitHub.
 */
public class RequestMethods {

    /**
     * The GitHub API's URL.
     */
    private static final String API_URL = "https://api.github.com/";

    /**
     * The URL's part for GitHub's users search API.
     */
    private static final String SEARCH_USERS_API_URL = "search/users?q=";

    /**
     * The added URL's part for specifying the necessary search page.
     */
    private static final String PAGE_URL = "&page=";

    /**
     * The URL's part for GitHub's users getting API.
     */
    private static final String GET_USER_URL = "users/";

    /**
     * The total amount of the found GitHub's users.
     */
    private static int totalUsersAmount;

    /**
     * Returns the total amount of the found GitHub's users.
     * @return The total amount of the found GitHub's users.
     */
    public static int getTotalUsersAmount() {
        return totalUsersAmount;
    }

    /**
     * Parses the user JSON object.
     * @param user The user JSON object.
     * @return The User instance.
     * @throws JSONException If some problem during parsing the JSON object happened.
     */
    private static User parseUser(JSONObject user) throws JSONException {
        String login = user.getString("login");
        int id = user.getInt("id");
        String avatarUrl = user.getString("avatar_url");

        return new User(login, id, avatarUrl);
    }

    /**
     * Parses the user's additional information JSON object.
     * @param user The user's additional information JSON object.
     * @return The AdditionalUserInformation instance.
     * @throws JSONException If some problem during parsing the JSON object happened.
     */
    private static AdditionalUserInformation parseAdditionalUserInformation(JSONObject user)
            throws JSONException {
        boolean isSiteAdmin = user.getBoolean("site_admin");
        String name = user.getString("name");
        String company = user.getString("company");
        String location = user.getString("location");
        String email = user.getString("email");
        String bio = user.getString("bio");
        int publicRepos = user.getInt("public_repos");
        int publicGists = user.getInt("public_gists");
        int followers = user.getInt("followers");
        int following = user.getInt("following");

        return new AdditionalUserInformation(isSiteAdmin, name, company, location, email, bio,
                publicRepos, publicGists, followers, following);
    }

    /**
     * Search the GitHub's users by the given keyword.
     * @param keyword The given keyword.
     * @param page The necessary search page.
     * @return The list of found users on the current search page.
     * @throws IOException If some connection problem happened (including exceeding GitHub's API limits for the amount of consequent requests).
     * @throws JSONException If some problem during parsing the JSON object happened.
     */
    public static List<User> searchUsers(String keyword, int page) throws IOException, JSONException {
        String response = Request.makeStringRequest(API_URL + SEARCH_USERS_API_URL + keyword + PAGE_URL + page);

        JSONObject jsonObject;
        jsonObject = new JSONObject(response);
        JSONArray items = jsonObject.getJSONArray("items");
        List<User> users = new ArrayList<User>();
        for(int i = 0; i < items.length(); i++) {
            users.add(parseUser(items.getJSONObject(i)));
        }
        totalUsersAmount = jsonObject.getInt("total_count");

        return users;
    }

    /**
     * Gets the GitHub's user additional information by the given login.
     * @param login The given login.
     * @param user The User instance for setting the AdditionalUserInformation instance
     *             (if user equals null, the new User instance will be created).
     * @return The User instance with set AdditionalUserInformation instance.
     * @throws IOException If some connection problem happened (including exceeding GitHub's API limits for the amount of consequent requests).
     * @throws JSONException If some problem during parsing the JSON object happened.
     */
    public static User getUser(String login, User user) throws IOException, JSONException {
        String response = Request.makeStringRequest(API_URL + GET_USER_URL + login);

        JSONObject jsonObject;
        jsonObject = new JSONObject(response);

        if(user == null) {
            user = parseUser(jsonObject);
        }
        user.setAdditionalUserInformation(parseAdditionalUserInformation(jsonObject));

        return user;
    }
}
