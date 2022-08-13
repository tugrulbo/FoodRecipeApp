package com.tugrulbo.foodrecipe.ui.favoriterecipe.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tugrulbo.foodrecipe.R
import com.tugrulbo.foodrecipe.data.local.entities.FavoritesEntity
import com.tugrulbo.foodrecipe.data.model.Result
import com.tugrulbo.foodrecipe.utils.RecipesDiffUtil

class FavoriteRecipesAdapter(
    private var onItemClicked: ((result: Result) -> Unit)
): RecyclerView.Adapter<FavoriteRecipesAdapter.ViewHolder>() {

    var favoriteRecipesList = emptyList<FavoritesEntity>()
    private lateinit var context: Context

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(data: FavoritesEntity){
            val ivRecipeImage: ImageView = itemView.findViewById(R.id.ivRecipeImage)
            val ivVegan: ImageView = itemView.findViewById(R.id.ivLeaf)
            val tvRecipeTitle: TextView = itemView.findViewById(R.id.tvRecipeTitle)
            val tvRecipeDes: TextView = itemView.findViewById(R.id.tvRecipeDesc)
            val tvNumberOfLikes: TextView = itemView.findViewById(R.id.tvHeartText)
            val tvNumberOfTimes: TextView = itemView.findViewById(R.id.tvClockText)
            val tvVegan: TextView = itemView.findViewById(R.id.tvLeafText)

            ivRecipeImage.load(data.result.image){
                crossfade(600)
            }
            tvRecipeTitle.text = data.result.title
            tvRecipeDes.text = data.result.summary
            tvNumberOfLikes.text = data.result.aggregateLikes.toString()
            tvNumberOfTimes.text = data.result.readyInMinutes.toString()
            if(data.result.vegan){
                tvVegan.setTextColor(ContextCompat.getColor(context,R.color.green))
                ivVegan.setColorFilter(ContextCompat.getColor(context,R.color.green))
            }

            itemView.setOnClickListener {
                onItemClicked(data.result)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.recipe_row_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentFavorite = favoriteRecipesList[position]
        holder.bind(currentFavorite)
    }

    override fun getItemCount(): Int {
        return favoriteRecipesList.size
    }

    fun setData(dataList:List<FavoritesEntity>){
        val diffUtil = RecipesDiffUtil(favoriteRecipesList,dataList)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil)
        favoriteRecipesList = dataList
        diffUtilResult.dispatchUpdatesTo(this)

    }
}