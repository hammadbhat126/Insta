package com.kashsoft.insta.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.security.identity.AccessControlProfileId
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.kashsoft.insta.AccountSettingActivity
import com.kashsoft.insta.Adapter.MyImagesAdapter
import com.kashsoft.insta.Model.Post
import com.kashsoft.insta.Model.User
import com.kashsoft.insta.R
import com.kashsoft.insta.ShowUsersActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList


class ProfileFragment : Fragment() {



    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser


    var postList: List<Post>? =null
    var myImagesAdapter : MyImagesAdapter?=null
    var myImagesAdapterSavedImg : MyImagesAdapter?=null
    var postListSaved: List<Post>? =null
    var mySavesImg: List<String>? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null)
        {
            // changes need  some real issue

            this.profileId = pref.getString("profileId" , "none")!!

        }

        if (profileId == firebaseUser.uid) {
            view.edit_account_setting_btn.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowButtonStatus()
        }


        // recycler View for uploaded Images

        var recylerViewUploadImages: RecyclerView
        recylerViewUploadImages = view.findViewById(R.id.recyler_view_upload_pic)
        recylerViewUploadImages.setHasFixedSize(true)
        val linearLayoutManager : LinearLayoutManager = GridLayoutManager(context, 3)
        recylerViewUploadImages.layoutManager = linearLayoutManager
        postList = ArrayList()
        myImagesAdapter = context?.let {MyImagesAdapter (it, postList as ArrayList<Post>) }
        recylerViewUploadImages.adapter = myImagesAdapter



        // recycler view for saveed images
        var recylerViewSavedImages: RecyclerView
        recylerViewSavedImages = view.findViewById(R.id.recyler_view_saved_pic)
        recylerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2 : LinearLayoutManager = GridLayoutManager(context, 3)
        recylerViewSavedImages.layoutManager = linearLayoutManager2
        postListSaved = ArrayList()
        myImagesAdapterSavedImg = context?.let {MyImagesAdapter (it, postListSaved as ArrayList<Post>) }
        recylerViewSavedImages.adapter = myImagesAdapterSavedImg



        // BY DEFAULT USER COME PROFILE PAGE UPLOADED PICTURE SHOULD BE SHOWED AS DEFAULT

        recylerViewSavedImages.visibility = View.GONE
        recylerViewUploadImages.visibility = View.VISIBLE


        val uploadedImagesBtn : ImageButton
        uploadedImagesBtn = view.findViewById(R.id.images_grid_view_btn)
        uploadedImagesBtn.setOnClickListener{
            recylerViewSavedImages.visibility = View.GONE
            recylerViewUploadImages.visibility = View.VISIBLE
        }


        val saveImagesBtn : ImageButton
        saveImagesBtn = view.findViewById(R.id.images_save_btn)
        uploadedImagesBtn.setOnClickListener{
            recylerViewSavedImages.visibility = View.VISIBLE
            recylerViewUploadImages.visibility = View.GONE
        }





     view.total_fallowers.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "followers")
            startActivity(intent)
        }



        view.total_fallowing.setOnClickListener {
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title", "following")
            startActivity(intent)
        }


        view.edit_account_setting_btn.setOnClickListener {
            val getButtonText = view.edit_account_setting_btn.text.toString()
            when{
                getButtonText =="Edit Profile" -> startActivity(Intent(context, AccountSettingActivity::class.java))
                getButtonText =="Follow" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString())
                            .setValue(true)
                    }
                    addNotification()

                }

                getButtonText =="Following" -> {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(profileId).removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1.toString()).removeValue()
                    }
                }
            }


        }

        // call the two methods get followers and get following
        getFollowers()
        getFollowings()
        userInfo()
        myPhotos()
        getTotalNumberOfPosts()
        mySaves()
        return view
    }

    private fun checkFollowAndFollowButtonStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }
        if (followingRef != null)
        {
            followingRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(po: DataSnapshot) {
                if (po.child(profileId).exists())
                {
                    view?.edit_account_setting_btn?.text= "Following"


                }else
                {
                    view?.edit_account_setting_btn?.text  = "Follow"

                }


                }

                override fun onCancelled(error: DatabaseError) {



                }
            })
        }


    }

    // new method to retrieve followers list
    private fun getFollowers(){
//note
        val followersRef =  FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")



        followersRef.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(po: DataSnapshot) {



                if(po.exists())
                {
                    view?.total_fallowers?.text = po.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun getFollowings(){
//note
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")



        followersRef.addValueEventListener(object :ValueEventListener
        {
            override fun onDataChange(po: DataSnapshot) {



                if(po.exists())
                {
                    view?.total_fallowing?.text = po.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    private  fun  myPhotos(){
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(po: DataSnapshot) {
               if (po.exists())
               {
                   (postList as ArrayList<Post>).clear()
                   for (snapshot in po.children)
                   {
                       val post = snapshot.getValue(Post::class.java)!!
                       if (post.getPublisher().equals(profileId))
                       {
                           (postList as ArrayList<Post>).add(post)
                       }
                       Collections.reverse(postList)
                       myImagesAdapter!!.notifyDataSetChanged()
                   }
               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }
    private fun userInfo(){
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(po: DataSnapshot) {

// note 25.40

                if (po.exists()){
                    val user = po.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(view?.pro_image_profile_fragment)
                    view?.profile_fragment_username?.text=user!!.getUsername()
                    view?.full_name_profile_fragment?.text=user!!.getFullname()
                    view?.bio_profile_fragment?.text=user!!.getBio()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
    private fun getTotalNumberOfPosts()
    {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()){
                    var postCounter = 0

                    for (snapshot in dataSnapshot.children)
                    {
                        val post = snapshot.getValue(Post::class.java)!!
                        if (post.getPublisher() == profileId)

                        {
                        postCounter++

                        }

                    }
                    total_posts.text = "" + postCounter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        }

private fun mySaves()
{
    mySavesImg = ArrayList()
    val saveRef= FirebaseDatabase.getInstance()
        .reference
        .child("Saves").child(firebaseUser.uid)

    saveRef.addValueEventListener(object : ValueEventListener{

        override fun onDataChange(datasnapshot: DataSnapshot) {


            if (datasnapshot.exists()){
                for (snapshot in datasnapshot.children)
                {
                    (mySavesImg as ArrayList<String>).add(snapshot.key!!)
                }
                readSavedImgData()
            }
        }
        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }


    })
}

    private fun readSavedImgData()
    {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
            if (datasnapshot.exists())
            {
                (postListSaved as ArrayList<Post>).clear()

                for (snapshot in datasnapshot.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    for (key in mySavesImg!!)
                    {
                        if (post!!.getPostid() ==key)
                        {
                            ( postListSaved as ArrayList<Post>).add(post!!)
                        }
                    }
                }
                myImagesAdapterSavedImg!!.notifyDataSetChanged()
            }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun addNotification()
    {
        val notiRef = FirebaseDatabase.getInstance()
            .reference.child("Notifications")
            .child(profileId)

        val notiMap = HashMap<String, Any>()
        notiMap["userid"] =firebaseUser!!.uid
        notiMap["test"] = "started following you"
        notiMap["postid"] = ""
        notiMap["ispost"] = false

        notiRef.push().setValue(notiMap)
    }
}