package com.example.myappalpha.tasklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myappalpha.R
import com.example.myappalpha.databinding.FragmentTaskListBinding

// l'IDE va râler ici car on a pas encore implémenté les méthodes nécessaires

    object TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            if(oldItem.id == newItem.id)
                return true
            return false
        }
        override fun areContentsTheSame(oldItem: Task, newItem: Task) : Boolean {
            if(oldItem == newItem)
                return true
            return false
        }
    }

    class TaskListAdapter : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
            return TaskViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: TaskViewHolder, position: Int)  {
            holder.bind(this.currentList[position].title)
        }

        inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(taskTitle: String) {
                val textView = itemView.findViewById<TextView>(R.id.task_title)
                textView.setText(taskTitle)
            }
        }
    }

    // on utilise `inner` ici afin d'avoir accès aux propriétés de l'adapter directement

