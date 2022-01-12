package com.kashsoft.insta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kashsoft.insta.Adapter.StoryAdapter
import com.kashsoft.insta.Model.Story
import com.kashsoft.insta.Model.User
import com.squareup.picasso.Picasso
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.activity_story.*

class StoryActivity : AppCompatActivity(), StoriesProgressView.StoriesListener {


    var currentUserId: String = ""
    var userId : String =""
    var imageList: List<String>? = null

    var storyIdsList  : List<String>? = null

    var storyProgressView: StoriesProgressView? = null
    var counter  = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story)

        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        userId = intent.getStringExtra("userId").toString()

        storyProgressView = findViewById(R.id.stories_progress)
    }



    private fun getStories(userId: String){


        imageList = ArrayList()
        storyIdsList = ArrayList()


        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!)

        ref.addValueEventListener(object  :ValueEventListener{

            override fun onDataChange(po: DataSnapshot) {

                (imageList as ArrayList<String>).clear()
                (storyIdsList as ArrayList<String>).clear()
                    for (snapshot in po.children){
                       val story: Story? = snapshot.getValue<Story>(Story::class.java)
                        val timeCurrent = System.currentTimeMillis()
                        if (timeCurrent>story!!.getTimeStart() && timeCurrent<story.getTimeEnd())
                        {
                            (imageList as ArrayList<String>).add(story.getImageUrl())
                            (storyIdsList as ArrayList<String>).add(story.getStoryId())




                        }
                    }

                storyProgressView!!.setStoriesCount((imageList as ArrayList<String>).size)
                storyProgressView!!.setStoryDuration(5000L)
                storyProgressView!!.setStoriesListener(this@StoryActivity)
                storyProgressView!!.startStories(counter)

                Picasso.get().load(imageList!!.get(counter)).placeholder(R.drawable.profile).into(image_story)
                addViewToStory(storyIdsList!!.get(counter))
               seenNumber(storyIdsList!!.get(counter))

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun userInfo( userId: String){
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(userId)

        usersRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(po: DataSnapshot) {

// note 25.40

                if (po.exists()){
                    val user = po.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(story_profile_image)

                    story_username.text = user.getUsername()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addViewToStory(storyId: String){

        FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!).child(storyId)
            .child("views")
            .child(currentUserId)
            .setValue(true)
    }
    private fun seenNumber(storyId: String)
    {

        val ref = FirebaseDatabase.getInstance().reference
            .child("Story")
            .child(userId!!).child(storyId)
            .child("views")

        ref.addValueEventListener(object : ValueEventListener{


            override fun onDataChange(po: DataSnapshot)
            {


            seen_number.text = ""+ po.childrenCount
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onNext() {

    }

    override fun onPrev() {
        TODO("Not yet implemented")
    }

    override fun onComplete() {
        TODO("Not yet implemented")
    }
}