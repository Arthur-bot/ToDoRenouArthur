package com.todorenouarthur.tasklist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todorenouarthur.R
import com.todorenouarthur.form.FormActivity

class TaskListFragment : Fragment()
{
    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )

    val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val newTask = result.data?.getSerializableExtra("task") as? Task

        if (newTask != null) {

            val oldTask = taskList.firstOrNull { it.id == newTask.id }
            if (oldTask != null) taskList = taskList - oldTask

            taskList += newTask
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = TaskListAdapter()
        adapter.currentList = taskList
        recyclerView.adapter = adapter
        adapter.onClickDelete = { task ->
            taskList = taskList - task
            adapter!!.notifyDataSetChanged()
        }

        adapter.onClickModify = { task ->
            val intent = Intent(context, FormActivity::class.java)
            intent.putExtra("task", task)
            formLauncher.launch(intent)
        }

        val fab =  view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        fab.setOnClickListener {
            val intent = Intent(context, FormActivity::class.java)
            formLauncher.launch(intent)
        }

        super.onViewCreated(view, savedInstanceState)
    }
}