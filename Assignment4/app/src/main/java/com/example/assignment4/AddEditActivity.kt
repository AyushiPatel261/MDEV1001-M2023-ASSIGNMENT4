package com.example.assignment4

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
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
    private lateinit var txtViewAddEditMovie: TextView
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
        val context: Context = applicationContext
        myAdapter = MovieAdapter(context, userArrayList)

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
            updateMovie()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateMovie() {
        val db = FirebaseFirestore.getInstance()

        val movieID = movieIDEditText.text.toString().toLong()
        val title = titleEditText.text.toString()
        val studio = studioEditText.text.toString()
        val genres = genresEditText.text.toString().split(",").map { it.trim() }
        val directors = directorsEditText.text.toString().split(",").map { it.trim() }
        val writers = writersEditText.text.toString().split(",").map { it.trim() }
        val actors = actorsEditText.text.toString().split(",").map { it.trim() }
        val year = yearEditText.text.toString().toInt()
        val length = lengthEditText.text.toString().toInt()
        val description = descriptionEditText.text.toString()
        val mpaRating = mpaRatingEditText.text.toString()
        val criticsRating = criticsRatingEditText.text.toString().toDouble()

        if (movie != null) {
            val movieDocumentRef = db.collection("movies").document(movie!!.documentID!!)
            movieDocumentRef.update(
                "movieID", movieID,
                "title", title,
                "studio", studio,
                "genres", genres,
                "directors", directors,
                "writers", writers,
                "actors", actors,
                "year", year,
                "length", length,
                "shortDescription", description,
                "mpaRating", mpaRating,
                "criticsRating", criticsRating
            )
                .addOnSuccessListener {
                    myAdapter.movieUpdateCallback!!.invoke(movie!!)
                    Toast.makeText(this, "Movie updated successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error updating movie: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        } else {
            val newMovie = hashMapOf(
                "movieID" to movieID,
                "title" to title,
                "studio" to studio,
                "genres" to genres,
                "directors" to directors,
                "writers" to writers,
                "actors" to actors,
                "year" to year,
                "length" to length,
                "shortDescription" to description,
                "mpaRating" to mpaRating,
                "criticsRating" to criticsRating
            )
            db.collection("movies")
                .add(newMovie)
                .addOnSuccessListener { documentReference ->
                    myAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Movie added successfully.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding movie: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
}
