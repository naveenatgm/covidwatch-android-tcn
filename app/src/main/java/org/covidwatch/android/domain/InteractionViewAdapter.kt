package org.covidwatch.android.domain

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.covidwatch.android.R
import org.covidwatch.android.data.Interaction
import java.text.SimpleDateFormat

class InteractionViewAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<InteractionViewAdapter.InteractionViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var interactionsList = emptyList<Interaction>() // Cached copy of words

    inner class InteractionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val debugInfo: TextView = itemView.findViewById(R.id.txt_debug_info)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InteractionViewHolder {
        val itemView = inflater.inflate(R.layout.debug_info_item, parent, false)
        return InteractionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InteractionViewHolder, position: Int) {
        val current = interactionsList[position]
        val seconds = ((current.lastSeen.time - current.interactionStart.time) / 1000)
        var ended = ""
        if (current.isEnded) {
            ended = "\n** Ended - ${SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(current.interactionEnd)}"
        }

      //  val dtxt = "Device# ${position + 1} : ${current.deviceId}\n" +
        val dtxt = "Device# ${position + 1} :  ${String(current.bytes)} \n" +
                "Started: ${SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(current.interactionStart)}\n" +
                "Last Seen: ${SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(current.lastSeen)} ${ended}\n" +
                "Dist: ${current.distanceHistory}\n" +
                "Avg/Median: ${"%.2f ft.".format(current.distanceInFeet)},   " +
                "Duration: ${(seconds / 60).toString().padStart(2, '0')}:${(seconds % 60).toString().padStart(2, '0')} minutes"

        holder.debugInfo.text = dtxt
    }

    internal fun setInteractions(intrc: List<Interaction>) {
        this.interactionsList = intrc
        notifyDataSetChanged()
    }

    override fun getItemCount() = interactionsList.size
}