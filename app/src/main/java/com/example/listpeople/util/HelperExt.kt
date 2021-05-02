package com.example.listpeople.util

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner

fun <T> Array<out T>.toHashSet(): HashSet<T> {
    return toCollection(HashSet<T>((size)))
}

fun showErrorDialog(activity: Activity,
                    title: String? = null,
                    message: String? = null,
                    positiveButtonText: String? = null,
                    positiveListener: DialogInterface.OnClickListener? = null,
                    negativeButtonText: String? = null,
                    negativeListener: DialogInterface.OnClickListener? = null){

    val alertDialog: AlertDialog.Builder = AlertDialog.Builder(activity)
        alertDialog.setTitle(title?:"Error")
        alertDialog.setMessage(message?:"Something went wrong with your request. Please try again later.")
    positiveButtonText?.let { positiveButtonText ->
        alertDialog.setPositiveButton(
            positiveButtonText,
            positiveListener)
    }
    negativeButtonText?.let { negativeButtonText ->
        alertDialog.setNegativeButton(
            negativeButtonText,
            DialogInterface.OnClickListener { _, _ -> })
    }
    alertDialog.setCancelable(false)
    alertDialog.show()
}
