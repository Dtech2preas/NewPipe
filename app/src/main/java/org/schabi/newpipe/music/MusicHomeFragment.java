package org.schabi.newpipe.music;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.schabi.newpipe.databinding.FragmentMusicHomeBinding;
import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.services.youtube.linkHandler.YoutubeSearchQueryHandlerFactory;
import org.schabi.newpipe.fragments.BaseStateFragment;
import org.schabi.newpipe.local.history.HistoryRecordManager;
import org.schabi.newpipe.util.DeviceUtils;
import org.schabi.newpipe.util.ExtractorHelper;
import org.schabi.newpipe.util.NavigationHelper;
import org.schabi.newpipe.util.ServiceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicHomeFragment extends BaseStateFragment<Void> {

    private FragmentMusicHomeBinding binding;
    private HorizontalStreamAdapter historyAdapter;
    private HistoryRecordManager historyRecordManager;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMusicHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        binding = null;
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        setTitle(requireContext().getString(org.schabi.newpipe.R.string.action_home));
        historyRecordManager = new HistoryRecordManager(requireContext());

        setupHistory();
        setupGenres();
    }

    private void setupHistory() {
        historyAdapter = new HorizontalStreamAdapter();
        binding.rvRecentlyPlayed.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.rvRecentlyPlayed.setAdapter(historyAdapter);
        historyAdapter.setOnItemClickListener(item -> NavigationHelper.openVideoDetail(requireContext(), item.getServiceId(), item.getUrl(), item.getName(), null, false));

        // Load history
        disposables.add(historyRecordManager.getStreamHistorySortedById()
                .firstElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(history -> {
                    List<InfoItem> items = new ArrayList<>();
                    for (var entry : history) {
                        items.add(entry.toStreamInfoItem());
                    }
                    if (items.isEmpty()) {
                        binding.tvRecentlyPlayed.setVisibility(View.GONE);
                        binding.rvRecentlyPlayed.setVisibility(View.GONE);
                    } else {
                        historyAdapter.setItems(items);
                    }
                }, Throwable::printStackTrace));
    }

    private void setupGenres() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Set<String> genres = prefs.getStringSet(GenreSelectionFragment.PREF_SELECTED_GENRES, Collections.emptySet());

        int padding = DeviceUtils.dpToPx(16, requireContext());

        for (String genre : genres) {
            // Inflate header and list manually or use a layout
            TextView header = new TextView(getContext());
            header.setText(genre);
            header.setTextAppearance(getContext(), androidx.appcompat.R.style.TextAppearance_AppCompat_Title);
            header.setPadding(padding, padding, padding, padding / 2);

            RecyclerView list = new RecyclerView(getContext());
            list.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
            list.setPadding(padding, 0, padding, 0);
            list.setClipToPadding(false);

            HorizontalStreamAdapter adapter = new HorizontalStreamAdapter();
            list.setAdapter(adapter);
            adapter.setOnItemClickListener(item -> NavigationHelper.openVideoDetail(requireContext(), item.getServiceId(), item.getUrl(), item.getName(), null, false));

            binding.homeContentContainer.addView(header);
            binding.homeContentContainer.addView(list);

            // Fetch content
            int serviceId = ServiceHelper.getSelectedServiceId(requireContext());

            disposables.add(ExtractorHelper.searchFor(serviceId, genre + " music", Collections.singletonList(YoutubeSearchQueryHandlerFactory.MUSIC_SONGS), "")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        adapter.setItems(result.getRelatedItems());
                    }, error -> {
                        // Error handling
                    }));
        }
    }

    @Override
    public void startLoading(boolean forceLoad) {}
}
