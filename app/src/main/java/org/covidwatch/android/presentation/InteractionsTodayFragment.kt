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
import java.util.*

class InteractionsTodayFragment : Fragment() {
    private lateinit var interactionsViewModel: InteractionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_interactions_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val interactionContacts = view.findViewById<TextView>(R.id.todays_interactions_contacts)
        val interactionMinutes = view.findViewById<TextView>(R.id.todays_interactions_minutes)

        interactionsViewModel = ViewModelProvider(this).get(InteractionViewModel::class.java)
        interactionsViewModel.allNotifiedInteractions.observe(viewLifecycleOwner, Observer { intr ->
            intr?.let {

                val currentDate = Calendar.getInstance()
                currentDate.set(Calendar.HOUR_OF_DAY, 0)
                currentDate.set(Calendar.MINUTE, 0)
                currentDate.set(Calendar.SECOND, 0)
                currentDate.set(Calendar.MILLISECOND, 0)

                val dateStart = currentDate.time.time

                var todaysContacts = 0L
                var todaysDuration = 0L
                it.forEach { interaction ->
                    if (interaction.interactionStart.time >= dateStart) {
                        ++todaysContacts
                        todaysDuration += interaction.lastSeen.time - interaction.interactionStart.time
                    }
                }

                interactionContacts?.setText(todaysContacts.toString())
                interactionMinutes?.setText((todaysDuration.toDouble() / (1000 * 60)).toLong().toString())
            }
        })
    }
}
