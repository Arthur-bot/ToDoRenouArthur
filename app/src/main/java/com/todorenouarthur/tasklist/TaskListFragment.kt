package com.todorenouarthur.tasklist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.todorenouarthur.R
import com.todorenouarthur.form.FormActivity
import com.todorenouarthur.network.Api
import com.todorenouarthur.user.UserInfoActivity
import kotlinx.coroutines.launch

class TaskListFragment : Fragment()
{
    private val adapter = TaskListAdapter()
    private val taskListViewModel: TaskListViewModel by viewModels()

    private val formLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val newTask = result.data?.getSerializableExtra("task") as? Task

        if (newTask != null) {
            taskListViewModel.addOrEdit(newTask)

            adapter.notifyDataSetChanged()
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

        val userInfoTextView = view.findViewById<TextView>(R.id.UserInfoText)
        val userInfoImageView = view.findViewById<ImageView>(R.id.UserAvatar)

        recyclerView.adapter = adapter
        adapter.onClickDelete = { task ->
            taskListViewModel.delete(task)
            adapter.notifyDataSetChanged()
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

        userInfoImageView.setOnClickListener {
            val intent = Intent(context, UserInfoActivity::class.java)
            formLauncher.launch(intent)
        }

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            taskListViewModel.taskList.collect { newList ->
                adapter.submitList(newList)
            }
        }

        lifecycleScope.launch {
            val userInfo = Api.userWebService.getInfo().body()
            if (userInfo != null && userInfoTextView != null) {
                userInfoTextView.text = "${userInfo.firstName} ${userInfo.lastName}"
                userInfoImageView.load("https://goo.gl/gEgYUd") {
                    transformations(CircleCropTransformation())
                }
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        taskListViewModel.refresh() // on demande de rafra??chir les donn??es sans attendre le retour directement

        super.onResume()
    }
}