package com.example.assignment4.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment4.R
import com.example.assignment4.dataclass.Movie

class MovieAdapter(private val movies: ArrayList<Movie>) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {
    var movieUpdateCallback: ((Movie) -> Unit)? = null
    var onDeleteClickListener: ((Movie) -> Unit)? = null

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val studioTextView: TextView = itemView.findViewById(R.id.studioTextView)
        val ratingTextView: TextView = itemView.findViewById(R.id.ratingTextView)
        val constraintLayout: ConstraintLayout = itemView.findViewById(R.id.constraintLayout)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDelete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_movie, parent, false)
        return ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleTextView.text = movie.title
        holder.studioTextView.text = movie.studio
        holder.ratingTextView.text = movie.criticsRating.toString()
        holder.buttonDelete.setOnClickListener {
            onDeleteClickListener?.invoke(movie)
        }
        //Set the background color of ratingTextView based on the rating
        val rating = movie.criticsRating
        val context = holder.itemView.context
        val color = when {
            rating > 7 -> ContextCompat.getColor(context, R.color.red)
            rating > 5 -> ContextCompat.getColor(context, R.color.yellow)
            else -> ContextCompat.getColor(context, R.color.green)
        }
        holder.ratingTextView.setBackgroundColor(color)
        holder.constraintLayout.setOnClickListener {
            movieUpdateCallback?.invoke(movie)
        }
    }
    override fun getItemCount(): Int {
        return movies.size
    }
}