package sameer.example.a7minutesworkout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import sameer.example.a7minutesworkout.databinding.ItemHistoryBinding

class HistoryAdapter(private val items: ArrayList<String>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        val llHistoryMain = binding.llHistoryMain
        val position = binding.tvEntryNumber
        val tvDate = binding.tvDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val date:String = items[position]
        holder.position.text = (position+1).toString()
        holder.tvDate.text = date

        if (position % 2 == 0){
            holder.llHistoryMain.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
        }else{
            holder.llHistoryMain.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
