package it.polito.ma.g14.timebank.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.MySentMessagesVM

class MySentMessagesFragment : Fragment() {

    companion object {
        fun newInstance() = MySentMessagesFragment()
    }

    private lateinit var viewModel: MySentMessagesVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_sent_messages, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MySentMessagesVM::class.java)
        // TODO: Use the ViewModel
    }

}