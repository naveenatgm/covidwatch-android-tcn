package org.covidwatch.android.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import org.covidwatch.android.R
import org.covidwatch.android.domain.InteractionViewModel

class InteractionsAllFragment : Fragment() {
    private lateinit var interactionsViewModel: InteractionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interactions_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val interactionContacts = view.findViewById<TextView>(R.id.all_interactions_contacts)
        val interactionMinutes = view.findViewById<TextView>(R.id.all_interactions_minutes)

        interactionsViewModel = ViewModelProvider(this).get(InteractionViewModel::class.java)
        interactionsViewModel.allNotifiedInteractions.observe(viewLifecycleOwner, Observer { intr ->
            intr?.let {
                interactionContacts?.setText((it.size).toString())
                var totalDuration = 0L
                it.forEach { interaction ->
                    totalDuration += interaction.lastSeen.time - interaction.interactionStart.time
                }

                interactionMinutes?.setText((totalDuration.toDouble() / (1000 * 60)).toLong().toString())
            }
        })
    }

}
