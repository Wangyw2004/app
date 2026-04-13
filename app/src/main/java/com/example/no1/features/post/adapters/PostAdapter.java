package com.example.no1.features.post.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.features.post.models.Post;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> posts;
    private OnPostClickListener listener;
    private OnLikeClickListener likeListener;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public interface OnLikeClickListener {
        void onLikeClick(Post post, int position);
    }

    public PostAdapter(List<Post> posts, OnPostClickListener listener, OnLikeClickListener likeListener) {
        this.posts = posts;
        this.listener = listener;
        this.likeListener = likeListener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, position);
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView contentText;
        private TextView authorText;
        private TextView timeText;
        private TextView likeCountText;
        private ImageView likeIcon;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.postTitle);
            contentText = itemView.findViewById(R.id.postContent);
            authorText = itemView.findViewById(R.id.postAuthor);
            timeText = itemView.findViewById(R.id.postTime);
            likeCountText = itemView.findViewById(R.id.likeCount);
            likeIcon = itemView.findViewById(R.id.likeIcon);
        }

        void bind(Post post, int position) {
            titleText.setText(post.getTitle());
            contentText.setText(post.getContent());
            authorText.setText(post.getAuthor());

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            timeText.setText(sdf.format(post.getCreateTime()));

            likeCountText.setText(String.valueOf(post.getLikeCount()));
            likeIcon.setSelected(post.isLiked());

            // 点击帖子查看详情
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPostClick(post);
                }
            });

            // 点赞
            likeIcon.setOnClickListener(v -> {
                if (likeListener != null) {
                    likeListener.onLikeClick(post, position);
                }
            });
        }
    }
}