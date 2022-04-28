package com.example.myappalpha.tasklist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myappalpha.R
import com.example.myappalpha.databinding.FragmentTaskListBinding
import java.util.*

// l'IDE va râler ici car on a pas encore implémenté les méthodes nécessaires

object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        if (oldItem.id == newItem.id)
            return true
        return false
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        if (oldItem == newItem)
            return true
        return false
    }
}

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
}

class TaskListAdapter(val listener: TaskListListener) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback) {
//        var onClickDelete: (Task) -> Unit = {}
//        var onClickEdit: (Task) -> Unit = {}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = this.currentList[position]
        holder.bind(task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(task: Task) {
            val textView = itemView.findViewById<TextView>(R.id.task_title)
            textView.setText(task.title)
            val buttonDelete = itemView.findViewById<ImageButton>(R.id.btnDeleteTask)
            buttonDelete.setOnClickListener { listener.onClickDelete(task) }

            val buttonEdit = itemView.findViewById<Button>(R.id.btnEditer)
            buttonEdit.setOnClickListener { listener.onClickEdit(task) }
        }

    }
}

// on utilise `inner` ici afin d'avoir accès aux propriétés de l'adapter directement

