package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.MyMessagesVM

class MyMessagesFragment : Fragment() {

    val myMessagesVM by viewModels<MyMessagesVM>()

    private lateinit var viewModel: MyMessagesVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_messages, container, false)

        return view
    }



}