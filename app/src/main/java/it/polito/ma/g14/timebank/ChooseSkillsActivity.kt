//package it.polito.ma.g14.timebank
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuInflater
//import android.widget.LinearLayout
//import android.widget.SearchView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import it.polito.listapplication.Item
//import it.polito.listapplication.ItemAdapter
//
//class ChooseSkillsActivity : AppCompatActivity() {
//
//    var skill_entry_holder : LinearLayout? = null
//
//    var searchText : String = ""
//
//    lateinit var adapter: ItemAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_choose_skills)
//
//        val intentSkills = intent.getStringArrayListExtra("skills") ?: arrayListOf()
//        intentSkills.forEach {
//            val index =skill_list.indexOf(Item(it, false))
//            if(index!=-1){
//                skill_list[index].active = true
//            }
//        }
//
//
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putStringArrayList("skills", adapter.checked_skills as ArrayList<String>)
//        outState.putString("searchText", searchText)
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        adapter.checked_skills = savedInstanceState.getStringArrayList("skills") ?: arrayListOf()
//        searchText = savedInstanceState.getString("searchText", "")
//        findViewById<SearchView>(R.id.searchBar)?.setQuery(searchText, true)
//        findViewById<SearchView>(R.id.searchBar)?.clearFocus()
//        populateSkills()
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.navbar, menu)
//        supportActionBar?.title = "Choose your skills"
//        //supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#03a2ff")))
//        menu.findItem(R.id.pencil).setVisible(false)
//        return true
//    }
//
//    override fun onBackPressed() {
//        val resultData = Intent()
//        resultData.putExtra("skills", adapter.checked_skills as ArrayList<String>)
//        setResult(Activity.RESULT_OK, resultData)
//
//        super.onBackPressed()
//    }
//
//    private fun populateSkills(){
//
//
////        skill_entry_holder?.let{
////            it.removeAllViews()
////            skill_list.forEachIndexed { index, skill ->
////                val inflater: LayoutInflater = layoutInflater
////                val skill_entry: CheckedTextView =
////                    inflater.inflate(R.layout.skills_entry, null) as CheckedTextView
////                skill_entry.text = skill
////                if(checked_skills?.indexOf(skill)!=-1){
////                    skill_entry.isChecked = true
////                }
////                else{
////                    skill_entry.isChecked = false
////                }
////
////                skill_entry.id = index
////                skill_entry.setOnClickListener {
////                    val entry = it as CheckedTextView
////                    entry.isChecked = !entry.isChecked
////                    if (entry.isChecked && checked_skills.indexOf(entry.text.toString()) == -1) {
////                        checked_skills.add(entry.text.toString())
////                    } else {
////                        checked_skills.remove(entry.text.toString())
////                    }
////                }
////                it.addView(skill_entry)
////            }
////        }
//    }
//}