package com.todorenouarthur.tasklist

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.todorenouarthur.R

import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider


class TaskListAdapter : RecyclerView.Adapter<TaskListAdapter.TaskViewHolder>() {

    lateinit var currentList: List<Task>

    // Déclaration de la variable lambda dans l'adapter:
    var onClickDelete: (Task) -> Unit = {}
    var onClickModify: (Task) -> Unit = {}

    // on utilise `inner` ici afin d'avoir accès aux propriétés de l'adapter directement
    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var titleTextView = itemView.findViewById<TextView>(R.id.task_title)
        var descriptionTextView = itemView.findViewById<TextView>(R.id.task_description)
        var deleteButton = itemView.findViewById<ImageButton>(R.id.deleteButton)
        var modifyButton = itemView.findViewById<ImageButton>(R.id.modifyButton)

        fun bind(task: Task) {
            titleTextView.text = task.title
            descriptionTextView.text = task.description

            deleteButton.setOnClickListener {
                onClickDelete(task)
            }

            modifyButton.setOnClickListener{
                onClickModify(task)
            }
        }
    }

    override fun getItemCount(): Int {
        return this.currentList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
        val view: View = itemView.inflate(
            R.layout.item_task,
            parent,
            false
        )

        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}