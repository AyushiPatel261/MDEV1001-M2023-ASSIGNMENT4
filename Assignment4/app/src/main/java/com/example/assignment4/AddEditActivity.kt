package com.example.assignment4

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.assignment4.adapter.MovieAdapter
import com.example.assignment4.dataclass.Movie
import com.google.firebase.firestore.FirebaseFirestore

class AddEditActivity : AppCompatActivity() {

    private lateinit var movieIDEditText: EditText
    private lateinit var titleEditText: EditText
    private lateinit var studioEditText: EditText
    private lateinit var genresEditText: EditText
    private lateinit var directorsEditText: EditText
    private lateinit var writersEditText: EditText
    private lateinit var actorsEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var lengthEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var mpaRatingEditText: EditText
    private lateinit var criticsRatingEditText: EditText
    private lateinit var txtViewAddEditMovie:TextView
    private lateinit var updateButton: Button
    private var userArrayList: MutableList<Movie> = mutableListOf()
    private lateinit var cancelButton: Button
    private var movie: Movie? = null
    private lateinit var myAdapter: MovieAdapter
    private lateinit var db: FirebaseFirestore

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        db = FirebaseFirestore.getInstance()
        txtViewAddEditMovie = findViewById(R.id.txtViewAddEditMovie)
        movieIDEditText = findViewById(R.id.editTextMovieId)
        titleEditText = findViewById(R.id.editTextTitle)
        studioEditText = findViewById(R.id.editTextStudio)
        genresEditText = findViewById(R.id.editTextGenres)
        directorsEditText = findViewById(R.id.editTextDirectors)
        writersEditText = findViewById(R.id.editTextWriters)
        actorsEditText = findViewById(R.id.editTextActors)
        yearEditText = findViewById(R.id.editTextYear)
        lengthEditText = findViewById(R.id.editTextLength)
        descriptionEditText = findViewById(R.id.editTextShortDescription)
        mpaRatingEditText = findViewById(R.id.editTextMpaRating)
        criticsRatingEditText = findViewById(R.id.editTextCriticsRating)
        updateButton = findViewById(R.id.buttonUpdate)
        cancelButton = findViewById(R.id.btnCancel)
        cancelButton.setOnClickListener {
            val intent = Intent(this, MovieListActivity::class.java)
            startActivity(intent)
        }
        movie = intent.getParcelableExtra("movie")
        if (movie != null) {
            // Editing existing movie
            //  movieIDEditText.setText(movie!!.movieID)
            movieIDEditText.setText(movie!!.movieID.toString())
            titleEditText.setText(movie!!.title)
            studioEditText.setText(movie!!.studio)
            genresEditText.setText(movie!!.genres.joinToString(", "))
            directorsEditText.setText(movie!!.directors.joinToString(", "))
            writersEditText.setText(movie!!.writers.joinToString(", "))
            actorsEditText.setText(movie!!.actors.joinToString(", "))
            lengthEditText.setText(movie!!.length.toString())
            yearEditText.setText(movie!!.year.toString())
            descriptionEditText.setText(movie!!.shortDescription)
            mpaRatingEditText.setText(movie!!.mpaRating)
            criticsRatingEditText.setText(movie!!.criticsRating.toString())
            updateButton.text = "Update"
            txtViewAddEditMovie.text = "Update Movie "
        } else {
            updateButton.text = "Add"
            txtViewAddEditMovie.text = "Add Movie"
        }
        updateButton.setOnClickListener {
            val newMovie = createMovieFromInput()
            if (movie == null) {
                addMovieToFirestore(newMovie)
            } else {
                updateMovieInFirestore(newMovie)
            }
        }
    }

    private fun createMovieFromInput(): Movie {
        val movieID = movieIDEditText.text.toString().trim().toLong()
        val title = titleEditText.text.toString().trim()
        val studio = studioEditText.text.toString().trim()
        val genres = genresEditText.text.toString().trim().split(",").map { it.trim() }
        val directors = directorsEditText.text.toString().trim().split(",").map { it.trim() }
        val writers = writersEditText.text.toString().trim().split(",").map { it.trim() }
        val actors = actorsEditText.text.toString().trim().split(",").map { it.trim() }
        val year = yearEditText.text.toString().trim().toInt()
        val length = lengthEditText.text.toString().trim().toInt()
        val description = descriptionEditText.text.toString().trim()
        val mpaRating = mpaRatingEditText.text.toString().trim()
        val criticsRating = criticsRatingEditText.text.toString().trim().toDouble()

        return Movie(
            "",movieID.toLong(),"",title,studio,genres,directors,writers,actors,year,length, description,mpaRating,criticsRating

        /*
            _id = "", // Leave this blank as it will be generated by Firestore
            movieID = movieID.toLong(),

            title = title,
            studio = studio,
            genres = genres,
            directors = directors,
            writers = writers,
            actors = actors,
            year = year,
            length = length,
            shortDescription = description,
            mpaRating = mpaRating,
            criticsRating = criticsRating*/
        )
    }
    private fun addMovieToFirestore(movie: Movie) {
        db.collection("movies").add(movie)
            .addOnSuccessListener {
                // Movie added successfully
                Log.d(ContentValues.TAG, "Movie added...")
                // Update the data source and notify the adapter
                userArrayList.add(movie)
                myAdapter.notifyItemInserted(userArrayList.size - 1)
                finish()
            }
            .addOnFailureListener {
                //Failed to add movie
                Toast.makeText(this, "Failed to Add Movie...", Toast.LENGTH_SHORT).show()
            }
    }
    private fun updateMovieInFirestore(movie: Movie) {
        db.collection("movies").document(movie.movieID!!.toString())
            .set(movie)
            .addOnSuccessListener {
                //Movie updated successfully
                //Log.d(ContentValues.TAG, "Movie updated with ID: ${movie.documentID}")
                //Update the movie in the RecyclerView
                val updatedIndex =
                    userArrayList.indexOfFirst { it.movieID == movie.movieID }
                if (updatedIndex != -1) {
                    userArrayList[updatedIndex] = movie
                    myAdapter.notifyItemChanged(updatedIndex)
                }
                finish()
            }
            .addOnFailureListener { e ->
                //Failed to update movie
                Log.w(ContentValues.TAG, "Error updating movie", e)
                Toast.makeText(this, "Failed to update movie", Toast.LENGTH_SHORT).show()
            }
    }
}