package com.kashsoft.insta.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kashsoft.insta.Adapter.PostAdapter
import com.kashsoft.insta.Adapter.StoryAdapter
import com.kashsoft.insta.Model.Post
import com.kashsoft.insta.Model.Story
import com.kashsoft.insta.R

class HomeFragment : Fragment() {

    private var postAdapter : PostAdapter? = null
    private var postList : MutableList<Post>? = null
    private var followingList: MutableList<String>? = null

    private var storyAdapter : StoryAdapter? = null
    private var storyList : MutableList<Story>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      val view =  inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView :RecyclerView?= null
        recyclerView = view.findViewById(R.id.recyler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        var recyclerViewStory : RecyclerView? = null
        recyclerViewStory = view.findViewById(R.id.recyler_view_story)
        val linearLayoutManager2 = LinearLayoutManager(context)
        linearLayoutManager2.reverseLayout = true
        linearLayoutManager2.stackFromEnd = true
        recyclerViewStory.layoutManager = linearLayoutManager2





        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

       storyList = ArrayList()
       storyAdapter = context?.let { StoryAdapter(it, storyList as ArrayList<Story>) }
        recyclerViewStory.adapter = storyAdapter

        checkFollowings()

        return view


    }

   private fun checkFollowings(){
       followingList = ArrayList()
       val followingRef = FirebaseDatabase.getInstance().reference
               .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
               .child("Following")
       followingRef.addValueEventListener(object : ValueEventListener
       {

           override fun onDataChange(po: DataSnapshot) {
             if (po.exists())
             {
                 (followingList as ArrayList<String>).clear()
                 for (snapshot in po.children)
                 {
                     snapshot.key?.let { (followingList as ArrayList<String>).add(it)}
                 }

                 retrievePosts()
                 retrieveStories()
             }
           }

           override fun onCancelled(error: DatabaseError) {
               TODO("Not yet implemented")
           }
       })


       }

    private fun  retrievePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object :ValueEventListener{


            override fun onDataChange(po: DataSnapshot) {
              postList?.clear()

                for (snapshot in po.children)
                {
                val post = snapshot.getValue(Post::class.java)
          // notes
                    for (id in followingList as ArrayList<String> )
                    {
                        if (post!!.getPublisher() == id)
                        {
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
             }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
  private fun retrieveStories(){
      val storyRef = FirebaseDatabase.getInstance().reference.child("Stories")
      storyRef.addValueEventListener(object: ValueEventListener{


          override fun onDataChange(datasnapshot: DataSnapshot) {
            val timeCurrent = System.currentTimeMillis()
              (storyList as ArrayList<Story>).clear()
              (storyList as ArrayList<Story>).add(Story("",0,0, "", FirebaseAuth
                  .getInstance().currentUser!!.uid))


              for (id in followingList!!)
              {
                  var countStory = 0
                  var story: Story? = null
                  for (snapshot in datasnapshot.child(id).children)
                  {
                      story = snapshot.getValue(Story::class.java)
                      if (timeCurrent>story!!.getTimeStart() && timeCurrent<story!!.getTimeEnd())
                      {
                          countStory++
                      }
                  }
                  if (countStory>0){
                      (storyList as ArrayList<Story>).add(story!!)
                  }
              }
              storyAdapter!!.notifyDataSetChanged()
          }

          override fun onCancelled(error: DatabaseError) {
              TODO("Not yet implemented")
          }


      } )
  }

}



