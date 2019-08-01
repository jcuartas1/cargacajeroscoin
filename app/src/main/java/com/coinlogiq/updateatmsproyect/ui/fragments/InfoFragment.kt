package com.coinlogiq.updateatmsproyect.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coinlogiq.updateatmsproyect.R
import com.coinlogiq.updateatmsproyect.ui.extensions.toast
import com.coinlogiq.updateatmsproyect.utils.CircleTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_info.view.*
import java.util.EventListener

class InfoFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private var chatSubscription: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _view =  inflater.inflate(R.layout.fragment_info, container, false)

        setUpChatDB()
        setUpCurrentUser()
        setUpCurrentUserInfoUI()

        //Firebase Style
        subscribeTotalMessagesFirebaseStyle()

        return _view
    }


    private fun setUpChatDB(){
        chatDBRef = store.collection("chat")
    }

    private fun setUpCurrentUser(){
        currentUser = mAuth.currentUser!!
    }

    private fun setUpCurrentUserInfoUI(){
        _view.textViewInfoEmail.text = currentUser.email
        _view.textViewInfoName.text = currentUser.displayName?.let { currentUser.displayName } ?: run {getString(R.string.info_no_name)}

        currentUser.photoUrl?.let {
                Picasso.get().load(it).resize(300,300)
                    .centerCrop().transform(CircleTransform()).into(_view.imageView)
        } ?: run {
            Picasso.get().load(R.drawable.ic_person).resize(300,300)
                .centerCrop().transform(CircleTransform()).into(_view.imageView)
        }
    }

    private fun subscribeTotalMessagesFirebaseStyle(){
        chatSubscription = chatDBRef.addSnapshotListener(object : EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot>{
            override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
                exception?.let {
                    activity!!.toast("Exception!")
                    return
                }
                snapshot?.let { _view.textViewInfoTotalMessages.text = "${it.size()}" }
            }
        })
    }

    override fun onDestroyView() {
        chatSubscription?.remove()
        super.onDestroyView()
    }

}
