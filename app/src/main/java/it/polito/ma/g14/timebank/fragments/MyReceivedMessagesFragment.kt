package it.polito.ma.g14.timebank.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.MyReceivedMessagesVM

class MyReceivedMessagesFragment : Fragment() {

    companion object {
        fun newInstance() = MyReceivedMessagesFragment()
    }

    private lateinit var viewModel: MyReceivedMessagesVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_received_messages, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyReceivedMessagesVM::class.java)
        // TODO: Use the ViewModel
    }

}