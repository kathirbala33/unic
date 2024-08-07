package com.yes.unic.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.app.ActivityCompat
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.yes.unic.R
import com.yes.unic.Utils.Constant
import com.yes.unic.Utils.Util
import com.yes.unic.databinding.ActivityCropImageBinding
import com.yes.unic.listener.AdapterListener

class ImageCropActivity : com.canhub.cropper.CropImageActivity(), AdapterListener {
    private lateinit var binding: ActivityCropImageBinding
    private var utils = Util()
    private lateinit var imageUri: Uri


    companion object {
        fun start(activity: Activity) {
            ActivityCompat.startActivity(
                activity,
                Intent(activity, ImageCropActivity::class.java),
                null,
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCropImageBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        binding.saveImage.setText(getString(R.string.save))
        binding.cropTextView.setText(getString(R.string.crop))
        binding.cropTextView.setOnClickListener { cropImage() }
        binding.backBtn.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.rotationTextView.setOnClickListener { onRotateClick() }
        binding.saveImage.setOnClickListener { saveImageInLocal() }

        binding.cropImageView.setOnCropWindowChangedListener {
            updateExpectedImageSize()
        }

        setCropImageView(binding.cropImageView)
    }

    private fun saveImageInLocal() {
        binding.progressBar.visibility = VISIBLE
        utils.saveImageToGallery(
            this,
            utils.uriToBitmap(this, imageUri),
            "image/jpeg",
            100,
            this
        )
    }

    override fun onSetImageUriComplete(
        view: CropImageView,
        uri: Uri,
        error: Exception?,
    ) {
        super.onSetImageUriComplete(view, uri, error)
        imageUri = uri
        updateRotationCounter()
        updateExpectedImageSize()
    }

    private fun updateExpectedImageSize() {
        binding.resolution.text = binding.cropImageView.expectedImageSize().toString()
    }

    override fun setContentView(view: View) {
        super.setContentView(binding.root)
    }

    private fun updateRotationCounter() {
        binding.rotationTextView.text =
            getString(R.string.rotation_value, binding.cropImageView.rotatedDegrees.toString())
    }

    override fun onPickImageResult(resultUri: Uri?) {
        super.onPickImageResult(resultUri)

        if (resultUri != null) {
            binding.cropImageView.setImageUriAsync(resultUri)
        }
    }

    override fun getResultIntent(uri: Uri?, error: java.lang.Exception?, sampleSize: Int): Intent {
        val result = super.getResultIntent(uri, error, sampleSize)
        // Adding some more information.
        return result.putExtra("EXTRA_KEY", "Extra data")
    }

    override fun setResult(uri: Uri?, error: Exception?, sampleSize: Int) {
        val result = CropImage.ActivityResult(
            originalUri = binding.cropImageView.imageUri,
            uriContent = uri,
            error = error,
            cropPoints = binding.cropImageView.cropPoints,
            cropRect = binding.cropImageView.cropRect,
            rotation = binding.cropImageView.rotatedDegrees,
            wholeImageRect = binding.cropImageView.wholeImageRect,
            sampleSize = sampleSize,
        )


        binding.cropImageView.setImageUriAsync(result.uriContent)
    }

    override fun setResultCancel() {
        super.setResultCancel()
    }

    override fun updateMenuItemIconColor(menu: Menu, itemId: Int, color: Int) {
        super.updateMenuItemIconColor(menu, itemId, color)
    }

    private fun onRotateClick() {
        binding.cropImageView.rotateImage(90)
        updateRotationCounter()
    }

    override fun adapterData(action: Int, value: Any) {
        if (action == Constant.CONVERT_RESPONSE) {
            binding.progressBar.visibility = GONE
        }
    }
}