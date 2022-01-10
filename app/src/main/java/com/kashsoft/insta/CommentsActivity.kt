package com.kashsoft.insta

import  androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.kashsoft.insta.Adapter.CommentAdapter
import com.kashsoft.insta.Model.Comment
import com.kashsoft.insta.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.activity_account_setting.profile_image_view_profile_frag
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser :FirebaseUser? = null
    private var commentAdapter : CommentAdapter?= null
    private var commentList: MutableList<Comment>? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        var intent = intent

        // check if bug occurs on launch
        postId = intent.getStringExtra("postId")!!

        publisherId = intent.getStringExtra("publisherId")!!

        firebaseUser = FirebaseAuth.getInstance().currentUser

        var recyclerView:RecyclerView
        recyclerView = findViewById(R.id.recycler_view_comments)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        commentList = ArrayList()
        commentAdapter = CommentAdapter(this, commentList)
        recyclerView.adapter = commentAdapter

        userInfo()
       readComments()
        getPostImage()
        addNotifications()

        post_comment.setOnClickListener (View.OnClickListener {
            if (add_comment!!.text.toString() == "")
            {
                Toast.makeText(this@CommentsActivity, "Please write comment first",
                Toast.LENGTH_LONG).show()
            }
            else
            {
                addComment()

            }
        } )



    }

    private fun addComment()
    {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments")
            .child(postId!!)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment!!.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)
        add_comment!!.text.clear()

    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(po: DataSnapshot) {
                if (po.exists())
                {
                    val user = po.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage())
                        .placeholder(R.drawable.profile).into(profile_image_comment)




                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getPostImage() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
            .child(postId!!).child("postimage")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(po: DataSnapshot) {
                if (po.exists())
                {
                    val image = po.value.toString()

                    Picasso.get().load(image)
                        .placeholder(R.drawable.profile).into(post_image_comment)




                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

private  fun readComments(){
    val commentsRef = FirebaseDatabase.getInstance().reference
        .child("Comments")
        .child(postId)

    commentsRef.addValueEventListener(object : ValueEventListener

    {
        override fun onDataChange(po: DataSnapshot)
        {
                if (po.exists())
                {
                        commentList !!.clear()
                    for (snapshot in po.children)
                    {
                            val comment =  snapshot.getValue(Comment::class.java)
                            commentList!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()
                }
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }

    })
}

    private fun addNotifications()
    {
     val notiRef = FirebaseDatabase.getInstance()
         .reference.child("Notifications")
         .child(publisherId!!)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] = firebaseUser!!.uid
        notiMap["test"] = "commented: " + add_comment!!.text.toString()
        notiMap["postid"] = postId
        notiMap["ispost"] = true

        notiRef.push().setValue(notiMap)
    }
}