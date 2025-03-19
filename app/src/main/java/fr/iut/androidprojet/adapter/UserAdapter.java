package fr.iut.androidprojet.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import fr.iut.androidprojet.R;
import fr.iut.androidprojet.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> users;
    private OnUserClickListener listener;
    private OnDeleteClickListener deleteListener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(User user);
    }

    public UserAdapter(List<User> users, OnUserClickListener listener, OnDeleteClickListener deleteListener) {
        this.users = users;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.textViewName.setText(user.getFirstName() + " " + user.getLastName());

        // clic sur la partie utilisateur
        holder.userContainer.setOnClickListener(v -> listener.onUserClick(user));

        // click sur le bouton de delete (je renvoie juste l'info à MainActivity)
        holder.btnDeleteUser.setOnClickListener(v -> deleteListener.onDeleteClick(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        LinearLayout userContainer;
        ImageView btnDeleteUser;
        TextView textViewName;

        UserViewHolder(View itemView) {
            super(itemView);
            userContainer = itemView.findViewById(R.id.userContainer);
            btnDeleteUser = itemView.findViewById(R.id.btnDeleteUser);
            textViewName = itemView.findViewById(R.id.textViewName);
        }
    }
}