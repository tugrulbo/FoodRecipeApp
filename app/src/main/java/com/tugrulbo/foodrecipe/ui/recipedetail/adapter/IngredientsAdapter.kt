package com.tugrulbo.foodrecipe.ui.recipedetail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.tugrulbo.foodrecipe.R
import com.tugrulbo.foodrecipe.data.model.ExtendedIngredient
import com.tugrulbo.foodrecipe.utils.Constants.BASE_IMAGE_URL
import com.tugrulbo.foodrecipe.utils.RecipesDiffUtil
import kotlinx.android.synthetic.main.ingredients_row_layout.view.*

class IngredientsAdapter: RecyclerView.Adapter<IngredientsAdapter.ViewHolder>() {

    private var ingredientList = emptyList<ExtendedIngredient>()

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ingredients_row_layout,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = ingredientList[position]
        holder.itemView.ingredient_imageView.load(BASE_IMAGE_URL + current.image){
            crossfade(600)
        }
        holder.itemView.ingredient_name.text = current.name
        holder.itemView.ingredient_amount.text = current.amount.toString()
        holder.itemView.ingredient_unit.text = current.unit
        holder.itemView.ingredient_consistency.text = current.consistency
        holder.itemView.ingredient_original.text = current.original

    }

    override fun getItemCount(): Int {
        return ingredientList.size
    }

    fun setData(newIngredientList:List<ExtendedIngredient>){
        val recipeDiffUtil = RecipesDiffUtil(ingredientList,newIngredientList)
        val diffUtilResult = DiffUtil.calculateDiff(recipeDiffUtil)
        ingredientList = newIngredientList
        diffUtilResult.dispatchUpdatesTo(this)
    }
}