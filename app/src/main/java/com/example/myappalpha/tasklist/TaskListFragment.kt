package com.example.myappalpha.tasklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myappalpha.R
import com.example.myappalpha.databinding.ActivityMainBinding
import com.example.myappalpha.databinding.FragmentTaskListBinding
import java.util.*

class TaskListFragment : Fragment() {
    private val adapter = TaskListAdapter()
    private lateinit var binding : FragmentTaskListBinding

//    private var taskList = listOf("Task 1", "Task 2", "Task 3")

    private var taskList = listOf(
        Task(id = "id_1", title = "Task 1", description = "description 1"),
        Task(id = "id_2", title = "Task 2"),
        Task(id = "id_3", title = "Task 3")
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        adapter.submitList(taskList)
        binding = FragmentTaskListBinding.inflate(layoutInflater)
        val rootView = binding.root
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rootView = binding.root
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.task_list)
        recyclerView.adapter = adapter

        val button = rootView.getViewById(R.id.floatingActionButton)

        button.setOnClickListener{
            val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            taskList = taskList + newTask
            adapter.submitList(taskList)
            adapter.notifyDataSetChanged()
        }
    }
}
