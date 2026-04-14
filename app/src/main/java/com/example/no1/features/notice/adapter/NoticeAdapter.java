package com.example.no1.features.notice.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.features.notice.models.Notice;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {

    private List<Notice> notices;
    private boolean isAdmin;
    private OnItemClickListener listener;
    private OnEditClickListener editListener;
    private OnDeleteClickListener deleteListener;

    public interface OnItemClickListener {
        void onClick(Notice notice);
    }

    public interface OnEditClickListener {
        void onEdit(Notice notice);
    }

    public interface OnDeleteClickListener {
        void onDelete(Notice notice);
    }

    public NoticeAdapter(List<Notice> notices, boolean isAdmin,
                         OnItemClickListener listener,
                         OnEditClickListener editListener,
                         OnDeleteClickListener deleteListener) {
        this.notices = notices;
        this.isAdmin = isAdmin;
        this.listener = listener;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notice notice = notices.get(position);
        holder.bind(notice);
    }

    @Override
    public int getItemCount() {
        return notices == null ? 0 : notices.size();
    }

    public void updateNotices(List<Notice> newNotices) {
        this.notices = newNotices;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvContent;
        private TextView tvTime;
        private TextView tvViewCount;
        private LinearLayout adminActions;
        private ImageView btnEdit;
        private ImageView btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvViewCount = itemView.findViewById(R.id.tvViewCount);
            adminActions = itemView.findViewById(R.id.adminActions);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Notice notice) {
            tvTitle.setText(notice.getTitle());

            String content = notice.getContent();
            if (content.length() > 60) {
                content = content.substring(0, 60) + "...";
            }
            tvContent.setText(content);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            tvTime.setText(sdf.format(notice.getCreateTime()));

            tvViewCount.setText("👁 " + notice.getViewCount());

            // 管理员显示操作按钮
            if (isAdmin) {
                adminActions.setVisibility(View.VISIBLE);
                btnEdit.setOnClickListener(v -> {
                    if (editListener != null) {
                        editListener.onEdit(notice);
                    }
                });
                btnDelete.setOnClickListener(v -> {
                    if (deleteListener != null) {
                        deleteListener.onDelete(notice);
                    }
                });
            } else {
                adminActions.setVisibility(View.GONE);
            }

            // 点击查看详情
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(notice);
                }
            });
        }
    }
}