/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jetbrains.githubsearch.models.User;

import java.util.List;

/**
 * Represents the fragment containing the list of users.
 */
public class UserFragment extends Fragment {

    /**
     * The enumeration.
     * Represents the current "type" of the RecyclerView's adapter's content.
     */
    public enum UserFragmentType {
        SEARCH_RESULTS, FAVOURITES
    }

    private static final String ARG_COLUMN_COUNT = "column-count";

    private int columnCount = 1;

    private OnListFragmentInteractionListener listener;

    /**
     * The RecyclerView instance of this fragment.
     */
    private RecyclerView recyclerView;

    /**
     * The RecyclerView's OnScrollListener.
     * Handles the events of the scrolling and loads new pages of the search results.
     */
    private RecyclerView.OnScrollListener onScrollListener;

    /**
     * The current UserFragmentType value.
     */
    private UserFragmentType userFragmentType = UserFragmentType.SEARCH_RESULTS;

    public UserFragment() {
        setRetainInstance(true);
    }

    @SuppressWarnings("unused")
    public static UserFragment newInstance(int columnCount) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }

            if(onScrollListener != null) {
                recyclerView.setOnScrollListener(onScrollListener);
            }

            if(recyclerView.getAdapter() == null) {
                setAdapter();
            }
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            listener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(User item);
    }

    /**
     * Sets the RecyclerView's OnScrollListener which handles the events of the scrolling and
     * loads new pages of the search results.
     * @param onScrollListener The RecyclerView's OnScrollListener.
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    /**
     * Returns the current UserFragmentType value.
     * @return The current UserFragmentType value.
     */
    public UserFragmentType getUserFragmentType() {
        return userFragmentType;
    }

    /**
     * Sets the current UserFragmentType value.
     * @param userFragmentType The current UserFragmentType value.
     */
    public void setUserFragmentType(UserFragmentType userFragmentType) {
        this.userFragmentType = userFragmentType;
        if(recyclerView != null) {
            setAdapter();
        }
    }

    /**
     * Sets the list of users - the content of the fragment's RecyclerView's adapter.
     * @param users The list of users.
     */
    public void setUserFragmentList(List<User> users) {
        recyclerView.setAdapter(new UserRecyclerViewAdapter(users, listener, this));
    }

    /**
     * Returns the fragment's RecyclerView's adapter.
     * @return The fragment's RecyclerView's adapter.
     */
    public UserRecyclerViewAdapter getAdapter() {
        return (UserRecyclerViewAdapter) recyclerView.getAdapter();
    }

    /**
     * Creates and sets the fragment's RecyclerView's adapter using the current UserFragmentType value.
     */
    private void setAdapter() {
        switch (userFragmentType) {
            case SEARCH_RESULTS:
                recyclerView.setAdapter(new UserRecyclerViewAdapter(Storage.getUsers(), listener, this));
                break;

            case FAVOURITES:
                recyclerView.setAdapter(new UserRecyclerViewAdapter(Storage.getFavourites(), listener, this));
                break;
        }
    }
}
