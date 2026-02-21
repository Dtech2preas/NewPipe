package org.schabi.newpipe.music;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.schabi.newpipe.NewPipeDatabase;
import org.schabi.newpipe.local.playlist.LocalPlaylistFragment;
import org.schabi.newpipe.local.playlist.LocalPlaylistManager;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MusicLibraryFragment extends Fragment {

    private static final String LIBRARY_PLAYLIST_NAME = "My Library";
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            LocalPlaylistManager playlistManager = new LocalPlaylistManager(NewPipeDatabase.getInstance(requireContext()));

            disposables.add(playlistManager.getPlaylistId(LIBRARY_PLAYLIST_NAME)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::openPlaylist, error -> {
                        // Ignore error, maybe try create
                    }, () -> {
                        createAndOpenPlaylist(playlistManager);
                    }));
        }
    }

    private void createAndOpenPlaylist(LocalPlaylistManager manager) {
        disposables.add(manager.createEmptyPlaylist(LIBRARY_PLAYLIST_NAME)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::openPlaylist, Throwable::printStackTrace));
    }

    private void openPlaylist(long id) {
        if (!isAdded()) return;

        getParentFragmentManager().beginTransaction()
                .replace(getId(), LocalPlaylistFragment.getInstance(id, LIBRARY_PLAYLIST_NAME))
                .commitAllowingStateLoss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new View(getContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
