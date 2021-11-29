package com.kashsoft.insta

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)



        SignIn_link_btn.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))


        }
        Signup_btn.setOnClickListener {
            CreateAccount()


        }

    }



    private fun CreateAccount() {
        val fullname = full_name_signUp.text.toString()
        val userName = user_name_signUp.text.toString()
        val email = email_signUp.text.toString()
        val password = password_signup.text.toString()

        when{

            TextUtils.isEmpty(fullname) -> Toast.makeText(this,"full name is required" ,
            Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(userName) -> Toast.makeText(this,
                "user name is required" ,Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(email) ->
                Toast.makeText(this,"email is required" ,Toast.LENGTH_LONG).show()

            TextUtils.isEmpty(password) -> Toast.makeText(
                this,"password is required" ,Toast.LENGTH_LONG).show()
            else  -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)

                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please Wait,This may take a while")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener{task ->
                      if (task.isSuccessful)
                      {
                        saveUserInfo(fullname,userName, email ,progressDialog)
                      }
                        else
                      {
                          val message = task.exception!!.toString()
                          Toast.makeText(this,
                              "Error: $message",Toast.LENGTH_LONG).show()
                          mAuth.signOut()
                          progressDialog.dismiss()

                      }
                    }
            }

        }


    }

    private fun saveUserInfo(
        fullname: String,
        userName: String,
        email: String,
        progressDialog: ProgressDialog
    ) {

        var currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef:DatabaseReference = FirebaseDatabase.getInstance().
        reference.child("users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullname.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "hey i am using this app"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/my-project-1554304913445.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=381cb118-5da0-4fd5-9419-98b6888db9a8"
        userRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener{ task ->
                if(task.isSuccessful)
                {
                progressDialog.dismiss()
                    Toast.makeText(this,
                        "Account have been Sucessfull",Toast.LENGTH_LONG).show()
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this,
                        "Error: ",Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }

            }
}