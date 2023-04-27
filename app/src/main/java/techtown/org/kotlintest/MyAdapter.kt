package techtown.org.kotlintest

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import techtown.org.kotlintest.fragment.MyTravelFragment
import techtown.org.kotlintest.myTravel.OneFragment
import techtown.org.kotlintest.myTravel.Recycle_Main
import techtown.org.kotlintest.myTravel.ScheduleData

class MyAdapter1(private val context: OneFragment) : RecyclerView.Adapter<MyAdapter1.ViewHolder>() {

    var datas = mutableListOf<ScheduleData>()
/*(val datas: MutableList<String>?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){*/

    override fun getItemCount(): Int{
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_travelschedule,parent,false)
        return ViewHolder(view)
    }
            /*RecyclerView.ViewHolder
            = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*val binding=(holder as MyViewHolder).binding
        binding.itemData.text= datas!![position]*/

        holder.bind(datas!![position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = itemView.findViewById(R.id.tv_tl_name)
        private val txtTime: TextView = itemView.findViewById(R.id.tv_tl_time)

        fun bind(item: ScheduleData) {
            txtName.text = item.name
            txtTime.text = item.time

        }
    }
}

class MyAdapter(private val context: MyTravelFragment) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var datas = mutableListOf<ListData>()
/*(val datas: MutableList<String>?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){*/

    override fun getItemCount(): Int{
        return datas?.size ?: 0
    }

    /*interface OnItemClickListener{
        fun onItemClick(v:View, data: ListData, pos : Int)
    }
    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview,parent,false)
        return ViewHolder(view)
    }
    /*RecyclerView.ViewHolder
    = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*val binding=(holder as MyViewHolder).binding
        binding.itemData.text= datas!![position]*/

        holder.bind(datas!![position])
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, Recycle_Main::class.java)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        private val txtName: TextView = itemView.findViewById(R.id.tv_tl_name)
        private val txtPlace: TextView = itemView.findViewById(R.id.tv_tl_place)

        fun bind(item: ListData) {
            txtName.text = item.name
            txtPlace.text = item.place

            /*itemView.setOnClickListener{
                Intent(context, Recycle_Main::class.java.java).apply{}

            }*/

            /*val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }*/
        }

    }
}

class MyDecoration(val context: Context): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val index = parent.getChildAdapterPosition(view) + 1

        outRect.set(10, 10, 10, 10)

        /*if (index % 3 == 0) //left, top, right, bottom
            outRect.set(10, 10, 10, 60)
        else
            outRect.set(10, 10, 10, 0)*/

        view.setBackgroundColor(Color.parseColor("#28A0FF"))
        ViewCompat.setElevation(view, 20.0f)

    }
}
