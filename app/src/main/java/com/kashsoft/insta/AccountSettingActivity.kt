package com.kashsoft.insta

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.kashsoft.insta.Model.User
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_account_setting.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class AccountSettingActivity : AppCompatActivity() {


    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null

    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        setContentView(R.layout.activity_account_setting)


        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("Profile Pictures")

        logout_btn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }


        change_image_text_btn.setOnClickListener {
            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this@AccountSettingActivity)
        }
// for save the changes like user name , full name and bio and image
        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked") {
                // for profile image change


                uploadImageAndUpdateInfo()

            } else {

                // changes for only fullname , username and bio

                updateUserInfoOnly()
            }

        }

        userInfo()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null
        ) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view_profile_frag.setImageURI(imageUri)
        } else {

        }
    }

    private fun updateUserInfoOnly() {

        // this if statement to force user to write Full details with out skipping
        when {
            full_name_profile_frag.text.toString() == "" -> {
                Toast.makeText(this, "Please Write Full First", Toast.LENGTH_LONG).show()
            }
            user_name_profile_frag.text.toString() == "" -> {


                Toast.makeText(this, "Please Write user name  First", Toast.LENGTH_LONG).show()
            }
            bio_profile_frag.text.toString() == "" -> {

                Toast.makeText(this, "Please Write BIO", Toast.LENGTH_LONG).show()
            }
            else -> {


                val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = user_name_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()


                usersRef.child(firebaseUser.uid).updateChildren(userMap)
                Toast.makeText(this, "Account Information has been Updated.", Toast.LENGTH_LONG)
                    .show()

                val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(po: DataSnapshot) {


                if (po.exists()) {
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


    private fun uploadImageAndUpdateInfo() {


        when {
            imageUri == null -> Toast.makeText(this, "Please Select Image", Toast.LENGTH_LONG)
                .show()

            TextUtils.isEmpty(full_name_profile_frag.text.toString()) ->
                Toast.makeText(this, "Please Write Full First", Toast.LENGTH_LONG).show()

            user_name_profile_frag.text.toString() == "" ->
                Toast.makeText(this, "Please Write user name  First", Toast.LENGTH_LONG).show()

            bio_profile_frag.text.toString() == "" ->
                Toast.makeText(this, "Please Write BIO", Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Accounts Settings")
                progressDialog.setMessage("Please wait we are updating your profile...")
                progressDialog.show()


                val fileref = storageProfilePicRef!!.child(firebaseUser!!.uid + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileref.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileref.downloadUrl
                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->

                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                        userMap["username"] = user_name_profile_frag.text.toString().toLowerCase()
                        userMap["bio"] = bio_profile_frag.text.toString().toLowerCase()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(
                            this,
                            "Account Information has been Updated.",
                            Toast.LENGTH_LONG
                        )
                            .show()

                        val intent = Intent(this@AccountSettingActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()

                    } else {
                        progressDialog.dismiss()
                    }
                })

            }
        }
    }
}

