package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.MyAdvertisementsAdapter
import it.polito.ma.g14.timebank.RVadapters.MyMessagesAdapter
import it.polito.ma.g14.timebank.models.*
import it.polito.ma.g14.timebank.utils.Utils

class MySentMessagesFragment : Fragment() {

    val sentMessagesVM by viewModels<MySentMessagesVM>()
    val firebaseVM by viewModels<FirebaseVM>()

    lateinit var adapter: MyMessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_sent_messages, container, false)

        requireActivity().invalidateOptionsMenu()

        sentMessagesVM.getReceivedMessages(Firebase.auth.currentUser!!.uid)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.sentMsgRV)
        val emptyRv = view.findViewById<TextView>(R.id.emptySentMsgRV2)

        //noinspection ResourceType
        val colorList = listOf<String>(
            resources.getString(R.color.purple),
            resources.getString(R.color.orange),
            resources.getString(R.color.red),
            resources.getString(R.color.green),
            resources.getString(R.color.eletric_blue),
        )

        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = MyMessagesAdapter(view, firebaseVM, requireContext(), "sent_msg")
        adapter.colorList = colorList as MutableList<String>
        rv.adapter = adapter


        sentMessagesVM.sentMessages.observe(viewLifecycleOwner) { it ->
            if(it.isEmpty()){
                rv.isGone = true
                emptyRv.isVisible = true
            }
            else {
                rv.isVisible = true
                emptyRv.isGone = true
                val sortBy = sentMessagesVM.getSortBy()
                adapter.updateMessages(it.toList(), sortBy)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

}