package org.schabi.newpipe.fragments.list.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.player.playqueue.SinglePlayQueue;
import org.schabi.newpipe.util.NavigationHelper;

public class MusicSearchFragment extends SearchFragment {

    public static MusicSearchFragment getInstance(final int serviceId, final String searchString) {
        final MusicSearchFragment searchFragment = new MusicSearchFragment();
        searchFragment.setQuery(serviceId, searchString, new String[0], "");

        if (!TextUtils.isEmpty(searchString)) {
            searchFragment.setSearchOnResume();
        }

        return searchFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Force content filter to Songs
        this.contentFilter = new String[]{YoutubeSearchQueryHandlerFactory.MUSIC_SONGS};
    }

    @Override
    public void onViewCreated(@NonNull final View rootView, final Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        final ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull final RecyclerView recyclerView,
                                  @NonNull final RecyclerView.ViewHolder viewHolder,
                                  @NonNull final RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder,
                                 final int direction) {
                final int position = viewHolder.getBindingAdapterPosition();
                if (position >= 0 && position < infoListAdapter.getItemsList().size()) {
                    final InfoItem item = infoListAdapter.getItemsList().get(position);

                    if (item instanceof StreamInfoItem) {
                        final StreamInfoItem streamItem = (StreamInfoItem) item;
                        if (direction == ItemTouchHelper.LEFT) {
                            // Queue (Enqueue)
                            NavigationHelper.enqueueOnPlayer(requireContext(),
                                    new SinglePlayQueue(streamItem));
                        } else {
                            // Play Next
                            NavigationHelper.enqueueNextOnPlayer(requireContext(),
                                    new SinglePlayQueue(streamItem));
                        }
                    }
                }
                // Reset item state (don't remove from list visually)
                infoListAdapter.notifyItemChanged(position);
            }
        });
        helper.attachToRecyclerView(itemsList);
    }
}
