package com.tugrulbo.foodrecipe.ui.recipes.adapter

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
import com.tugrulbo.foodrecipe.data.model.FoodRecipe
import com.tugrulbo.foodrecipe.data.model.Result
import com.tugrulbo.foodrecipe.utils.RecipesDiffUtil
import org.jsoup.Jsoup

class RecipeAdapter(
    private var onItemClicked: ((result: Result) -> Unit)
) :RecyclerView.Adapter<RecipeAdapter.ViewHolder>() {

    private var recipes = emptyList<Result>()
    private lateinit var context: Context

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        fun bind(result: Result){
            val ivRecipeImage:ImageView = itemView.findViewById(R.id.ivRecipeImage)
            val ivVegan:ImageView = itemView.findViewById(R.id.ivLeaf)
            val tvRecipeTitle:TextView = itemView.findViewById(R.id.tvRecipeTitle)
            val tvRecipeDes:TextView = itemView.findViewById(R.id.tvRecipeDesc)
            val tvNumberOfLikes:TextView = itemView.findViewById(R.id.tvHeartText)
            val tvNumberOfTimes:TextView = itemView.findViewById(R.id.tvClockText)
            val tvVegan:TextView = itemView.findViewById(R.id.tvLeafText)

            ivRecipeImage.load(result.image){
                crossfade(600)
            }
            tvRecipeTitle.text = result.title
            result.summary.let { value->
                val summary = Jsoup.parse(value).text()
                tvRecipeDes.text = summary
            }

            tvNumberOfLikes.text = result.aggregateLikes.toString()
            tvNumberOfTimes.text = result.readyInMinutes.toString()
            if(result.vegan){
                tvVegan.setTextColor(ContextCompat.getColor(context,R.color.green))
                ivVegan.setColorFilter(ContextCompat.getColor(context,R.color.green))
            }

            itemView.setOnClickListener {
                onItemClicked(result)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
       return ViewHolder(
           LayoutInflater.from(parent.context).inflate(
               R.layout.recipe_row_layout,
               parent,
               false
       ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun setData(newData:FoodRecipe){
        val recipesDiffUtil = RecipesDiffUtil(recipes,newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }
}