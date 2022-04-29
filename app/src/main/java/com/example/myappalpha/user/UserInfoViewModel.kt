package com.example.myappalpha.user

import com.example.myappalpha.network.Api

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myappalpha.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserInfoViewModel : ViewModel() {
    private val webService = Api.taskWebService

    // privée mais modifiable à l'intérieur du VM:
    private val _tasksStateFlow = MutableStateFlow<List<Task>>(emptyList())
    // même donnée mais publique et non-modifiable à l'extérieur afin de pouvoir seulement s'y abonner:
    public val tasksStateFlow: StateFlow<List<Task>> = _tasksStateFlow.asStateFlow()

    fun update(task: Task) {
        viewModelScope.launch {
            val response = webService.update(task)
            if (!response.isSuccessful) {
                Log.e("Network", "Error: ${response.raw()}")
                return@launch
            }
            val updatedTask = response.body()!!
            _tasksStateFlow.value = _tasksStateFlow.value - task + updatedTask
        }
    }
}