package com.example.no1.features.post.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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
        private TextView authorText;
        private TextView timeText;
        private TextView titleText;
        private TextView contentText;
        private TextView likeCountText;
        private TextView commentCountText;
        private ImageView likeIcon;
        private LinearLayout imageContainer;

        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            authorText = itemView.findViewById(R.id.postAuthor);
            timeText = itemView.findViewById(R.id.postTime);
            titleText = itemView.findViewById(R.id.postTitle);
            contentText = itemView.findViewById(R.id.postContent);
            likeCountText = itemView.findViewById(R.id.likeCount);
            commentCountText = itemView.findViewById(R.id.commentCount);
            likeIcon = itemView.findViewById(R.id.likeIcon);
            imageContainer = itemView.findViewById(R.id.imageContainer);
        }

        void bind(Post post, int position) {
            authorText.setText(post.getAuthor());

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            timeText.setText(sdf.format(post.getCreateTime()));

            titleText.setText(post.getTitle());
            contentText.setText(post.getContent());
            likeCountText.setText(String.valueOf(post.getLikeCount()));
            likeIcon.setSelected(post.isLiked());
            // 设置评论数
            commentCountText.setText(String.valueOf(post.getCommentCount()));
            // 显示图片
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                imageContainer.setVisibility(View.VISIBLE);
                imageContainer.removeAllViews();

                int maxImages = Math.min(post.getImages().size(), 3);
                for (int i = 0; i < maxImages; i++) {
                    String imagePath = post.getImages().get(i);
                    View imageView = LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_post_image, imageContainer, false);
                    ImageView iv = imageView.findViewById(R.id.postImage);

                    Glide.with(itemView.getContext())
                            .load(new java.io.File(imagePath))
                            .centerCrop()
                            .into(iv);

                    imageContainer.addView(imageView);
                }

                // 如果超过3张，显示"+N"
                if (post.getImages().size() > 3) {
                    TextView moreText = new TextView(itemView.getContext());
                    moreText.setText("+" + (post.getImages().size() - 3));
                    moreText.setGravity(android.view.Gravity.CENTER);
                    moreText.setBackgroundColor(0x88000000);
                    moreText.setTextColor(0xFFFFFFFF);
                    moreText.setPadding(16, 16, 16, 16);
                    imageContainer.addView(moreText);
                }
            } else {
                imageContainer.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPostClick(post);
                }
            });

            likeIcon.setOnClickListener(v -> {
                if (likeListener != null) {
                    likeListener.onLikeClick(post, position);
                }
            });
        }
    }
}