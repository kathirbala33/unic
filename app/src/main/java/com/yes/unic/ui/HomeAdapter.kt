package com.yes.unic.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yes.unic.Utils.Constant.Companion.RECYCLER_VIEW_SELECTION
import com.yes.unic.databinding.HomeAdapterBinding
import com.yes.unic.listener.AdapterListener
import com.yes.unic.model.HomeModel

class HomeAdapter(
    mContext: Context,
    arrayList: ArrayList<HomeModel>,
    adapterListener: AdapterListener,
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private var arrayList = java.util.ArrayList<HomeModel>()
    private var commonAdapterBinding: HomeAdapterBinding? = null
    private var adapterListener: AdapterListener? = null
    private var context: Context? = null

    init {
        this.arrayList = arrayList
        this.adapterListener = adapterListener
        this.context = mContext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        commonAdapterBinding = HomeAdapterBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val view = commonAdapterBinding!!.root
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView?.text = arrayList[position].name
        holder.imageView?.background = arrayList[position].image
        holder.totalRelativeLayout?.setOnClickListener {
            adapterListener?.adapterData(RECYCLER_VIEW_SELECTION, arrayList[position].name)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val textView = commonAdapterBinding?.valuesTextView
        val imageView = commonAdapterBinding?.itemImageView
        val totalRelativeLayout = commonAdapterBinding?.totalRelativeLayout
        override fun onClick(view: View?) {

        }

    }
}