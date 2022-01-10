package com.kashsoft.insta.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.kashsoft.insta.Model.Notification
import com.kashsoft.insta.R
import de.hdodenhof.circleimageview.CircleImageView

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

    }



    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {

        var postImage : ImageView = itemView.findViewById(R.id.notification_post_image)

        var profileImage: CircleImageView = itemView.findViewById(R.id.notification_profile_image)
        var userName : TextView = itemView.findViewById(R.id.username_notification)
        var text : TextView = itemView.findViewById(R.id.comment_notification)

    }


}