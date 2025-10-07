package com.dx.calcount.ui.utils

import android.app.AlertDialog
import android.content.Context
import com.dx.calcount.R

object ConfirmationDialog {
    
    fun showDeleteMealConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_confirm_delete_title)
            .setMessage(R.string.dialog_confirm_delete_meal_message)
            .setPositiveButton(R.string.dialog_confirm_delete) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(R.string.dialog_confirm_cancel, null)
            .show()
    }
    
    fun showDeleteFoodConfirmation(
        context: Context,
        onConfirm: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_confirm_delete_title)
            .setMessage(R.string.dialog_confirm_delete_food_message)
            .setPositiveButton(R.string.dialog_confirm_delete) { _, _ ->
                onConfirm()
            }
            .setNegativeButton(R.string.dialog_confirm_cancel, null)
            .show()
    }
}
