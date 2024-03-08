package com.example.taskify

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.taskify.databinding.ActivityAddNoteBinding
import com.example.taskify.models.Note
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date

class AddNote : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding

    private lateinit var note: Note
    private lateinit var old_note: Note
    var isUpdated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        try{
            old_note = intent.getSerializableExtraProvider("current_note") ?: Note(0,"","","")
            binding.etTitle.setText(old_note.title)
            binding.etNote.setText(old_note.note)
            isUpdated = true
        }catch(e: Exception){
            e.printStackTrace()
        }

        binding.imgCheck.setOnClickListener {

            val title = binding.etTitle.text.toString()
            val note_desc = binding.etNote.text.toString()

            if(title.isNotEmpty() || note_desc.isNotEmpty()){

                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")

                if(isUpdated){

                    note = Note(
                        old_note.id, title, note_desc, formatter.format(Date())
                    )

                }else{

                    note = Note(
                        null, title, note_desc, formatter.format(Date())
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

            binding.imgArrowBack.setOnClickListener {
                finish()
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