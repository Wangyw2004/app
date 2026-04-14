package com.example.no1.features.comment.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.features.comment.models.Comment;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.ReplyViewHolder> {

    private List<Comment> replies;
    private String currentUserId;
    private boolean isAdmin;
    private OnReplyClickListener replyListener;
    private OnDeleteClickListener deleteListener;

    public interface OnReplyClickListener {
        void onReplyClick(Comment reply, int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Comment reply, int position);
    }

    public CommentReplyAdapter(List<Comment> replies, String currentUserId,
                               OnReplyClickListener replyListener,
                               OnDeleteClickListener deleteListener) {
        this.replies = replies;
        this.currentUserId = currentUserId;
        this.replyListener = replyListener;
        this.deleteListener = deleteListener;
        this.isAdmin = false;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        Comment reply = replies.get(position);
        holder.bind(reply, position);
    }

    @Override
    public int getItemCount() {
        return replies == null ? 0 : replies.size();
    }

    public void updateReplies(List<Comment> newReplies) {
        this.replies = newReplies;
        notifyDataSetChanged();
    }

    public void updateCurrentUserId(String userId) {
        this.currentUserId = userId;
        notifyDataSetChanged();
    }

    class ReplyViewHolder extends RecyclerView.ViewHolder {
        private TextView authorText;
        private TextView contentText;
        private TextView timeText;
        private TextView replyButton;
        private ImageView deleteIcon;

        ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            authorText = itemView.findViewById(R.id.replyAuthor);
            contentText = itemView.findViewById(R.id.replyContent);
            timeText = itemView.findViewById(R.id.replyTime);
            replyButton = itemView.findViewById(R.id.replyToReplyButton);
            deleteIcon = itemView.findViewById(R.id.deleteReplyIcon);
        }

        void bind(Comment reply, int position) {
            authorText.setText(reply.getAuthor());
            contentText.setText(reply.getDisplayContent());

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            timeText.setText(sdf.format(reply.getCreateTime()));

            if (replyButton != null) {
                replyButton.setOnClickListener(v -> {
                    if (replyListener != null) {
                        replyListener.onReplyClick(reply, position);
                    }
                });
            }

            boolean canDelete = (currentUserId != null && reply.isAuthor(currentUserId)) || isAdmin;

            if (canDelete) {
                deleteIcon.setVisibility(View.VISIBLE);
                deleteIcon.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onDeleteClick(reply, position);
                    }
                });
            } else {
                deleteIcon.setVisibility(View.GONE);
            }
        }
    }
}