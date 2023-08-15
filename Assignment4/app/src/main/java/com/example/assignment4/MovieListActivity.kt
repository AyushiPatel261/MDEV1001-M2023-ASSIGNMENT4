package com.example.assignment4

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment4.adapter.MovieAdapter
import com.example.assignment4.dataclass.Movie
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class MovieListActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var userArrayList: ArrayList<Movie>
    private lateinit var myAdapter: MovieAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var btnAdd: Button
    lateinit var btnClose : Button
    private lateinit var userId:String
    companion object {
        private const val ADD_EDIT_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        recyclerView = findViewById(R.id.recyclerView)
        btnAdd = findViewById(R.id.btnAdd)
        btnClose = findViewById(R.id.btnClose)
        btnClose.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        userId= FirebaseAuth.getInstance().currentUser!!.uid
        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        myAdapter = MovieAdapter(userArrayList as ArrayList<Movie>)
        recyclerView.adapter = myAdapter
        myAdapter.movieUpdateCallback = {movie->
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("movie", movie)
            startActivityForResult(intent, ADD_EDIT_REQUEST_CODE)
        }
        EventChangeListener()
        btnAdd.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
        onDeleteMovie()
    }
    private fun onDeleteMovie() {
        myAdapter.onDeleteClickListener = {
            val dialog = AlertDialog.Builder(this)
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete this movie?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteMovie(it)
                }
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun deleteMovie(movie: Movie) {
        db = FirebaseFirestore.getInstance()
        db.collection("movies").document(movie.documentID ?: "")
            .delete()
            .addOnSuccessListener {
                userArrayList.remove(movie)
                myAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Deleted Successfully...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to delete....", Toast.LENGTH_SHORT).show()
            }
    }
    private fun EventChangeListener() {
        db = FirebaseFirestore.getInstance()
        db.collection("movies").addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ){
                if(error != null){
                    Log.d(ContentValues.TAG, "onEvent: ${error.message.toString()}")
                    return
                }
                for(dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val movie = dc.document.toObject(Movie::class.java)
                        movie.documentID = dc.document.id
                        userArrayList.add(movie)
                    }
                }
                myAdapter.notifyDataSetChanged()
            }
        })
    }
}