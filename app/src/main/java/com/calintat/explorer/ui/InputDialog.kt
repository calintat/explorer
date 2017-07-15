package com.calintat.explorer.ui

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.EditText

import com.calintat.explorer.R

abstract class InputDialog protected constructor(context: Context, positive: String, title: String) : AlertDialog.Builder(context) {

    private val editText: EditText

    init {

        val view = View.inflate(context, R.layout.dialog_edit_text, null)

        editText = view.findViewById(R.id.dialog_edit_text) as EditText

        setView(view)

        setNegativeButton("Cancel", null)

        setPositiveButton(positive) { _, _ ->

            if (editText.length() != 0) onActionClick(editText.text.toString())
        }

        setTitle(title)
    }

    abstract fun onActionClick(text: String)

    fun setDefault(text: String) {

        editText.setText(text)

        editText.setSelection(editText.text.length)
    }
}