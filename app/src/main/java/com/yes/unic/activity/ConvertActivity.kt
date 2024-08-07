package com.yes.unic.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContract
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.yes.unic.HomeActivity
import com.yes.unic.R
import com.yes.unic.Utils.Constant
import com.yes.unic.Utils.Constant.Companion.REQUEST_CAMERA_PERMISSION
import com.yes.unic.Utils.Constant.Companion.REQUEST_GALLERY_PERMISSION
import com.yes.unic.Utils.DialogUtils
import com.yes.unic.Utils.Util
import com.yes.unic.databinding.ActivityConvertBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream

class ConvertActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityConvertBinding
    private var imageUtils: Util = Util()
    private var alertDialog: DialogUtils = DialogUtils()
    private var progressBarStatus = 0
    private var dummy: Int = 0
    private var path: String? = ""
    private var convertedPath: String? = ""
    private lateinit var format: String
    private lateinit var endFormat: String
    private lateinit var imageFile: File
    private lateinit var imageData: Bitmap
    private lateinit var uri: Uri
    private lateinit var mDelayHandler: Handler
    private var progress: ProgressBar? = null
    private var dialogUtils = DialogUtils()
    private lateinit var dialog: Dialog



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConvertBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        format = Constant.IMAGE_JPGE
        endFormat = Constant.JPG_FORMAT
        displayPopup()
        mDelayHandler = Handler()
        binding.beforeConvertingLayout.visibility = VISIBLE
        binding.afterConvertingLayout.visibility = GONE
        dialog = dialogUtils.alertDialogLoadingBar(this)
        binding.jpgTextView.setOnClickListener(this)
        binding.pngTextView.setOnClickListener(this)
        binding.webpTextView.setOnClickListener(this)
        binding.convertingTextView.setOnClickListener(this)
        binding.convertedImageView.setOnClickListener(this)
        binding.okTextView.setOnClickListener(this)
        binding.backArrow.setOnClickListener(this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCameraPicture()
                } else {
                    checkCameraPermission()
                }
            }

            REQUEST_GALLERY_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    checkGalleryPermission()
                }
            }
        }
    }


    private fun displayPopup() {
        val dialogView = layoutInflater.inflate(R.layout.fragment_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogView)
        val imageViewCamera = dialogView.findViewById<ImageView>(R.id.camera)
        val imageViewGallery = dialogView.findViewById<ImageView>(R.id.gallery)

        dialog.setOnCancelListener {
            navigateHome()
        }
        imageViewCamera.setOnClickListener {
            dialog.dismiss()
            checkCameraPermission()
        }
        imageViewGallery.setOnClickListener {
            dialog.dismiss()

            checkGalleryPermission()
        }
        dialogView.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()


    }


    private fun navigateHome() {
        val intent = Intent(this, HomeActivity::class.java)
        // Define the transition animation
        val options = ActivityOptionsCompat.makeCustomAnimation(
            this,
            R.anim.slide_in_right, // Enter animation
            R.anim.slide_out_left // Exit animation
        )

        ActivityCompat.startActivity(this, intent, options.toBundle())
    }

    private fun getCameraPicture() {
        try {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (null != takePictureIntent.resolveActivity(this.packageManager)) {
                try {
                    imageFile = imageUtils.createImageFile(this)
                } catch (ex: Exception) {
                    Log.e("##Exception", "${ex.message}")
                }
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    uri = Uri.fromFile(imageFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                } else {
                    uri = FileProvider.getUriForFile(
                        this, this.packageName + ".provider", imageFile
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                }
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                cameraLauncher.launch(takePictureIntent)
            }
        } catch (e: Exception) {
            Log.d("##PathFromCamera", e.toString())
        }


    }

    @SuppressLint("QueryPermissionsNeeded")
    fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (galleryIntent.resolveActivity(packageManager) != null) {
            galleryLauncher.launch(galleryIntent)
        } else {
            // Handle case where there's no app to handle the gallery intent.
        }
    }

    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the selected image from the gallery here (e.g., get the image data from the result data)
                val imageDataUri: Uri = result.data?.data!!

                imageData = imageUtils.uriToBitmap(this, imageDataUri)
                path = imageUtils.getFilePathFromContentUri(imageDataUri, this)
                binding.convertImageView.background = null
                binding.convertImageView.setImageBitmap(imageData)
            }
        }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // Use the returned uri.
            val uriContent = result.uriContent
            val uriFilePath = result.getUriFilePath(this) // optional usage
        } else {
            // An error occurred.
            val exception = result.error
        }
    }


    private fun checkGalleryPermission() {
        // Check gallery permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            openGallery()
        } else {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request gallery permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_GALLERY_PERMISSION
                )
            } else {
                openGallery()
            }
        }
    }


    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request camera permission
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION
            )
        } else {
            getCameraPicture()
        }


    }

     var cameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the captured image here (e.g., get the image data from the result data)
            imageData = BitmapFactory.decodeFile(imageFile.absoluteFile.toString())
            binding.convertImageView.background = null
            binding.convertImageView.setImageBitmap(imageData)
            // Use the captured image as needed
        }
    }


    private fun saveImageToGallery(
        context: Context, bitmap: Bitmap, format: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            runOnUiThread { dialog.show() }
            val relativeLocation =
                Environment.DIRECTORY_PICTURES + File.separator + Constant.FOLDER_NAME
            val fileNameTime: String = "IMG-" + imageUtils.getCurrentDateTime().replace(":", "_")
            Log.e("##NAme", "-$fileNameTime")
            Log.e("##NAmePath", relativeLocation + "/" + fileNameTime + endFormat)
            convertedPath = Constant.PATH + "$relativeLocation/$fileNameTime$endFormat"
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
                        bitmap.compress(Bitmap.CompressFormat.PNG, 10, stream)
                        val savedPath = context.getFileStreamPath(fileNameTime)
                        Log.e("##Sucee", "-$savedPath")
                        runOnUiThread {
                            dialog.dismiss()
                            alertDialog.showAlertDialog(this@ConvertActivity, "sucess")
                            binding.beforeConvertingLayout.visibility = GONE
                            binding.afterConvertingLayout.visibility = VISIBLE
                            val file =
                                getImageFromPictures(this@ConvertActivity, fileNameTime + endFormat)
                            binding.convertedImageView.setImageBitmap(file)
                            binding.convertedPathTextView.text = convertedPath
                        }

                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    dialog.dismiss()
                }

                Log.e("##error", "--$e")
                e.printStackTrace()
            }
        }
    }

    private fun viewConvertedImage() {
        // Create an Intent
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW

        // Using FileProvider to get URI
        val file = File(convertedPath.toString())
        val imageUri = FileProvider.getUriForFile(this, this.packageName + ".provider", file)

        // Set the Intent type
        intent.setDataAndType(imageUri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // Start the Intent
        startActivity(intent)

    }

    private fun getImageFromPictures(context: Context, imageName: String): Bitmap? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(imageName)
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        var bitmap: Bitmap? = null
        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, contentUri)
            }
        }
        return bitmap
    }

    override fun onClick(view: View?) {
        if (view?.id != null) {
            when (view.id) {

                R.id.jpgTextView -> {
                    format = Constant.IMAGE_JPGE
                    binding.jpgTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.rounded_blue)
                    binding.pngTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.sharp_corner)
                    binding.webpTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.sharp_corner)
                    binding.jpgTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.pngTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.webpTextView.setTextColor(ContextCompat.getColor(this, R.color.black))

                }

                R.id.pngTextView -> {
                    format = Constant.IMAGE_PNG
                    endFormat = Constant.PNG_FORMAT
                    binding.jpgTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.sharp_corner)
                    binding.pngTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.rounded_blue)
                    binding.webpTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.sharp_corner)
                    binding.jpgTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.pngTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.webpTextView.setTextColor(ContextCompat.getColor(this, R.color.black))

                }

                R.id.webpTextView -> {
                    format = Constant.IMAGE_WEBP
                    endFormat = Constant.WEBP_FORMAT
                    binding.jpgTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.sharp_corner)
                    binding.pngTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.sharp_corner)
                    binding.webpTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.rounded_blue)
                    binding.jpgTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.pngTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                    binding.webpTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                }

                R.id.convertingTextView -> {
                    saveImageToGallery(this, imageData, format)
                }

                R.id.convertedImageView -> {
                    viewConvertedImage()
                }

                R.id.okTextView -> {
                    navigateHome()
                }

                R.id.backArrow -> {
                    navigateHome()
                }
            }
        }
    }


}