package com.kashsoft.insta.Adapter

import android.content.Context
import android.content.LocusId
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kashsoft.insta.Fragments.PostDetailsFragment
import com.kashsoft.insta.Fragments.ProfileFragment
import com.kashsoft.insta.Model.Notification
import com.kashsoft.insta.Model.Post
import com.kashsoft.insta.Model.User
import com.kashsoft.insta.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_comments.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class NotificationAdapter(private val mContext:Context,
                          private val mNotificaton: List<Notification>)
    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notification_item_layout, parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return mNotificaton.size

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    val notification = mNotificaton[position]

        if (notification.getText() == "started following you")
        {
            holder.text.text = "started following you"
        }else if(notification.getText() == "start liked you")
                {
            holder.text.text = "liked your post"
        }
        else if(notification.getText().contains("commented"))
        {
            holder.text.text = notification.getText().replace("commented:", "commented: ")
        }else{
            holder.text.text = notification.getText()
        }




        userInfo(holder.profileImage, holder.userName,notification.getUserId())

      if (notification.getIsPost()){   // is is check

          holder.postImage.visibility = View.VISIBLE
          getPostImage(holder.postImage, notification.getPostId())
      }else
      {
          holder.postImage.visibility = View.GONE
      }

    holder.itemView.setOnClickListener{
        if (notification.getIsPost())
        {
            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE ).edit()
            editor.putString("postId" , notification.getPostId())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().
            replace(R.id.fragment_container, PostDetailsFragment()).commit()
        }else
        {

            val editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE ).edit()
            editor.putString("profileId" , notification.getUserId())
            editor.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().
            replace(R.id.fragment_container, ProfileFragment()).commit()
        }
    }


    }



    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {

        var postImage : ImageView = itemView.findViewById(R.id.notification_post_image)

        var profileImage: CircleImageView = itemView.findViewById(R.id.notification_profile_image)
        var userName : TextView = itemView.findViewById(R.id.username_notification)
        var text : TextView = itemView.findViewById(R.id.comment_notification)




    }
    private fun userInfo(imageView: ImageView, userName: TextView, publisherId: String){
        val usersRef= FirebaseDatabase
            .getInstance().getReference()
            .child("Users").child(publisherId)

        usersRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(po: DataSnapshot) {


                if (po.exists()){
                    val user = po.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(imageView)
               userName.text=user!!.getUsername()


                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    private fun getPostImage(imageView: ImageView, postID: String ) {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
            .child(postID)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(po: DataSnapshot) {
                if (po.exists())
                {


                    val post = po.getValue<Post>(Post::class.java)
                    Picasso.get().load(post!!.getPostimage())
                        .placeholder(R.drawable.profile).into(imageView)




                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}