package com.example.myappalpha.tasklist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.myappalpha.R
import com.example.myappalpha.databinding.ActivityMainBinding
import com.example.myappalpha.databinding.FragmentTaskListBinding
import com.example.myappalpha.form.FormActivity
import com.example.myappalpha.network.Api
import com.example.myappalpha.network.TasksListViewModel
import com.example.myappalpha.network.UserInfo
import com.example.myappalpha.user.UserInfoActivity
import kotlinx.coroutines.launch
import java.util.*

class TaskListFragment : Fragment() {
    private val viewModel: TasksListViewModel by viewModels()
    private val listener : TaskListListener = object : TaskListListener {
        override fun onClickDelete(task: Task) {
            viewModel.delete(task)
        }

        override fun onClickEdit(task: Task) {
            val intent = Intent(context, FormActivity::class.java)
            intent.putExtra("taskEdit", task)
            editTask.launch(intent)
            viewModel.update(task)
        }
    }
    private val adapter = TaskListAdapter(listener)
    private lateinit var binding : FragmentTaskListBinding

    val createTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        viewModel.create(task)
    }
    val editTask = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = result.data?.getSerializableExtra("task") as Task? ?: return@registerForActivityResult
        viewModel.update(task)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            val infoUser = Api.userWebService.getInfo().body()!!
            val userInfoTextView = view?.findViewById<TextView>(R.id.userInfoTxtView)
            val avatarImageView = view?.findViewById<ImageView>(R.id.avatarImageView)
//          val lienImage = "https://static.onzemondial.com/photo_article/682895/266910/1200-L-real-madrid-la-statistique-affolante-de-karim-benzema.jpg"
            avatarImageView?.load(infoUser.avatar){
                crossfade(true)
                transformations(CircleCropTransformation())
                error(R.drawable.ic_launcher_background) // affiche une image par défaut en cas d'erreur:
            }
            userInfoTextView?.text = "${infoUser.firstName} ${infoUser.lastName}"
            avatarImageView?.setOnClickListener{
                val intent = Intent(context, UserInfoActivity::class.java)
                intent.putExtra("lienImage", infoUser.avatar)
                startActivity(intent)
            }

        }
        viewModel.refresh() // on demande de rafraîchir les données sans attendre le retour directement
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTaskListBinding.inflate(layoutInflater)
        val rootView = binding.root
        return rootView
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rootView = binding.root
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.task_list)
        recyclerView.adapter = adapter

        val buttonAddTask = rootView.getViewById(R.id.btnAddTask)

        buttonAddTask.setOnClickListener{
            // val newTask = Task(id = UUID.randomUUID().toString(), title = "Task ${taskList.size + 1}")
            // taskList = taskList + newTask
            // adapter.submitList(taskList)
            // adapter.notifyDataSetChanged()
            val intent = Intent(context, FormActivity::class.java)
            createTask.launch(intent)
        }

        lifecycleScope.launch { // on lance une coroutine car `collect` est `suspend`
            viewModel.tasksStateFlow.collect { newList ->
                // cette lambda est executée à chaque fois que la liste est mise à jour dans le VM
                // -> ici, on met à jour la liste dans l'adapter
                adapter.submitList(newList)
            }
        }
    }
}
