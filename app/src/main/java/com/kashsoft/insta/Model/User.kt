package com.kashsoft.insta.Model



class User

{
    private var username : String = ""
    private var fullname : String = ""
    private var bio : String = ""
    private var image : String = ""
    private var uid : String = ""

    constructor()

    constructor(username: String, fullname : String, bio :String, image: String , uid:String)

    {
        this.username = username
        this.fullname = fullname
        this.bio = bio
        this.uid = uid
        this.image = image
    }

fun getUsername(): String
{
    return username
}
    fun setUsername(username: String): String
    {
        return username
    }




    fun getFullname(): String
    {
        return fullname
    }
    fun setFullname(fullname: String): String
    {
        return fullname
    }




    fun getBio(): String
    {
        return bio
    }
    fun setBio(bio: String): String
    {
        return bio
    }




    fun getImage(): String
    {
        return image
    }
    fun setImage(image:  String): String
    {
        return image
    }



    fun getUID(): String
    {
        return uid
    }
    fun setUID(uid:  String): String
    {
        return uid
    }




}