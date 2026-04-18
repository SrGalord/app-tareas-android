package com.example.galvisjaddys.data.task

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val time: String,
    val reminderTime: String,   // hora de la alarma
    val hasReminder: Boolean
)