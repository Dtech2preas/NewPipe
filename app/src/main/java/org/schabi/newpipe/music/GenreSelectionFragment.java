package org.schabi.newpipe.music;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.google.android.material.chip.Chip;

import org.schabi.newpipe.databinding.FragmentGenreSelectionBinding;
import org.schabi.newpipe.fragments.BaseStateFragment;
import org.schabi.newpipe.util.NavigationHelper;

import java.util.HashSet;
import java.util.Set;

public class GenreSelectionFragment extends BaseStateFragment<Void> {

    private FragmentGenreSelectionBinding binding;
    private static final String[] PREDEFINED_GENRES = {
            "Hip Hop", "R&B", "Pop", "Rock", "Jazz", "Classical",
            "Electronic", "Country", "Amapiano", "Reggae"
    };
    public static final String PREF_SELECTED_GENRES = "music_selected_genres";

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        binding = FragmentGenreSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void startLoading(final boolean forceLoad) {
        // No loading needed
    }

    @Override
    protected void initViews(final View rootView, final Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        for (final String genre : PREDEFINED_GENRES) {
            addChip(genre);
        }

        binding.btnAddCustom.setOnClickListener(v -> showAddCustomDialog());
        binding.btnDone.setOnClickListener(v -> saveAndContinue());
    }

    private void addChip(final String text) {
        final Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        binding.chipGroupGenres.addView(chip);
    }

    private void showAddCustomDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Custom Genre");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            final String genre = input.getText().toString();
            if (!genre.isEmpty()) {
                addChip(genre);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveAndContinue() {
        final Set<String> selectedGenres = new HashSet<>();
        for (int i = 0; i < binding.chipGroupGenres.getChildCount(); i++) {
            final View child = binding.chipGroupGenres.getChildAt(i);
            if (child instanceof Chip) {
                final Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    selectedGenres.add(chip.getText().toString());
                }
            }
        }

        final SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(requireContext());
        prefs.edit().putStringSet(PREF_SELECTED_GENRES, selectedGenres).apply();

        NavigationHelper.openMusicHomeFragment(getParentFragmentManager());
    }
}
