package com.example.socialapp.daos

import android.widget.Toast
import com.example.socialapp.MainActivity
import com.example.socialapp.models.Post
import com.example.socialapp.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Text

class PostDao {

    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("posts")
//    val userCollection = db.collection("users")
    val auth = Firebase.auth

    fun addpost(text: String){
        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
        val userDao =  UserDao()
        val user = userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            val currentTime = System.currentTimeMillis()
            val post = Post(text,user,currentTime)
            postCollection.document().set(post)

        }
    }


    fun deletePost(postId: String) {
        postCollection.document(postId).delete()
    }

//    fun deletePost(userId: String,postId: String){
//        postCollection.document(postId).delete()
//        GlobalScope.launch {
//            val currentUserId = auth.currentUser!!.uid
//                val user = User()
//                val post = Post()
//                val user = getUserById(userId).await().toObject(User::class.java)!!
//                val idPost = post.createdBy.uid
//                val idUser = user.uid
//                if (idUser == idPost) {
//                    postCollection.document(postId).delete()
//                }
//                else {
//                    Toast.makeText(MainActivity(), "You can't Delete this Post", Toast.LENGTH_LONG).show()
//                }
//            }
//    }

//    fun getUserById(userId: String): Task<DocumentSnapshot>{
//        return userCollection.document(userId).get()
//    }

    fun getPostById(postId: String): Task<DocumentSnapshot>{
        return postCollection.document(postId).get()
    }

    fun updateLikes(postId: String){
        GlobalScope.launch {
            val currentUserId = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            val isLiked = post.likedBy.contains(currentUserId)

            if (isLiked){
                post.likedBy.remove(currentUserId)
            }
            else{
                post.likedBy.add(currentUserId)
            }
            postCollection.document(postId).set(post)
        }

    }

}
