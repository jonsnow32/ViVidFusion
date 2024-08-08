package cloud.app.avp.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cloud.app.avp.databinding.ItemClientLoadingBinding

class ClientLoadingAdapter :
    RecyclerView.Adapter<ClientLoadingAdapter.ViewHolder>() {

    override fun getItemCount() = 1

    class ViewHolder(val binding: ItemClientLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ItemClientLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {}
}
