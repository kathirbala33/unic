package com.yes.unic.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.yes.unic.HomeActivity
import com.yes.unic.R
import com.yes.unic.Utils.Constant
import com.yes.unic.Utils.DialogUtils
import com.yes.unic.Utils.Util
import com.yes.unic.databinding.ActivityCompressBinding
import com.yes.unic.listener.AdapterListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CompressActivity : AppCompatActivity(), AdapterListener, OnClickListener {
    private lateinit var binding: ActivityCompressBinding
    private lateinit var imageFile: File
    private  var bitmapImage: Bitmap? = null
    private lateinit var path: String
    private var util: Util = Util()
    private var dialogUtils = DialogUtils()
    private lateinit var dialog: Dialog
    private var quality = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCompressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.beforeConvertingLayout.visibility = VISIBLE
        binding.afterConvertingLayout.visibility = GONE
        dialog = dialogUtils.alertDialogLoadingBar(this)
        binding.convertingTextView.setOnClickListener(this)
        binding.sizeTextView.setOnClickListener(this)
        binding.qualityTextView.setOnClickListener(this)
        binding.okTextView.setOnClickListener(this)
        binding.backArrow.setOnClickListener(this)
        val intent = Intent(this, HomeActivity::class.java)
        util.displayPopup(layoutInflater, this, intent, this)
        binding.compressionSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                quality = progress // Use the progress value as the compression quality
                binding.seekbarTextView.setText(progress.toString())
//                val compressedBitmap = compressBitmap(bitmapImage, quality)


//                binding.compressedImageView.setImageBitmap(compressedBitmap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        binding.compressionSizeSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                quality = progress // Use the progress value as the compression quality
                binding.seekbarSizeTextView.setText(progress.toString())
//                val compressedBitmap = compressBitmap(bitmapImage, quality)


//                binding.compressedImageView.setImageBitmap(compressedBitmap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }


    override fun adapterData(action: Int, value: Any) {
        when (action) {
            Constant.GET_CAMERA_VALUE -> {
                val intent: Intent = value as Intent
                cameraLauncher.launch(intent)
            }

            Constant.GET_CAMERA_IMAGE_FILE -> {
                imageFile = value as File
                path = imageFile.toString()
            }

            Constant.CONVERT_RESPONSE -> {
                dialog.dismiss()
                val fileName = value as String
                runOnUiThread {
                    binding.beforeConvertingLayout.visibility = GONE
                    binding.afterConvertingLayout.visibility = VISIBLE
                    binding.convertedImageView.setImageBitmap(bitmapImage)
                    binding.convertedPathTextView.text = fileName
                }


            }

            Constant.GET_GALLERY_VALUE -> {
                val intent: Intent = value as Intent
                galleryLauncher.launch(intent)
            }
        }
    }

    private var cameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle the captured image here (e.g., get the image data from the result data)
            bitmapImage = BitmapFactory.decodeFile(imageFile.absoluteFile.toString())
            binding.compressedImageView.background = null
            binding.compressedImageView.setImageBitmap(bitmapImage)
            // Use the captured image as needed
        }
    }

    private val galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the selected image from the gallery here (e.g., get the image data from the result data)
                val imageDataUri: Uri = result.data?.data!!

                bitmapImage = util.uriToBitmap(this, imageDataUri)
                path = util.getFilePathFromContentUri(imageDataUri, this).toString()

                binding.compressedImageView.background = null
                binding.compressedImageView.setImageBitmap(bitmapImage)
            }
        }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.convertingTextView -> {
                    if(bitmapImage!=null) {
                        dialog.show()
                        util.saveImageToGallery(this, bitmapImage!!, "image/jpeg", quality, this)
                    }
                }
                R.id.okTextView -> {
                    navigateHome()
                }

                R.id.backArrow -> {
                    navigateHome()
                }
                R.id.sizeTextView -> {
                    binding.sizeLayout.visibility = VISIBLE
                    binding.qualityLayout.visibility = GONE
                    binding.sizeTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.rounded_blue)
                    binding.qualityTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.sharp_corner)
                    binding.sizeTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.qualityTextView.setTextColor(ContextCompat.getColor(this, R.color.black))

                }
                R.id.qualityTextView -> {
                    binding.sizeLayout.visibility = GONE
                    binding.qualityLayout.visibility = VISIBLE
                    binding.qualityTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.rounded_blue)
                    binding.sizeTextView.background =
                        ContextCompat.getDrawable(this, R.drawable.sharp_corner)
                    binding.qualityTextView.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.sizeTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
                }
            }

        }
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

    fun compressImageToFile(inputFile: File, outputFile: File, targetSizeInKB: Int) {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        val originalBitmap = BitmapFactory.decodeFile(inputFile.absolutePath, options)

        val outputStream = ByteArrayOutputStream()
        var quality = 100 // Initial quality value

        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        while (outputStream.size() / 1024 > targetSizeInKB) {
            outputStream.reset() // Clear the previous compressed data
            quality -= 10 // Reduce quality by 10 units
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        }

        val fos = FileOutputStream(outputFile)
        fos.write(outputStream.toByteArray())
        fos.close()
    }


    fun compressAndSaveImage1(
        inputFilePath: String,
        desiredSizeInKB: Int,
        outputFolderPath: String
    ) {
        // Load the original image from the specified file path
        val originalBitmap = BitmapFactory.decodeFile(inputFilePath)

        // Calculate the compression quality to achieve the desired image size
        var quality = 100
        var imageInBytes: ByteArray
        do {
            // Compress the image with the current quality level
            val outputStream = ByteArrayOutputStream()
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            imageInBytes = outputStream.toByteArray()
            quality -= 5 // Decrease quality by 5 each time (you can adjust this value as needed)

            // Check the resulting image size in KB
        } while (imageInBytes.size > desiredSizeInKB * 1024 && quality > 0)

        // Create the output folder if it doesn't exist
        val outputFolder = File(outputFolderPath)
        if (!outputFolder.exists()) {
            outputFolder.mkdirs()
        }

        // Generate a new file path for the compressed image
        val outputFile = File(outputFolder, "compressed_image.jpg")

        // Save the compressed image to the new file
        val outputStream = FileOutputStream(outputFile)
        outputStream.write(imageInBytes)
        outputStream.close()
    }

    fun compressAndSaveImage(
        inputFilePath: String,
        desiredSizeInKB: Int,
        outputFolderPath: String
    ) {
        var relativeLocation =
            Environment.DIRECTORY_PICTURES + File.separator + Constant.FOLDER_NAME
//        relativeLocation = "$relativeLocation/IMG-" + util.getCurrentDateTime().replace(":", "_.jpg")
        // Load the original image from the specified file path
        val originalBitmap = BitmapFactory.decodeFile(inputFilePath)

        // Calculate the compression quality to achieve the desired image size
        var quality = 100
        var imageInBytes: ByteArray
        do {
            // Compress the image with the current quality level
            val outputStream = ByteArrayOutputStream()
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            imageInBytes = outputStream.toByteArray()
            quality -= 5 // Decrease quality by 5 each time (you can adjust this value as needed)

            // Check the resulting image size in KB
        } while (imageInBytes.size > desiredSizeInKB * 1024 && quality > 0)

        // Create the output folder if it doesn't exist
        val outputFolder = File(relativeLocation)
        if (!outputFolder.exists()) {
            outputFolder.mkdirs()
        }

        // Generate a new file path for the compressed image
        val outputFile = File(outputFolder, "IMG.jpg")

        // Save the compressed image to the new file
        Log.e("##Loc", "-" + outputFile)
        val outputStream = FileOutputStream(outputFile)
        outputStream.write(imageInBytes)
        outputStream.close()
    }

}