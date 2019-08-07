package com.coinlogiq.updateatmsproyect.ui.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.model.rates.Rate
import com.coinlogiq.updateatmsproyect.ui.extensions.inflate
import com.coinlogiq.updateatmsproyect.utils.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_rates_item.view.*
import java.text.SimpleDateFormat


class RatesAdapter (private val items: List<Rate>) : RecyclerView.Adapter<RatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(parent.inflate(R.layout.fragment_rates_item))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(rate : Rate) = with(itemView){
            textViewRate.text = rate.text
            textViewStar.text = rate.rate.toString()
            textViewFecha.text = SimpleDateFormat("dd MMM, yyyy").format(rate.createdAt)
            if(rate.profileImgUrl.isEmpty()){
                Picasso.get().load(R.drawable.ic_person).resize(100,100)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfile)
            }else {
                Picasso.get().load(rate.profileImgUrl).resize(100,100)
                    .centerCrop().transform(CircleTransform()).into(imageViewProfile)
            }
        }
    }

}