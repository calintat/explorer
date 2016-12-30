package com.calintat.explorer.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.calintat.explorer.R;

public abstract class InputDialog extends AlertDialog.Builder {

    private final EditText editText;

    protected InputDialog(Context context, String positive, String title) {

        super(context);

        View view = View.inflate(context, R.layout.dialog_edit_text, null);

        editText = (EditText) view.findViewById(R.id.dialog_edit_text);

        setView(view);

        setNegativeButton("Cancel", null);

        setPositiveButton(positive, (dialog, which) -> {

            if (editText.length() != 0) onActionClick(editText.getText().toString());
        });

        setTitle(title);
    }

    public abstract void onActionClick(String text);

    public void setDefault(String text) {

        editText.setText(text);

        editText.setSelection(editText.getText().length());
    }
}