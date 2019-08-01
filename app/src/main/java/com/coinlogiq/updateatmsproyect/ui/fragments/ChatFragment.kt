package com.coinlogiq.updateatmsproyect.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.model.Message
import com.coinlogiq.updateatmsproyect.model.TotalMessagesEvent
import com.coinlogiq.updateatmsproyect.ui.adapters.ChatAdapter
import com.coinlogiq.updateatmsproyect.ui.extensions.toast
import com.coinlogiq.updateatmsproyect.utils.RxBus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.fragment_chat.view.editTextMessage
import java.util.*
import java.util.EventListener
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatFragment : Fragment() {

    private lateinit var _view : View
    private lateinit var adapter: ChatAdapter
    private val messaList: ArrayList<Message> = ArrayList()

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view = inflater.inflate(R.layout.fragment_chat, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpRecyclerView()
        setUpChatBtn()

        subscribeToChatMessages()

        return _view
        }

    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpRecyclerView(){
        val layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(messaList, currentUser.uid)

        _view.recyclerView.setHasFixedSize(true)
        _view.recyclerView.layoutManager = layoutManager
        _view.recyclerView.itemAnimator = DefaultItemAnimator()
        _view.recyclerView.adapter = adapter
    }

    private fun setUpChatBtn(){
        _view.buttonSend.setOnClickListener {
            val messaText = _view.editTextMessage.text.toString()
            if(messaText.isNotEmpty()){
                val photo = currentUser.photoUrl?.let { currentUser.photoUrl.toString() } ?: run { "" }
                val message = Message(currentUser.uid,messaText,photo,Date())
                saveMessage(message)
                _view.editTextMessage.setText("")
            }
        }
    }

    private fun saveMessage(message: Message){
        val newMessage = HashMap<String,Any>()
        newMessage["authorId"] = message.authorId
        newMessage["message"] = message.message
        newMessage["profileImagUrl"] = message.profileImagUrl
        newMessage["sentAt"] = message.sentAt

        chatDBRef.add(newMessage)
            .addOnCompleteListener {
                activity!!.toast("Message added!")
            }
            .addOnFailureListener {
                activity!!.toast("Message error, try again!")
            }
    }

    private fun subscribeToChatMessages(){
        chatSubscription = chatDBRef
            .orderBy("sentAt", Query.Direction.DESCENDING)
            .limit(100)
            .addSnapshotListener(object: EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast("Exception!")
                    return
                }
                snapshot?.let {
                    messaList.clear()
                    val messages = it.toObjects(Message::class.java)
                    messaList.addAll(messages.asReversed())
                    adapter.notifyDataSetChanged()
                    _view.recyclerView.smoothScrollToPosition(messaList.size)
                    RxBus.publish(TotalMessagesEvent(messaList.size))
                }
            }
        })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }

}

