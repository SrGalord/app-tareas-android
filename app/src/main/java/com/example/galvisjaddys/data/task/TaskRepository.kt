package com.example.galvisjaddys.data.task

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object TaskRepository {

    private val taskList = mutableListOf<Task>()
    private val gson = Gson()

    fun loadTasks(context: Context) {
        val prefs = context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("tasks", null)

        if (json != null) {
            val type = object : TypeToken<MutableList<Task>>() {}.type
            val savedTasks: MutableList<Task> = gson.fromJson(json, type)
            taskList.clear()
            taskList.addAll(savedTasks)
        }
    }

    fun saveTasks(context: Context) {
        val prefs = context.getSharedPreferences("tasks_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val json = gson.toJson(taskList)
        editor.putString("tasks", json)
        editor.apply()
    }

    fun addTask(task: Task, context: Context) {
        taskList.add(task)
        saveTasks(context)
    }

    fun getTasks(): List<Task> {
        return taskList
    }

    fun updateTask(updatedTask: Task, context: Context) {
        val index = taskList.indexOfFirst { it.id == updatedTask.id }
        if (index != -1) {
            taskList[index] = updatedTask
            saveTasks(context)
        }
    }
    fun deleteTask(task: Task) {
        taskList.remove(task)
    }
}