package com.example.myappalpha

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.myappalpha.tasklist.TaskListAdapter
import com.example.myappalpha.tasklist.TaskListFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        val Fragment = TaskListFragment()
//        Fragment.onViewCreated(findViewById<RecyclerView>(R.id.task_list), savedInstanceState)
    }

}