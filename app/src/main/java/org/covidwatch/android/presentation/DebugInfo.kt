package org.covidwatch.android.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R
import org.covidwatch.android.databinding.FragmentDebugInfoBinding
import org.covidwatch.android.domain.InteractionViewAdapter
import org.covidwatch.android.domain.InteractionViewModel


class DebugInfo : Fragment() {

    private var _binding: FragmentDebugInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var intrcViewModel: InteractionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDebugInfoBinding.inflate(inflater, container, false)

        val recyclerView = binding.root.findViewById<RecyclerView>(R.id.interaction_view)
        val adapter = binding.root.context?.let { InteractionViewAdapter(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this.context)


        intrcViewModel = ViewModelProvider(this).get(InteractionViewModel::class.java)
        intrcViewModel.allInteractions.observe(viewLifecycleOwner, Observer { intr ->
            intr?.let { adapter?.setInteractions(it) }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}