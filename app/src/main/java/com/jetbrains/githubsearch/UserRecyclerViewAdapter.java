/**
 * The task is done by Anton Rigin in 2017.
 */

package com.jetbrains.githubsearch;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jetbrains.githubsearch.UserFragment.OnListFragmentInteractionListener;
import com.jetbrains.githubsearch.models.User;

import java.util.List;

/**
 * Represents the UserFragment's RecyclerView's adapter.
 */
public class UserRecyclerViewAdapter extends RecyclerView.Adapter<UserRecyclerViewAdapter.ViewHolder> {

    /**
     * The adapter's content - the users list.
     */
    private final List<User> users;

    /**
     * The OnListFragmentInteractionListener instance.
     */
    private final OnListFragmentInteractionListener listener;

    /**
     * The UserFragment which has created this adapter.
     */
    private UserFragment userFragment;

    /**
     * Constructor.
     * @param users The adapter's content - the users list.
     * @param listener The OnListFragmentInteractionListener instance.
     * @param userFragment The UserFragment which has created this adapter.
     */
    public UserRecyclerViewAdapter(List<User> users, OnListFragmentInteractionListener listener, UserFragment userFragment) {
        this.users = users;
        this.listener = listener;
        this.userFragment = userFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_user, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.user = users.get(position);
        holder.loginTextView.setText(holder.user.getLogin());
        holder.loadAvatar();
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Represents the holder of the users list element.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        /**
         * The View instance.
         */
        private final View view;

        /**
         * The ImageView showing the user's avatar.
         */
        private final ImageView avatarImageView;

        /**
         * The TextView showing the user's login.
         */
        private final TextView loginTextView;

        /**
         * The user in this list element.
         */
        private User user;

        private ImageLoad imageLoad;

        /**
         * Constructor.
         * @param view The View instance.
         */
        public ViewHolder(final View view) {
            super(view);
            this.view = view;
            avatarImageView = (ImageView) view.findViewById(R.id.avatarImageView);
            loginTextView = (TextView) view.findViewById(R.id.loginTextView);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Storage.setCurrentUser(user);
                    Intent intent = new Intent(userFragment.getContext(), UserActivity.class);
                    userFragment.startActivity(intent);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    String[] items = null;

                    if(!((MainActivity) (userFragment.getActivity())).checkFavourite(user)) {
                        items = new String[] {"Add to favourites"};
                    } else {
                        items = new String[] {"Remove from favourites"};
                    }

                    new AlertDialog.Builder(view.getContext()).setTitle("Options").setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!((MainActivity) (userFragment.getActivity())).checkFavourite(user)) {
                                ((MainActivity) (userFragment.getActivity())).addFavourite(user);
                            } else {
                                ((MainActivity) (userFragment.getActivity())).removeFavourite(user);
                                if(userFragment.getUserFragmentType() == UserFragment.UserFragmentType.FAVOURITES) {
                                    users.remove(user);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    }).show();

                    return true;
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + loginTextView.getText() + "'";
        }

        /**
         * Loads the user's avatar image using AsyncTask.
         */
        void loadAvatar() {
            if(user.getAvatar() == null) {
                avatarImageView.setImageResource(R.drawable.noavatar);
                if(imageLoad == null) {
                    imageLoad = new ImageLoad(user, this, null);
                    imageLoad.execute(user.getAvatarUrl());
                }
            } else {
                avatarImageView.setImageBitmap(user.getAvatar());
            }
        }
    }
}
