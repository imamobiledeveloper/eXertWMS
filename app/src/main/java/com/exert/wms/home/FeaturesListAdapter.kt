package com.exert.wms.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.exert.wms.databinding.HomeFeaturesListItemLayoutBinding

class FeaturesListAdapter(
    private val featuresList: List<String>,
    private val onFeatureTextClick: (String) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mSelectedItem = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolderBinding = HomeFeaturesListItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FeaturesViewHolder(viewHolderBinding, onFeatureTextClick)
    }

    override fun getItemCount(): Int {
        return featuresList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FeaturesViewHolder).bind(featuresList[position], position, mSelectedItem)
    }

    inner class FeaturesViewHolder(
        holderBinding: HomeFeaturesListItemLayoutBinding,
        private val onFeatureTextClick: (String) -> Unit?
    ) : RecyclerView.ViewHolder(holderBinding.root) {
        private val featureTV: TextView = holderBinding.featureNameTV

        fun bind(text: String, position: Int, selectedPosition: Int) {
            featureTV.text = text
            featureTV.setOnClickListener {
//                mSelectedItem = bindingAdapterPosition
//                bindingAdapter?.notifyDataSetChanged()
                onFeatureTextClick(featureTV.text.toString())
            }
        }
    }
}