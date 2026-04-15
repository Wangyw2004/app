package com.example.no1.features.post.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.no1.R;
import java.io.File;
import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {

    private List<String> imagePaths;

    public ImagePagerAdapter(List<String> imagePaths) {
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_pager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);
        holder.bind(imagePath);
    }

    @Override
    public int getItemCount() {
        return imagePaths == null ? 0 : imagePaths.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.pagerImage);
        }

        void bind(String imagePath) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // 使用 Glide 或系统方式加载
                try {
                    Glide.with(itemView.getContext())
                            .load(imageFile)
                            .into(imageView);
                } catch (Exception e) {
                    imageView.setImageURI(android.net.Uri.fromFile(imageFile));
                }
            }
        }
    }
}