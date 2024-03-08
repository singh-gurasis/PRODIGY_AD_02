package com.example.taskify

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.taskify.adapters.NotesAdapter
import com.example.taskify.database.NoteDatabase
import com.example.taskify.databinding.ActivityMainBinding
import com.example.taskify.models.Note
import com.example.taskify.models.NoteViewModel
import java.io.Serializable

class MainActivity : AppCompatActivity(), NotesAdapter.NotesClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var selectedNote: Note

    private val updateNote = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->

        if(result.resultCode == Activity.RESULT_OK){

            val myNote = MySerializableData("note")
            intent.putExtra("myNote", myNote)

            val note: MySerializableData? = intent.getSerializableExtraProvider("myNote")
            if(note != null){
                viewModel.updateNote(note)
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initUI()

        viewModel = ViewModelProvider(this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application))[NoteViewModel::class.java]

        viewModel.allnotes.observe(this) {list ->
            list?.let{
                adapter.updateList(list)
            }
        }

        database = NoteDatabase.getDatabase(this)

    }

    private fun initUI() {

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this, this)
        binding.recyclerView.adapter = adapter

        val getContent = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ result ->

            if(result.resultCode == Activity.RESULT_OK){

                val myNote = MySerializableData("note")
                intent.putExtra("myNote", myNote)

                val note: MySerializableData? = intent.getSerializableExtraProvider("myNote")
                if(note != null){

                    viewModel.insertNote(note)

                }

            }

        }

        binding.fbAddNote.setOnClickListener {

            val intent = Intent(this, AddNote::class.java)
            getContent.launch(intent)

        }

        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {

                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {

                if(newText != null){
                    adapter.filterList(newText)
                }

                return true

            }

        })

    }

    @Suppress("DEPRECATION")
    private inline fun <reified T: Serializable> Intent.getSerializableExtraProvider(identifierParameter: String): T? {
        return if (Build.VERSION.SDK_INT >= 33) {
            this.getSerializableExtra(identifierParameter, T::class.java)
        } else {
            this.getSerializableExtra(identifierParameter) as T?
        }
    }

    data class MySerializableData(val message: String) : Serializable

    override fun onItemClicked(note: Note) {

        val intent = Intent(this@MainActivity, AddNote::class.java)
        intent.putExtra("current_note", note)
        updateNote.launch(intent)

    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popUpDisplay(cardView)
    }

    private fun popUpDisplay(cardView: CardView) {

        val popup = PopupMenu(this, cardView)
        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        if(item?.itemId == R.id.delete_note){

            viewModel.deleteNote(selectedNote)
            return true

        }
        return false
    }

}