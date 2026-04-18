package com.example.galvisjaddys.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.galvisjaddys.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.galvisjaddys.data.task.TaskRepository

class TaskListFragment : Fragment() {

    private var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        // Botón agregar
        val btnAdd = view.findViewById<FloatingActionButton>(R.id.btnAdd)

        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_list_to_detail)
        }

        // RecyclerView
        recyclerView = view.findViewById(R.id.recyclerTasks)
        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        TaskRepository.loadTasks(requireContext())

        return view
    }

    override fun onResume() {
        super.onResume()

        val tasks = TaskRepository.getTasks()

        recyclerView?.adapter = TaskAdapter(tasks) { task ->

            val bundle = Bundle()
            bundle.putInt("taskId", task.id)

            findNavController().navigate(R.id.action_list_to_detail, bundle)
        }
    }
}