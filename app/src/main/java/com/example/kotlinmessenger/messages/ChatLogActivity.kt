package com.example.kotlinmessenger.messages

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.example.kotlinmessenger.views.ChatFromitem
import com.example.kotlinmessenger.views.ChatToitem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*


class ChatLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

  //      val username = intent.getStringExtra(NewMessageActivity.USER_KEY)

   //     val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
   //     supportActionBar?.title = toUser?.username

        val adapter = GroupAdapter<GroupieViewHolder>()

        listenForMessages()
 //       setupDummyData()

        edittext_chat_log.setOnClickListener {
            Log.d(TAG, "Clicking edit text")
            expanded_edittext_chat_log.visibility = View.VISIBLE
            emoji_button_chat_log2.visibility = View.VISIBLE
            edittext_chat_log.visibility = View.GONE
            expand_button_chat_log.visibility = View.GONE
            camera_button_chat_log.visibility = View.GONE
            image_button_chat_log.visibility = View.GONE
            mic_button_chat_log.visibility = View.GONE
            emoji_button_chat_log.visibility = View.GONE





//            val anim: Animation =
//                AnimationUtils.loadAnimation(
//                    this,
//                    R.anim.expand_edittext_into_chatmode
//                )
//            edittext_chat_log.startAnimation(anim)
        }

        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message")
            performSendMessage()

        }

    }
    private fun listenForMessages()
    {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if(chatMessage != null)
                {
                    Log.d(TAG, chatMessage.text)

                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid)
                    {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromitem(chatMessage.text, currentUser))

                    }
                    else
                    {
                        adapter.add(ChatToitem(chatMessage.text, toUser!!))
                    }
                }

                recyclerview_chat_log.scrollToPosition(adapter.itemCount-1)


            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }


    private fun performSendMessage()
    {
        //how do we actually send a message to firebase....
        val text = expanded_edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user.uid

        if (fromId == null) return

//        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved our chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount -1)
            }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/" +
                "$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/" +
                "$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
    private fun setupDummyData() {

        val adapter = GroupAdapter<GroupieViewHolder>()
//        adapter.add(ChatFromitem("FROM MESSSSSSAGEEEE"))
//        adapter.add(ChatToitem("TO MESSAGE\n TO MESSAGE"))
//        adapter.add(ChatFromitem("FROM MESSSSSSAGEEEE"))
//        adapter.add(ChatToitem("TO MESSAGE\n TO MESSAGE"))
//        adapter.add(ChatFromitem("FROM MESSSSSSAGEEEE"))
//        adapter.add(ChatToitem("TO MESSAGE\n TO MESSAGE"))
//        adapter.add(ChatFromitem("FROM MESSSSSSAGEEEE"))
//        adapter.add(ChatToitem("TO MESSAGE\n TO MESSAGE"))
//        adapter.add(ChatFromitem("FROM MESSSSSSAGEEEE"))
//        adapter.add(ChatToitem("TO MESSAGE\n TO MESSAGE"))

        recyclerview_chat_log.adapter = adapter

    }

}

