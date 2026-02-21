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
            "Hip Hop", "R&B", "Pop", "Rock", "Jazz", "Classical", "Electronic", "Country", "Amapiano", "Reggae"
    };
    public static final String PREF_SELECTED_GENRES = "music_selected_genres";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGenreSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void startLoading(boolean forceLoad) {
        // No loading needed
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        for (String genre : PREDEFINED_GENRES) {
            addChip(genre);
        }

        binding.btnAddCustom.setOnClickListener(v -> showAddCustomDialog());
        binding.btnDone.setOnClickListener(v -> saveAndContinue());
    }

    private void addChip(String text) {
        Chip chip = new Chip(getContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        binding.chipGroupGenres.addView(chip);
    }

    private void showAddCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Custom Genre");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String genre = input.getText().toString();
            if (!genre.isEmpty()) {
                addChip(genre);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void saveAndContinue() {
        Set<String> selectedGenres = new HashSet<>();
        for (int i = 0; i < binding.chipGroupGenres.getChildCount(); i++) {
            View child = binding.chipGroupGenres.getChildAt(i);
            if (child instanceof Chip) {
                Chip chip = (Chip) child;
                if (chip.isChecked()) {
                    selectedGenres.add(chip.getText().toString());
                }
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        prefs.edit().putStringSet(PREF_SELECTED_GENRES, selectedGenres).apply();

        NavigationHelper.openMusicHomeFragment(getParentFragmentManager());
    }
}
