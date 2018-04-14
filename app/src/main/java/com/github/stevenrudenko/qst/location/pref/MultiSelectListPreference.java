package com.github.stevenrudenko.qst.location.pref;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseBooleanArray;

import com.github.stevenrudenko.qst.location.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Multi-select list preference with selection limit. */
public class MultiSelectListPreference extends android.preference.MultiSelectListPreference {

    private String summary;
    private int minLimit;

    public MultiSelectListPreference(Context context) {
        super(context);
        init(context, null);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        // Retrieve the Preference summary attribute since it's private in the Preference class.
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiSelectListPreference);
        summary = a.getString(R.styleable.MultiSelectListPreference_summary);
        minLimit = a.getInt(R.styleable.MultiSelectListPreference_minLimit, 0);
        a.recycle();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        final boolean[] checkedItems = new boolean[getEntries().length];
        getValues().forEach(value -> checkedItems[findIndexOfValue(value)] = true);
        builder.setMultiChoiceItems(getEntries(), checkedItems, (d, i, b) -> {
            final AlertDialog dialog = (AlertDialog) d;
            final boolean valid = dialog.getListView().getCheckedItemCount() >= minLimit;
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(valid);
        });
        builder.setPositiveButton(getPositiveButtonText(), (d, listener) -> {
            final AlertDialog dialog = (AlertDialog) d;
            final CharSequence[] entryValues = getEntryValues();
            final SparseBooleanArray selection = dialog.getListView().getCheckedItemPositions();
            final Set<String> values = new HashSet<>(selection.size());
            for (int i = 0; i < selection.size(); i++) {
                final int key = selection.keyAt(i);
                final boolean selected = selection.get(key);
                if (selected) {
                    values.add(entryValues[key].toString());
                }
            }
            setValues(values);
        });
    }

    /**
     * Returns the summary of this ListPreference. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current entry
     * value will be substituted in its place.
     *
     * @return the summary with appropriate string substitution
     */
    @Override
    public CharSequence getSummary() {
        if (summary == null) {
            return super.getSummary();
        } else {
            final CharSequence[] entries = getSelectedEntries();
            return String.format(summary, TextUtils.join(", ", entries));
        }
    }

    public CharSequence[] getSelectedEntries() {
        final CharSequence[] entries = getEntries();
        final Set<String> values = getValues();
        final int size = values.size();
        final List<Integer> indexes = new ArrayList<>(size);
        values.forEach(value -> indexes.add(findIndexOfValue(value)));
        Collections.sort(indexes);
        final List<CharSequence> selection = new ArrayList<>(size);
        indexes.forEach(idx -> selection.add(entries[idx]));
        final CharSequence[] result = new CharSequence[size];
        selection.toArray(result);
        return result;
    }

}
