package com.example.galvisjaddys.ui.task

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.galvisjaddys.R
import com.example.galvisjaddys.ReminderReceiver
import com.example.galvisjaddys.data.task.Task
import com.example.galvisjaddys.data.task.TaskRepository
import java.util.*

class TaskDetailFragment : Fragment() {

    private var taskId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_task_detail, container, false)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etTime = view.findViewById<EditText>(R.id.etTime)
        val etReminderTime = view.findViewById<EditText>(R.id.etReminderTime)
        val cbReminder = view.findViewById<CheckBox>(R.id.cbReminder)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        crearCanalNotificacion()

        // 🔥 TIME PICKER (NO MÁS ERRORES DE FORMATO)
        etReminderTime.setOnClickListener {
            val calendar = Calendar.getInstance()

            val timePicker = TimePickerDialog(
                requireContext(),
                { _, hour, minute ->
                    val formatted = String.format("%02d:%02d", hour, minute)
                    etReminderTime.setText(formatted)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
            timePicker.show()
        }

        taskId = arguments?.getInt("taskId") ?: -1

        if (taskId != -1) {
            val task = TaskRepository.getTasks().find { it.id == taskId }

            task?.let {
                etTitle.setText(it.title)
                etDescription.setText(it.description)
                etTime.setText(it.time)
                etReminderTime.setText(it.reminderTime)
                cbReminder.isChecked = it.hasReminder
            }
        }

        btnSave.setOnClickListener {

            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            val time = etTime.text.toString()
            val reminderTime = etReminderTime.text.toString().trim()
            val reminder = cbReminder.isChecked

            if (title.isEmpty()) {
                Toast.makeText(requireContext(), "Ingrese un título", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (reminder && reminderTime.isEmpty()) {
                Toast.makeText(requireContext(), "Seleccione hora del recordatorio", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val task = Task(
                id = if (taskId == -1) {
                    (System.currentTimeMillis() % 100000).toInt()
                } else {
                    taskId
                },
                title = title,
                description = description,
                time = time,
                reminderTime = reminderTime,
                hasReminder = reminder
            )

            // Permiso notificaciones
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }

            if (taskId == -1) {
                TaskRepository.addTask(task, requireContext())
            } else {
                TaskRepository.updateTask(task, requireContext())
            }

            // 🔔 ALARMA SEGURA
            if (reminder) {
                try {

                    val parts = reminderTime.split(":")
                    if (parts.size != 2) {
                        Toast.makeText(requireContext(), "Hora inválida", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val hour = parts[0].toIntOrNull()
                    val minute = parts[1].toIntOrNull()

                    if (hour == null || minute == null) {
                        Toast.makeText(requireContext(), "Hora inválida", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val calendar = Calendar.getInstance()
                    calendar.set(Calendar.HOUR_OF_DAY, hour)
                    calendar.set(Calendar.MINUTE, minute)
                    calendar.set(Calendar.SECOND, 0)

                    if (calendar.timeInMillis <= System.currentTimeMillis()) {
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                    }

                    val intent = Intent(requireContext(), ReminderReceiver::class.java)
                    intent.putExtra("title", title)
                    intent.putExtra("description", description)

                    val pendingIntent = PendingIntent.getBroadcast(
                        requireContext(),
                        task.id,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    val alarmManager = requireContext()
                        .getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )

                    Toast.makeText(requireContext(), "Alarma programada a las $reminderTime", Toast.LENGTH_SHORT).show()

                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                    return@setOnClickListener
                }
            }

            Toast.makeText(requireContext(), "Tarea guardada", Toast.LENGTH_SHORT).show()
            findNavController().navigateUp()
        }

        return view
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "canal_tareas",
                "Recordatorios",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager = requireContext().getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }
}
