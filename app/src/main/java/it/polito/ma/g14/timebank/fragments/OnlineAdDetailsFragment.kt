package it.polito.ma.g14.timebank.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import it.polito.ma.g14.timebank.R
import it.polito.ma.g14.timebank.models.Advertisement
import it.polito.ma.g14.timebank.models.FirebaseVM
import it.polito.ma.g14.timebank.utils.Utils


class OnlineAdDetailsFragment() : Fragment() {

    private val vm by viewModels<FirebaseVM>()

    lateinit var tv_title : TextView
    lateinit var tv_description : TextView
    lateinit var tv_date : TextView
    lateinit var tv_from : TextView
    lateinit var tv_to : TextView
    lateinit var tv_location : TextView
    lateinit var tv_fullname : TextView
    lateinit var tv_user_description : TextView
    lateinit var iv_profileImage : ImageView
    lateinit var btn_book : Button
    lateinit var btn_chat : Button

    lateinit var advertisement : Advertisement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_online_ad_details, container, false)

        requireActivity().invalidateOptionsMenu()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_title = view.findViewById<TextView>(R.id.textView4)
        tv_description = view.findViewById<TextView>(R.id.textView5)
        tv_date = view.findViewById<TextView>(R.id.textView64)
        tv_from = view.findViewById<TextView>(R.id.textView62)
        tv_to = view.findViewById<TextView>(R.id.textView63)
        tv_location = view.findViewById<TextView>(R.id.textView19)
        tv_fullname = view.findViewById<TextView>(R.id.textView77)
        tv_user_description = view.findViewById<TextView>(R.id.textView74)
        iv_profileImage = view.findViewById<ImageView>(R.id.imageView6)
        btn_book = view.findViewById(R.id.button7)
        btn_chat = view.findViewById(R.id.button8)

        advertisement = requireArguments().getSerializable("advertisement") as Advertisement

        tv_title.text = advertisement.title
        tv_description.text = advertisement.description
        tv_date.text = advertisement.date
        tv_from.text = advertisement.from
        tv_to.text = advertisement.to
        tv_location.text = advertisement.location
        tv_fullname.text = "By ${advertisement.user.fullname}"
        if(advertisement.user.description.isNotEmpty()) {
            tv_user_description.text = advertisement.user.description
        }
        else{
            tv_user_description.text = "No description provided"
        }
        btn_book.setOnClickListener {

        }
        btn_chat.setOnClickListener {
            startChat()
        }

        val profileImageRef = vm.storageRef.child(advertisement.uid)

        val options: RequestOptions = RequestOptions()
            .placeholder(R.drawable.user)
            .error(R.drawable.user)

        Glide.with(requireContext())
            .load(profileImageRef)
            .apply(options)
            .into(iv_profileImage)

    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        Utils.manageActionBarItemsVisibility(requireActivity(), menu)
    }

    fun bookSlot(){

    }

    fun startChat(){
        val bundle = bundleOf("advertisementID" to advertisement.id, "advertiserUID" to advertisement.uid)
        view?.findNavController()?.navigate(R.id.action_onlineAdDetailsFragment_to_chatFragment, bundle)
    }
}