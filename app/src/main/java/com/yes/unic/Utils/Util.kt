package com.yes.unic.Utils

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yes.unic.R
import com.yes.unic.Utils.Constant.Companion.CONVERT_RESPONSE
import com.yes.unic.Utils.Constant.Companion.GET_CAMERA_IMAGE_FILE
import com.yes.unic.Utils.Constant.Companion.GET_CAMERA_VALUE
import com.yes.unic.Utils.Constant.Companion.GET_GALLERY_VALUE
import com.yes.unic.Utils.Constant.Companion.REQUEST_CAMERA_PERMISSION
import com.yes.unic.listener.AdapterListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar

class Util {
    private lateinit var imageFile: File
    private lateinit var uri: Uri

    fun getUriFromPath(filePath: String, context: Context): Uri {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            return Uri.parse(filePath.toString());
            return FileProvider.getUriForFile(
                context!!,
                context!!.getPackageName() + ".provider",
                File(filePath)
            )
        } else {
            return Uri.fromFile(File(filePath));
        }
    }

    fun createImageFile(context: Context): File {
        val folder =
            File(context!!.getExternalFilesDir(null)?.absoluteFile, Constant.IMAGE_DOC_DIR_NAME)
        folder.mkdirs()

        val file = File(folder, System.currentTimeMillis().toString() + ".JPEG")
        if (file.exists())
            file.delete()
        file.createNewFile()
        return file;
    }

    fun uriToBitmap(context: Context, uri: Uri): Bitmap {
        var bitmap: Bitmap? = null
        try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap!!
    }

    fun getFilePathFromContentUri(uri: Uri, context: Context): String? {
        val filePath: String?
        var contentUri: Uri? = null
        val id: String = try {
            DocumentsContract.getDocumentId(uri)
        } catch (e: IllegalArgumentException) {
            ""
        }
        if (id.isNotEmpty()) {
            val regex: Regex = "-?\\d+(\\.\\d+)?".toRegex()
            val numeric = id.matches(regex)
            if (numeric) {
                contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
            }
        } else {
            contentUri = uri
        }
        val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
        var cursor: Cursor? = null
        if (contentUri != null) {
            cursor = context.contentResolver.query(contentUri, projection, null, null, null)
        }
        if (cursor != null) {
            cursor.moveToFirst()
            val column_index = cursor.getColumnIndexOrThrow(projection[0])
            filePath = cursor.getString(column_index)
            cursor.close()
        } else {
            filePath = uri.path
        }
        Log.e("##Path", "--$filePath")
        return filePath
    }


    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Months are zero-based (0-11)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        Log.e("##ImageName", "$year-$month-$day $hour:$minute:$second")
        return "$year-$month-$day $hour:$minute:$second"
    }

    fun getImageResolution(imagePath: String): Pair<Int, Int>? {
        val file = File(imagePath)
        if (!file.exists()) {
            Log.e("##ImageResolution", "--File Not")
            return null
        }
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(imagePath, options)
        Log.e("##ImageResolution", "--" + Pair(options.outWidth, options.outHeight))
        return Pair(options.outWidth, options.outHeight)
    }


    fun getFileSizeInMB(filePath: String): Double {
        val file = File(filePath)
        val bytes = file.length()
        val kilobytes = bytes / 1024
        val megabytes = kilobytes / 1024.0
        return megabytes
    }

    fun getImageFileSize(imagePath: String): String {
        val file = File(imagePath)
        if (file.exists()) {
            val size: String = getFileSizeInMB(file.length().toString()).toString()
            Log.e("##ImageSize", "-$size")
            return size
        } else {
            return ""
        }
    }


    fun displayPopup(
        layoutInflater: LayoutInflater,
        activity: Activity,
        intent: Intent,
        adapterListener: AdapterListener
    ) {
        val dialogView = layoutInflater.inflate(R.layout.fragment_dialog, null)
        val dialog = BottomSheetDialog(activity)
        dialog.setContentView(dialogView)
        val imageViewCamera = dialogView.findViewById<ImageView>(R.id.camera)
        val imageViewGallery = dialogView.findViewById<ImageView>(R.id.gallery)

        dialog.setOnCancelListener {
            navigateHome(intent, activity)
        }
        imageViewCamera.setOnClickListener {
            dialog.dismiss()
            checkCameraPermission(activity, adapterListener)
        }
        imageViewGallery.setOnClickListener {
            dialog.dismiss()

            checkGalleryPermission(activity,adapterListener)
        }
        dialogView.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()


    }
    private fun checkGalleryPermission(activity: Activity, adapterListener: AdapterListener) {
        // Check gallery permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openGallery(activity,adapterListener)
        } else {
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request gallery permission
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constant.REQUEST_GALLERY_PERMISSION
                )
            } else {
                openGallery(activity, adapterListener)
            }
        }
    }

      fun openGallery(activity: Activity, adapterListener: AdapterListener) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (galleryIntent.resolveActivity(activity.packageManager) != null) {
            adapterListener.adapterData(GET_GALLERY_VALUE, galleryIntent)
//            galleryLauncher.launch(galleryIntent)
        } else {
            // Handle case where there's no app to handle the gallery intent.
        }
    }

    private fun checkCameraPermission(activity: Activity, adapterListener: AdapterListener) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
            )
        } else {
            getCameraPicture(activity, adapterListener)
        }


    }

     fun getCameraPicture(activity: Activity, adapterListener: AdapterListener) {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (null != takePictureIntent.resolveActivity(activity.packageManager)) {
                try {
                    imageFile = createImageFile(activity)
                    adapterListener.adapterData(GET_CAMERA_IMAGE_FILE, imageFile)
                } catch (ex: Exception) {
                    Log.e("##Exception", "${ex.message}")
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    uri = Uri.fromFile(imageFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                } else {
                    uri = FileProvider.getUriForFile(
                        activity, activity.packageName + ".provider", imageFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                }
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                adapterListener.adapterData(GET_CAMERA_VALUE, takePictureIntent)
            }
        } catch (e: Exception) {
            Log.d("##PathFromCamera", e.toString())
        }
    }

    fun saveImageToGallery(
        context: Context,
        bitmap: Bitmap,
        format: String,
        quality: Int,
        adapterListener: AdapterListener
    ) {
        GlobalScope.launch(Dispatchers.IO) {
//            activity.runOnUiThread { dialog.show() }
            val relativeLocation =
                Environment.DIRECTORY_PICTURES + File.separator + Constant.FOLDER_NAME
            val fileNameTime: String = "IMG-" + getCurrentDateTime().replace(":", "_")
//            var convertedPath = Constant.PATH + "$relativeLocation/$fileNameTime$+.jpg"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileNameTime)
                put(MediaStore.MediaColumns.MIME_TYPE, format)
                put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
            }
            withContext(Dispatchers.Main) {
                // Update the progress bar to indicate completion
//                updateProgressBar(100)
            }

            val contentResolver = context.contentResolver
            val imageUri =
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            try {
                imageUri?.let { uri ->
                    val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
                    outputStream?.use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                        val savedPath = context.getFileStreamPath(fileNameTime)
                        Log.e("##Sucee", "-$savedPath")
                        adapterListener.adapterData(CONVERT_RESPONSE, savedPath.toString())
                    }
                }
            } catch (e: Exception) {
                /* runOnUiThread {
                     dialog.dismiss()
                 }*/

                Log.e("##error", "--$e")
                e.printStackTrace()
            }
        }
    }


    private fun navigateHome(intent: Intent, activity: Activity) {
        val intent = intent
        // Define the transition animation
        val options = ActivityOptionsCompat.makeCustomAnimation(
            activity,
            R.anim.slide_in_right, // Enter animation
            R.anim.slide_out_left // Exit animation
        )

        ActivityCompat.startActivity(activity, intent, options.toBundle())
    }


}