package com.kashsoft.insta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kashsoft.insta.Model.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingActivity : AppCompatActivity() {


    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_account_setting)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        logout_btn.setOnClickListener{
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }

// for save the changes like user name , full name and bio and image
        save_info_profile_btn.setOnClickListener{
        if (checker == "clicked")
        {
            // for profile image change


        }else
        {

            // changes for only fullname , username and bio

            updateUserInfoOnly()
        }

        }

        userInfo()

    }

    private fun updateUserInfoOnly()

    {

       // this if statement to force user to write Full details with out skipping
        when {
            full_name_profile_frag.text.toString() == "" -> {
                Toast.makeText(this,"Please Write Full First", Toast.LENGTH_LONG).show()
            }
            user_name_profile_frag.text.toString() == "" -> {


                Toast.makeText(this,"Please Write user name  First", Toast.LENGTH_LONG).show()
            }
            bio_profile_frag.text.toString() == "" -> {

                Toast.makeText(this,"Please Write BIO", Toast.LENGTH_LONG).show()
            }
            else -> {


                val usersRef= FirebaseDatabase.getInstance().getReference().child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = user_name_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()


                usersRef.child(firebaseUser.uid).updateChildren(userMap)
                Toast.makeText(this, "Account Information has been Updated.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun userInfo(){
        val usersRef= FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser.uid)

        usersRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(po: DataSnapshot) {



                if (po.exists()){
                    val user = po.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_view_profile_frag)
                  user_name_profile_frag.setText(user!!.getUsername())
           full_name_profile_frag?.setText(user!!.getFullname())
             bio_profile_frag.setText(user!!.getBio())

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}