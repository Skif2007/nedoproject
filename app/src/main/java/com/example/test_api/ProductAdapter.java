package com.example.test_api;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private List<Product> products;
    private OnItemClickListener listener;
    private ImageLoader imageLoader = new ImageLoader();

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        imageLoader.loadImage(product.getImageUrl(), holder.imageView);
        holder.textName.setText(product.getName());
        holder.textPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.itemView.setOnClickListener(v -> listener.onItemClick(product));
    }

    @Override
    public int getItemCount() { return products.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textName, textPrice;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            textName = itemView.findViewById(R.id.product_name);
            textPrice = itemView.findViewById(R.id.product_price);
        }
    }
}