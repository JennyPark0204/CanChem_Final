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
import com.example.canchem.databinding.ItemCompoundBinding

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
        private val compoundCId: TextView = itemView.findViewById(R.id.compound_cid)
        private val compoundMF: TextView = itemView.findViewById(R.id.compound_mf)
        private val compoundInpacName: TextView = itemView.findViewById(R.id.compound_inpac_name)
        private val compoundIsomericSmiles: TextView = itemView.findViewById(R.id.compound_isomericSmiles)
        private val maxLength = 70

        fun bind(compound: ChemicalCompound) {
            compoundName.text = compound.synonyms?.joinToString(separator = "; ") { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "Unknown"
            compoundCId.text = "Compound CID : ${compound.cid?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "N/A"}"
            compoundMF.text = "MF : ${compound.molecularFormula?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "N/A"}"
            compoundInpacName.text = "IUPAC Name : ${compound.inpacName?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "N/A"}"
            compoundIsomericSmiles.text = "Isomeric SMILES : ${compound.isomericSmiles?.let { if (it.length > maxLength) "${it.substring(0, maxLength)}..." else it } ?: "N/A"}"

            if (!compound.image2DUri.isNullOrEmpty()) {
                Picasso.get().load(compound.image2DUri).into(compoundImage)
            } else {
                compoundImage.setImageResource(R.drawable.ic_no_image)
            }
            itemView.setOnClickListener { onItemClick(compound) }
        }
    }
}