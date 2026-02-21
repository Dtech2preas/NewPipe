package org.schabi.newpipe.info_list.holder;

import android.view.ViewGroup;
import android.widget.ImageView;

import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem;
import org.schabi.newpipe.info_list.InfoItemBuilder;
import org.schabi.newpipe.local.history.HistoryRecordManager;

public class PlaylistInfoItemHolder extends PlaylistMiniInfoItemHolder {
    public final ImageView itemPlayIcon;

    public PlaylistInfoItemHolder(final InfoItemBuilder infoItemBuilder, final ViewGroup parent) {
        super(infoItemBuilder, R.layout.list_playlist_item, parent);
        itemPlayIcon = itemView.findViewById(R.id.itemPlayIcon);
    }

    @Override
    public void updateFromItem(final InfoItem infoItem,
                               final HistoryRecordManager historyRecordManager) {
        super.updateFromItem(infoItem, historyRecordManager);
        if (itemPlayIcon != null && infoItem instanceof PlaylistInfoItem) {
            itemPlayIcon.setOnClickListener(view -> {
                if (itemBuilder.getOnPlaylistSelectedListener() != null) {
                    itemBuilder.getOnPlaylistSelectedListener()
                            .selected((PlaylistInfoItem) infoItem);
                }
            });
        }
    }
}
