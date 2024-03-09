package com.example.taskify

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import com.example.taskify.databinding.ActivityAddNoteBinding
import com.example.taskify.models.Note
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

class AddNote : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    private lateinit var note: Note
    private lateinit var oldNote: Note
    var isUpdated = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        try{

            oldNote = intent.getSerializableExtraProvider("current_note") ?: Note(0,"","","")
            binding.etTitle.setText(oldNote.title)
            binding.etNote.setText(oldNote.note)
            isUpdated = true

        }catch(e: Exception){
            e.printStackTrace()
        }

        binding.imgCheck.setOnClickListener {

            val title = binding.etTitle.text.toString()
            val noteDesc = binding.etNote.text.toString()

            if(title.isNotEmpty() || noteDesc.isNotEmpty()){

                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")

                note = if(isUpdated){

                    Note(
                        oldNote.id, title, noteDesc, formatter.format(Date())
                    )

                }else{

                    Note(
                        null, title, noteDesc, formatter.format(Date())
                    )

                }

                val intent = Intent()
                intent.putExtra("note", note)
                setResult(Activity.RESULT_OK, intent)
                finish()

            }else{

                Toast.makeText(this@AddNote, "Please enter some data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Do nothing to prevent the system back button from navigating back
                }
            }
            onBackPressedDispatcher.addCallback(this, callback)

            binding.imgArrowBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

        }

    }

    @Suppress("DEPRECATION")
    private inline fun <reified T : Serializable> Intent.getSerializableExtraProvider(identifierParameter: String): T? {
        return if (Build.VERSION.SDK_INT >= 33) {
            this.getSerializableExtra(identifierParameter, T::class.java)
        } else {
            this.getSerializableExtra(identifierParameter) as? T
        }
    }

}