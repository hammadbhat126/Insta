package com.kashsoft.insta

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kashsoft.insta.Adapter.UserAdapter
import com.kashsoft.insta.Model.User

class ShowUsersActivity : AppCompatActivity()
{


    var id: String = ""
    var title: String = ""

    var userAdapter : UserAdapter? = null
    var userList: List<User>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)
    }
}