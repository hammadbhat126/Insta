package com.kashsoft.insta.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.security.identity.AccessControlProfileId
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.kashsoft.insta.AccountSettingActivity
import com.kashsoft.insta.Model.User
import com.kashsoft.insta.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters


    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
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
        if (pref != null) {
            // changes need
            this.profileId = pref.getString("profileId", "none").toString()

        }

        if (profileId == firebaseUser.uid) {
            view.edit_account_setting_btn.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowAndFollowButtonStatus()
        }
        view.edit_account_setting_btn.setOnClickListener {
            startActivity(Intent(context, AccountSettingActivity::class.java))

        }

        // call the two methods get followes and get following
        getFollowers()
        getFollowings()
        userInfo()
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

    // new method to retrive followers list
    private fun getFollowers(){

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

    private fun userInfo(){
        val usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)

        usersRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(po: DataSnapshot) {

            //    if (context!= null)
            //    {
            //        return
           //     }
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
}