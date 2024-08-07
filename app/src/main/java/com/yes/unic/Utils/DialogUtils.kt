package com.yes.unic.Utils

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.ImageView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yes.unic.R

class DialogUtils {
    var dialog: Dialog? = null
    fun alertDialogLoadingBar(context: Context): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.progress_dialog)
        builder.setCancelable(false)
        val dialog = builder.create()
//        dialog.show()

        // Simulate some background work for demonstration purposes
        /* Thread {
             Thread.sleep(3000)
             runOnUiThread {
                 dialog.dismiss()
             }
         }.start()*/
        return dialog
    }

    fun showAlertDialog(context: Context, value: String): Dialog {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert Dialog")
        builder.setMessage("This is an example of an alert dialog with positive and negative buttons.")

        builder.setPositiveButton("Yes") { _: DialogInterface, i: Int ->
            // Put the actions you want to execute when the positive button is clicked
            // For instance, you might display a Toast message:
        }

        builder.setNegativeButton("No") { _: DialogInterface, i: Int ->
            // Put the actions you want to execute when the negative button is clicked
            // For instance, you might display a Toast message:
        }
        return builder.create()
    }

    fun show(context: Context): Dialog {
        val inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflator.inflate(R.layout.progress_dialog, null)
        dialog = Dialog(context, R.style.CustomProgressBarTheme)

        if (dialog!!.window != null) {
            dialog!!.window!!.setBackgroundDrawableResource(R.color.bg_color)
        }

        dialog!!.setContentView(view)
        dialog!!.show()
        dialog!!.setCancelable(false)
        return dialog!!
    }

    fun cameraOpen(layoutInflater: LayoutInflater, context: Activity) {
        val dialogView = layoutInflater.inflate(R.layout.fragment_dialog, null)
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogView)
        val imageViewCamera = dialogView.findViewById<ImageView>(R.id.camera)
        val imageViewGallery = dialogView.findViewById<ImageView>(R.id.gallery)
        dialog.setOnCancelListener {
//            navigateHome()
        }
        imageViewCamera.setOnClickListener {
            dialog.dismiss()
            PermissionUtils.checkCameraPermission(context)
        }
        imageViewGallery.setOnClickListener {
            dialog.dismiss()

//            checkGalleryPermission()
        }
        dialogView.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}