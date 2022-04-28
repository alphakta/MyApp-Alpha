package com.example.myappalpha.form

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.myappalpha.R
import com.example.myappalpha.tasklist.Task
import java.util.*


class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val buttonValider = findViewById<Button>(R.id.btnValider)

        val editTextTask = findViewById<EditText>(R.id.editTextTask)
        val editTextTaskDescription = findViewById<EditText>(R.id.editTextDescription)

        val task = intent.getSerializableExtra("taskEdit") as Task?

        editTextTask.setText(task?.title)
        editTextTaskDescription.setText(task?.description)
        
        buttonValider.setOnClickListener {
            val newTask = Task(id = task?.id ?: UUID.randomUUID().toString(), title=editTextTask.text.toString(), description=editTextTaskDescription.text.toString())
            Log.d("test", "btnValider")
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}