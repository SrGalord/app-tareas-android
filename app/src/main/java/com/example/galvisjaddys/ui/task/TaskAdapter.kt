package com.example.galvisjaddys.ui.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.galvisjaddys.R
import com.example.galvisjaddys.data.task.Task
import com.example.galvisjaddys.data.task.TaskRepository

class TaskAdapter(
    private val taskList: List<Task>,
    private val onClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val cbDone: CheckBox = view.findViewById(R.id.cbDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val task = taskList[position]

        holder.tvTitle.text = task.title
        holder.tvDescription.text = task.description
        holder.tvTime.text = task.time

        // Click para editar
        holder.itemView.setOnClickListener {
            onClick(task)
        }

        // 🔥 Check para eliminar
        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = false

        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                TaskRepository.deleteTask(task)
                notifyDataSetChanged()
            }
        }
    }
}