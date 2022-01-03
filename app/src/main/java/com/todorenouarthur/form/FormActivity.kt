package com.todorenouarthur.form

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todorenouarthur.R
import com.todorenouarthur.tasklist.Task
import java.util.*

class FormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        val title = this.findViewById<EditText>(R.id.editTextName)
        val description = this.findViewById<EditText>(R.id.editTextDescription)
        val newTask = Task(id = UUID.randomUUID().toString(), title = "New Task !")

        val task = intent.getSerializableExtra("task") as? Task
        if (task != null) {
            title.setText(task.title)
            description.setText(task.description)

            newTask.id = task.id
        }


        val validate =  this.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        validate.setOnClickListener {
            newTask.title = title.text.toString()
            newTask.description = description.text.toString()
            intent.putExtra("task", newTask)
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}