package com.kashsoft.insta.Model

class Post {

    private var postid: String = ""
    private var postimage: String = ""
    private var publisher: String = ""
    private var description: String = ""




    constructor()


    constructor(postid: String, publisher: String, description: String, postimage: String) {
        this.postid = postid
        this.publisher = publisher
        this.description = description
        this.postimage = postimage
    }


    fun getPostid() : String{

        return postid
    }

    fun getPostImage() : String{

        return postimage
    }

    fun getPublisher() : String{

        return publisher


    }

    fun getDescription() : String{

        return description


    }

    fun setPostid(postid: String){


    this.postid = postid
}

    fun setPostImage(postimage: String){


        this.postimage = postimage
    }
    fun setPublisher(publisher: String){


        this.publisher = publisher
    }
    fun setDescription(description: String){


        this.description = description
    }
}