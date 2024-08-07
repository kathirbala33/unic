package com.yes.unic

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.yes.unic.Utils.Constant
import com.yes.unic.Utils.Util
import com.yes.unic.activity.CompressActivity
import com.yes.unic.activity.ConvertActivity
import com.yes.unic.activity.ImageCropActivity
import com.yes.unic.databinding.ActivityMainBinding
import com.yes.unic.listener.AdapterListener
import com.yes.unic.model.HomeModel
import com.yes.unic.ui.HomeAdapter
import java.io.File


class HomeActivity : AppCompatActivity(), OnClickListener, AdapterListener {
    private lateinit var binding: ActivityMainBinding
    private var imageUtils: Util = Util()
    private var path: String? = ""
    private lateinit var format: String
    private lateinit var imageFile: File
    private lateinit var imageData: Bitmap
    private lateinit var uri: Uri
    private lateinit var homeAdapter: HomeAdapter

    companion

    object {
        private const val REQUEST_CAMERA_PERMISSION = 101
        private const val REQUEST_GALLERY_PERMISSION = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun setHomeValues() {
        val homeList: ArrayList<HomeModel> = ArrayList()
        homeList.add(
            HomeModel(
                getString(R.string.conver),
                ContextCompat.getDrawable(this, R.drawable.convert)!!
            )
        )
        homeList.add(
            HomeModel(
                getString(R.string.compress),
                ContextCompat.getDrawable(this, R.drawable.ic_compress)!!
            )
        )
        homeList.add(
            HomeModel(
                getString(R.string.crop),
                ContextCompat.getDrawable(this, R.drawable.ic_crop)!!
            )
        )

        homeAdapter = HomeAdapter(this, homeList, this)
        binding.mainRecyclerView.adapter = homeAdapter

    }

    private fun init() {
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.layoutManager = GridLayoutManager(this, 2)
        setHomeValues()
        MobileAds.initialize(this) {}
        val adView = AdView(this)
        val adRequest = AdRequest.Builder().build()
        adView.setAdSize(AdSize.BANNER)
        adView.adUnitId = "ca-app-pub-3220476658801646/6992656515"
        binding.adView.loadAd(adRequest)
    }


    @SuppressLint("InflateParams")
    override fun onClick(view: View?) {


    }


    override fun adapterData(action: Int, value: Any) {
        if (action == Constant.RECYCLER_VIEW_SELECTION) {
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this,
                R.anim.slide_in_right, // Enter animation
                R.anim.slide_out_left // Exit animation
            )
            if (value == getString(R.string.conver)) {
                val intent = Intent(this, ConvertActivity::class.java)
                ActivityCompat.startActivity(this, intent, options.toBundle())
            } else if (value == getString(R.string.compress)) {
                val intent = Intent(this, CompressActivity::class.java)
                ActivityCompat.startActivity(this, intent, options.toBundle())
            } else if (value == getString(R.string.crop)) {
                val intent = Intent(this, ImageCropActivity::class.java)
                ActivityCompat.startActivity(this, intent, options.toBundle())
            }
        }

    }
}