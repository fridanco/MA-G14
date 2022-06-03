package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.RVadapters.MyMessagesAdapter
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.models.MySentMessagesVM
import it.polito.ma.g14.timebank.utils.Utils

class MySentMessagesFragment(val sortBy: String, val filterBy: String) : Fragment() {

    val sentMessagesVM by viewModels<MySentMessagesVM>()
    val firebaseVM by viewModels<FirebaseVM>()

    lateinit var adapter: MyMessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sentMessagesVM.setSortBy(sortBy)
        sentMessagesVM.setFilterBy(filterBy)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_sent_messages, container, false)

        sentMessagesVM.getReceivedMessages(Firebase.auth.currentUser!!.uid)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rv = view.findViewById<RecyclerView>(R.id.sentMsgRV)
        val emptyRv = view.findViewById<TextView>(R.id.emptySentMsgRV2)

        //noinspection ResourceType
        val colorList = listOf(
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


        sentMessagesVM.sentMessages.observe(viewLifecycleOwner) {
            if(it.isEmpty()){
                rv.isGone = true
                emptyRv.isVisible = true
                emptyRv.text = "You have not received any message yet"
            }
            else {
                val sortBy = sentMessagesVM.getSortBy()
                val filterBy = sentMessagesVM.getFilterBy()
                if(adapter.updateMessages(it.toList(), sortBy, filterBy)>0){
                    rv.isVisible = true
                    emptyRv.isGone = true
                }
                else{
                    rv.isGone = true
                    emptyRv.isVisible = true
                    emptyRv.text = "No messages match your search"
                }
            }
        }

        sentMessagesVM.sortBy.observe(viewLifecycleOwner){
            if(adapter.addSort(it)>0){
                rv.isVisible = true
                emptyRv.isGone = true
            }
            else{
                rv.isGone = true
                emptyRv.isVisible = true
                if(sentMessagesVM.getFilterBy().isNotBlank()) {
                    emptyRv.text = "No messages match your search"
                }
                else{
                    emptyRv.text = "You have not received any message yet"
                }
            }
        }

        sentMessagesVM.filterBy.observe(viewLifecycleOwner){
            if(adapter.addFilter(it)>0){
                rv.isVisible = true
                emptyRv.isGone = true
            }
            else{
                rv.isGone = true
                emptyRv.isVisible = true
                emptyRv.text = "No messages match your search"
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

}