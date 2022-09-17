package ru.otus.presentation

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ru.otus.R

class CloseDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.exit_title)
            .setMessage(R.string.exit_question)
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
            .setPositiveButton(android.R.string.ok) { _, _ -> activity?.finish()}
            .create()
    }
}