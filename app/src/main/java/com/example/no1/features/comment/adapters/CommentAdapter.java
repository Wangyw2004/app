package com.example.no1.features.comment.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.features.comment.models.Comment;
import com.example.no1.features.comment.views.CommentDetailActivity;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private String currentUserId;
    private String postId;
    private OnReplyClickListener replyListener;
    private OnDeleteClickListener deleteListener;

    public interface OnReplyClickListener {
        void onReplyClick(Comment comment);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Comment comment, int position);
    }

    public CommentAdapter(List<Comment> comments, String postId, String currentUserId,
                          OnReplyClickListener replyListener,
                          OnDeleteClickListener deleteListener) {
        this.comments = comments;
        this.postId = postId;
        this.currentUserId = currentUserId;
        this.replyListener = replyListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.bind(comment, position);

        int replyCount = comment.getReplyCount();

        if (replyCount > 0) {
            Comment latestReply = comment.getLatestReply();
            if (latestReply != null) {
                holder.replyPreview.setVisibility(View.VISIBLE);
                holder.latestReplyAuthor.setText(latestReply.getAuthor());

                String replyContent = latestReply.getReplyTo() != null ?
                        "回复 @" + latestReply.getReplyTo() + "：" + latestReply.getContent() :
                        latestReply.getContent();
                holder.latestReplyContent.setText(replyContent);

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                holder.latestReplyTime.setText(sdf.format(latestReply.getCreateTime()));
            }

            holder.viewAllButton.setVisibility(View.VISIBLE);
            holder.viewAllButton.setText("共 " + replyCount + " 条回复");
        } else {
            holder.replyPreview.setVisibility(View.GONE);
            holder.viewAllButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    public void updateComments(List<Comment> newComments) {
        this.comments = newComments;
        notifyDataSetChanged();
    }

    public void updateCurrentUserId(String userId) {
        this.currentUserId = userId;
        notifyDataSetChanged();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView authorText;
        private TextView contentText;
        private TextView timeText;
        private TextView replyIndicator;
        private TextView replyButton;
        private ImageView deleteIcon;
        private LinearLayout replyPreview;
        private TextView latestReplyAuthor;
        private TextView latestReplyContent;
        private TextView latestReplyTime;
        private TextView viewAllButton;

        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            authorText = itemView.findViewById(R.id.commentAuthor);
            contentText = itemView.findViewById(R.id.commentContent);
            timeText = itemView.findViewById(R.id.commentTime);
            replyIndicator = itemView.findViewById(R.id.replyIndicator);
            replyButton = itemView.findViewById(R.id.replyButton);
            deleteIcon = itemView.findViewById(R.id.deleteCommentIcon);
            replyPreview = itemView.findViewById(R.id.replyPreview);
            latestReplyAuthor = itemView.findViewById(R.id.latestReplyAuthor);
            latestReplyContent = itemView.findViewById(R.id.latestReplyContent);
            latestReplyTime = itemView.findViewById(R.id.latestReplyTime);
            viewAllButton = itemView.findViewById(R.id.viewAllRepliesButton);
        }

        void bind(Comment comment, int position) {
            authorText.setText(comment.getAuthor());

            if (!comment.isTopLevel() && comment.getReplyTo() != null) {
                replyIndicator.setVisibility(View.VISIBLE);
                replyIndicator.setText("回复 @" + comment.getReplyTo() + "：");
            } else {
                replyIndicator.setVisibility(View.GONE);
            }

            contentText.setText(comment.getContent());

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            timeText.setText(sdf.format(comment.getCreateTime()));

            if (currentUserId != null && comment.isAuthor(currentUserId)) {
                deleteIcon.setVisibility(View.VISIBLE);
                deleteIcon.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onDeleteClick(comment, position);
                    }
                });
            } else {
                deleteIcon.setVisibility(View.GONE);
            }

            replyButton.setOnClickListener(v -> {
                if (replyListener != null) {
                    replyListener.onReplyClick(comment);
                }
            });

            viewAllButton.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CommentDetailActivity.class);
                intent.putExtra("post_id", postId);
                intent.putExtra("comment_id", comment.getId());
                v.getContext().startActivity(intent);
            });
        }
    }
}