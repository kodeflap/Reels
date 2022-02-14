package com.example.reels

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.viewpager2.widget.ViewPager2
import com.example.reels.adapter.VideoAdapter
import com.example.reels.model.VideoModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var viewPager2: ViewPager2
    lateinit var adapter: VideoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*** set full screen ****/
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)

        /*** set find id ****/
        viewPager2 = findViewById(R.id.view_pager)

        /*** set database ****/
        val dataBase = Firebase.database.getReference("videos")
        val options = FirebaseRecyclerOptions.Builder<VideoModel>()
            .setQuery(dataBase,VideoModel::class.java)
            .build()

        /*** set daapter ****/
        adapter = VideoAdapter(options)
        viewPager2.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}