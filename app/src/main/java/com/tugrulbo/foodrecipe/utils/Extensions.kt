package com.tugrulbo.foodrecipe.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar


fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>){
    observe(lifecycleOwner, object : Observer<T>{
        override fun onChanged(t: T?) {
            removeObserver(this)
            observer.onChanged(t)
        }

    })
}

fun View.showVisibility(isVisible:Boolean):View{
    visibility = if (isVisible){
        View.VISIBLE
    }else{
        View.GONE
    }
    return this
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
