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
        private val maxLength = 70

        fun bind(compound: ChemicalCompound) {
            compoundName.text = compound.inpacName?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "Unknown"
            compoundId.text = "ID: ${compound.id?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "N/A"}"
            compoundCid.text = "CID: ${compound.cid?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "N/A"}"
            compoundIsomericSmiles.text = "Isomeric Smiles: ${compound.isomericSmiles?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "N/A"}"
            compoundDescription.text = "Description: ${compound.description?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "No description available"}"

            if (!compound.image2DUri.isNullOrEmpty()) {
                Picasso.get().load(compound.image2DUri).into(compoundImage)
            } else {
                compoundImage.setImageResource(R.drawable.ic_no_image)
            }
            itemView.setOnClickListener { onItemClick(compound) }
        }
    }
}