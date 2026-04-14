package com.example.no1.features.service.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.no1.R;
import com.example.no1.features.service.models.Service;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdminServiceRecordAdapter extends RecyclerView.Adapter<AdminServiceRecordAdapter.ViewHolder> {

    private List<Service> services;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(Service service);
    }

    public AdminServiceRecordAdapter(List<Service> services, OnItemClickListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_service_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = services.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return services == null ? 0 : services.size();
    }

    public void updateServices(List<Service> newServices) {
        this.services = newServices;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvType;
        private TextView tvStatus;
        private TextView tvTime;
        private TextView tvUserName;
        private TextView tvTitle;
        private TextView tvCategory;
        private TextView tvContent;
        private TextView btnDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvContent = itemView.findViewById(R.id.tvContent);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }

        void bind(Service service) {
            // 类型
            if ("complaint".equals(service.getType())) {
                tvType.setText("投诉");
                tvType.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_orange_dark));
            } else {
                tvType.setText("报修");
                tvType.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_blue_dark));
            }

            // 状态
            tvStatus.setText(service.getStatusText());
            tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), service.getStatusColor()));

            // 时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            tvTime.setText(sdf.format(service.getCreateTime()));

            // 提交人
            tvUserName.setText("提交人：" + service.getUserName());

            // 标题
            tvTitle.setText(service.getTitle());

            // 类型
            tvCategory.setText("类型：" + service.getCategory());

            // 内容
            String content = service.getDescription();
            if (content.length() > 50) {
                content = content.substring(0, 50) + "...";
            }
            tvContent.setText(content);

            btnDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onClick(service);
                }
            });
        }
    }
}