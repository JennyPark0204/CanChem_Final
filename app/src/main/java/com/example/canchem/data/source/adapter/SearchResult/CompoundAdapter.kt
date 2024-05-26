package com.example.canchem.data.source.adapter.SearchResult

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.canchem.R
import com.squareup.picasso.Picasso
import com.example.canchem.data.source.dataclass.Search.ChemicalCompound

class CompoundAdapter(
    private val context: Context,
    private val compounds: List<ChemicalCompound>,
    private val onItemClick: (ChemicalCompound) -> Unit
) : RecyclerView.Adapter<CompoundAdapter.CompoundViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompoundViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_compound, parent, false)
        return CompoundViewHolder(view)
    }

    override fun onBindViewHolder(holder: CompoundViewHolder, position: Int) {
        val compound = compounds[position]
        holder.bind(compound)
    }

    override fun getItemCount(): Int = compounds.size

    inner class CompoundViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val compoundImage: ImageView = itemView.findViewById(R.id.compound_image)
        private val compoundName: TextView = itemView.findViewById(R.id.compound_name)
        private val compoundId: TextView = itemView.findViewById(R.id.compound_id)
        private val compoundCid: TextView = itemView.findViewById(R.id.compound_cid)
        private val compoundIsomericSmiles: TextView = itemView.findViewById(R.id.compound_isomericSmiles)
        private val compoundDescription: TextView = itemView.findViewById(R.id.compound_Description)

        fun bind(compound: ChemicalCompound) {
            compoundName.text = compound.inpacName
            compoundId.text = "ID: ${compound.id}"
            compoundCid.text = "CID: ${compound.cid}"
            compoundIsomericSmiles.text = "Isomeric Smiles: ${compound.isomericSmiles}"

            val maxLength = 70
            val description = compound.description
            val trimmedDescription = if (description.length > maxLength) {
                "${description.substring(0, maxLength)}..." // 최대 글자 수까지만 자르고 "..." 추가
            } else {
                description // 최대 글자 수보다 작으면 그대로 표시
            }
            compoundDescription.text = "Description: ${trimmedDescription}"

            if (compound.image2DUri != null) {
                Picasso.get().load(compound.image2DUri).into(compoundImage)
            } else {
                compoundImage.setImageResource(R.drawable.ic_no_image)
            }
            itemView.setOnClickListener { onItemClick(compound) }
        }
    }
}