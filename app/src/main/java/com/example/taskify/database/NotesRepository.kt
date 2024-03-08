package com.example.taskify.database

import androidx.lifecycle.LiveData
import com.example.taskify.MainActivity
import com.example.taskify.models.Note

class NotesRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: MainActivity.MySerializableData?){
        noteDao.insert(note)
    }

    suspend fun delete(note: Note){
        noteDao.delete(note)
    }

    suspend fun update(note: MainActivity.MySerializableData?){
        noteDao.update(note.id, note.title, note.note)
    }

}