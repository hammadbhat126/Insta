package com.kashsoft.insta.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kashsoft.insta.Adapter.PostAdapter
import com.kashsoft.insta.Model.Post
import com.kashsoft.insta.R


class PostDetailsFragment() : Fragment() {

    private var postAdapter : PostAdapter? = null
    private var postList : MutableList<Post>? = null
    private var postId:String= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
    val view =  inflater.inflate(R.layout.fragment_post_details, container, false)


        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preferences!=null)
        {

            postId= preferences.getString("postId", "None").toString()
        }

        var recyclerView : RecyclerView = view.findViewById(R.id.recyler_view_post_details)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = linearLayoutManager
        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter


        retrievePost()

        return view
    }

    private fun  retrievePost() {
        val postsRef = FirebaseDatabase.getInstance().reference
            .child("Posts")
            .child(postId)
        postsRef.addValueEventListener(object : ValueEventListener
        {


            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(po: DataSnapshot)
            {
                postList?.clear()
                val post = po.getValue(Post::class.java)
                postList!!.add(post!!)

                postAdapter!!.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}