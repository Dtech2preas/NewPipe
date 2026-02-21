package org.schabi.newpipe.music;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.schabi.newpipe.databinding.ItemStreamHorizontalBinding;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.util.image.PicassoHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HorizontalStreamAdapter extends RecyclerView.Adapter<HorizontalStreamAdapter.ViewHolder> {

    private final List<InfoItem> items = new ArrayList<>();
    private Consumer<InfoItem> onItemClickListener;

    public void setItems(List<? extends InfoItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(Consumer<InfoItem> listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStreamHorizontalBinding binding = ItemStreamHorizontalBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemStreamHorizontalBinding binding;

        public ViewHolder(ItemStreamHorizontalBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> {
                if (onItemClickListener != null && getBindingAdapterPosition() != RecyclerView.NO_POSITION) {
                    onItemClickListener.accept(items.get(getBindingAdapterPosition()));
                }
            });
        }

        public void bind(InfoItem item) {
            binding.itemVideoTitleView.setText(item.getName());
            if (item instanceof StreamInfoItem) {
                binding.itemUploaderView.setText(((StreamInfoItem) item).getUploaderName());
            } else {
                binding.itemUploaderView.setText(item.getName());
            }

            PicassoHelper.loadThumbnail(item.getThumbnailUrl())
                    .into(binding.itemThumbnailView);
        }
    }
}
