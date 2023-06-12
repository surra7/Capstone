package techtown.org.kotlintest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import techtown.org.kotlintest.community.CommunityFragment
import techtown.org.kotlintest.community.DetailPost
import techtown.org.kotlintest.community.PostData
import techtown.org.kotlintest.databinding.ItemDayListBinding
import techtown.org.kotlintest.databinding.ItemTravelScheduleBinding
import techtown.org.kotlintest.mySchedule.*
import techtown.org.kotlintest.myTravel.*
import java.util.*
import kotlin.collections.ArrayList

class MyAdapter1(private val context: mySchedule, var scheduleList: MutableList<ScheduleData>) : RecyclerView.Adapter<MyAdapter1.ViewHolder>() {

    override fun getItemCount(): Int {
        return scheduleList.size ?: 0
    }

    //화면 설정
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recommend, parent, false)
        return ViewHolder(view)
    }
    /*RecyclerView.ViewHolder
    = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))*/

    //데이터 설정
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val schedule: ScheduleData = scheduleList[position]
        holder.txtPlace.text = schedule.place

        holder.itemView.setOnClickListener{

            //수정화면으로 이동
            val intent = Intent(holder.itemView.context, UpdateActivity::class.java)
            intent.putExtra("uid", schedule.Uid)
            intent.putExtra("key", schedule.travelKey)
            intent.putExtra("sKey", schedule.scheduleKey)
            intent.putExtra("place", schedule.place)
            intent.putExtra("time", schedule.time)
            intent.putExtra("day", schedule.day)
            intent.putExtra("memo", schedule.memo)
            intent.putExtra("location", schedule.location)
            intent.putExtra("latitude", schedule.latitude)
            intent.putExtra("longitude", schedule.longitude)
            intent.putExtra("diffDay", schedule.diffDay)
            /*context.startActivity(intent)
            (context as Activity).finish()*/
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtPlace: TextView = itemView.findViewById(R.id.tv_rc_name)
    }
}

class OutRecyclerViewAdapter(val context: Context, var itemList: MutableList<ScheduleListData>): RecyclerView.Adapter<OutRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemDayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: ItemDayListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScheduleListData) {
            binding.mySchedule = item

            binding.scheduleRecycle.adapter = InRecyclerViewAdapter(context, item.scheduleList)
            binding.scheduleRecycle.layoutManager = LinearLayoutManager(context)
            binding.scheduleRecycle.addItemDecoration(MyDecoration(context))
        }
    }

}

class InRecyclerViewAdapter(context: Context, var itemList: MutableList<ScheduleData>): RecyclerView.Adapter<InRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemTravelScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener{

            //수정화면으로 이동
            val intent = Intent(holder.itemView.context, UpdateActivity::class.java)
            intent.putExtra("uid", item.Uid)
            intent.putExtra("key", item.travelKey)
            intent.putExtra("sKey", item.scheduleKey)
            intent.putExtra("place", item.place)
            intent.putExtra("time", item.time)
            intent.putExtra("day", item.day)
            intent.putExtra("memo", item.memo)
            intent.putExtra("location", item.location)
            intent.putExtra("latitude", item.latitude)
            intent.putExtra("longitude", item.longitude)
            intent.putExtra("diffDay", item.diffDay)
            /*context.startActivity(intent)
            (context as Activity).finish()*/
            ContextCompat.startActivity(holder.itemView.context, intent, null)

        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class Holder(var binding: ItemTravelScheduleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ScheduleData) {
            binding.mySchedule = item
        }
    }

}

class MyPostAdapter(private val context: CommunityFragment) :
    RecyclerView.Adapter<MyPostAdapter.ViewHolder>() {

    var datas = mutableListOf<PostData>()
    var storage = Firebase.storage

/*(val datas: MutableList<String>?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){*/

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val post: PostData = datas[position]
        val id = post.userId
        val profilePic = storage.reference.child("profile").child("photo").child("${id}.png")
        holder.txtName.text = post.userName
        holder.txtId.text = post.userId
        holder.txtContext.text = post.postContext
        holder.txtTime.text = post.postTime
        holder.txtHeart.text = post.cntHeart.toString()
        holder.txtComment.text = post.cntComment.toString()
        holder.txtBookmark.text = post.cntBookmark.toString()

        profilePic.downloadUrl.addOnSuccessListener(){
            Glide.with(context)
                .load(it as Uri)
                .into(holder.imgProfile)
        }
        /*holder.imgProfile = post.postContext*/

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailPost::class.java)
            intent.putExtra("uid", post.Uid)
            intent.putExtra("key", post.postKey)
            intent.putExtra("name", post.userName)
            intent.putExtra("id", post.userId)
            intent.putExtra("context", post.postContext)
            intent.putExtra("uri", post.profileUri)
            intent.putExtra("time", post.postTime)
            intent.putExtra("heart", post.cntHeart)
            intent.putExtra("comment", post.cntComment)
            intent.putExtra("bookmark", post.cntBookmark)
            intent.putExtra("postimg", post.postImg)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val imgProfile: ImageView = itemView.findViewById(R.id.imageProfile)
        val txtName: TextView = itemView.findViewById(R.id.userName)
        val txtId: TextView = itemView.findViewById(R.id.userId)
        val txtContext: TextView = itemView.findViewById(R.id.postContext)
        val txtTime: TextView = itemView.findViewById(R.id.post_time)
        val txtHeart: TextView = itemView.findViewById(R.id.cnt_heart)
        val txtComment: TextView = itemView.findViewById(R.id.cnt_comment)
        val txtBookmark: TextView = itemView.findViewById(R.id.cnt_bookmark)

        /*fun bind(item: TravelData) {
            txtName.text = item.name
            txtsDate.text = item.sDate
            txteDate.text = item.eDate
        }*/

    }
}

class MyAdapter(private val context: MyTravelFragment) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    var datas = mutableListOf<TravelData>()

/*(val datas: MutableList<String>?): RecyclerView.Adapter<RecyclerView.ViewHolder>(){*/

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_travel_list, parent, false)
        return ViewHolder(view)
    }
    /*RecyclerView.ViewHolder
    = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*val binding=(holder as MyViewHolder).binding
        binding.itemData.text= datas!![position]*/

        val travel: TravelData = datas[position]
        holder.txtName.text = travel.name
        holder.txtsDate.text = travel.sDate
        holder.txteDate.text = travel.eDate

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, mySchedule::class.java)
            intent.putExtra("uid", travel.Uid)
            intent.putExtra("key", travel.travelKey)
            intent.putExtra("name", travel.name)
            intent.putExtra("place", travel.place)
            intent.putExtra("sDate", travel.sDate)
            intent.putExtra("eDate", travel.eDate)
            intent.putExtra("diffDay", travel.diffDay)
            intent.putExtra("travelWhom", travel.travelWhom)
            intent.putExtra("travelStyle", travel.travelStyle)
            intent.putExtra("flags", travel.flags)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val txtName: TextView = itemView.findViewById(R.id.tv_tl_name)
        val txtsDate: TextView = itemView.findViewById(R.id.tv_sDate)
        val txteDate: TextView = itemView.findViewById(R.id.tv_eDate)


        /*fun bind(item: TravelData) {
            txtName.text = item.name
            txtsDate.text = item.sDate
            txteDate.text = item.eDate

            *//*itemView.setOnClickListener{
                Intent(context, Recycle_Main::class.java.java).apply{}

            }

            val pos = adapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }*//*
        }*/

    }
}

class MyAdapter2(private val context: Add_Country, var itemList: ArrayList<CountryData>) :
    RecyclerView.Adapter<MyAdapter2.ViewHolder>(), Filterable {

    /*var datas = mutableListOf<CountryData>()*/
    /*var travelDB = Firebase.database.reference.child("travel")*/
    var TAG = "MyAdapter2"
    var filteredCountry = ArrayList<CountryData>()
    var itemFilter = ItemFilter()

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView

        init {
            txtName = itemView.findViewById(R.id.tv_country_name)
        }
    }

    init {
        filteredCountry.addAll(itemList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country: CountryData = filteredCountry[position]
        holder.txtName.text = country.countryName

        val selected = holder.itemView.findViewById<Button>(R.id.select_btn)

        selected.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    override fun getItemCount(): Int {
        return filteredCountry?.size ?: 0
    }

    // (2) 리스너 인터페이스
    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }
    // (3) 외부에서 클릭 시 이벤트 설정
    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }
    // (4) setItemClickListener로 설정한 함수 실행
    private lateinit var itemClickListener : OnItemClickListener

    override fun getFilter(): Filter {
        return itemFilter
    }

    inner class ItemFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val filterString = charSequence.toString()
            val results = FilterResults()
            Log.d(TAG, "charSequence : $charSequence")

            //검색이 필요없을 경우를 위해 원본 배열을 복제
            val filteredList: ArrayList<CountryData> = ArrayList<CountryData>()
            //공백제외 아무런 값이 없을 경우 -> 원본 배열
            if (filterString.trim { it <= ' ' }.isEmpty()) {
                results.values = itemList
                results.count = itemList.size

                return results
            } else {
                for (item in itemList) {
                    if (item.countryName.contains(filterString) ||
                        item.countryName.contains(filterString.replaceFirstChar { //첫글자만 대문자로
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        })) {
                        filteredList.add(item)
                    }
                }
            }
            results.values = filteredList
            results.count = filteredList.size

            return results
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            filteredCountry.clear()
            filteredCountry.addAll(filterResults.values as ArrayList<CountryData>)
            notifyDataSetChanged()
        }
    }

}

class MyTodoAdapter(private val context: TodoList) :
    RecyclerView.Adapter<MyTodoAdapter.ViewHolder>() {

    var datas = mutableListOf<TodoData>()
    var todoDB = Firebase.database.reference.child("todo")

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checklist, parent, false)
        return ViewHolder(view)
    }
    /*RecyclerView.ViewHolder
    = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))*/

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val txtTodo: TextView = itemView.findViewById(R.id.tv_context)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

        fun bind(item: TodoData) {
            txtTodo.text = item.todo
            checkBox.isChecked = item.isChecked
        }
    }

    private fun toBoolean(n: Int) = n != 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*val binding=(holder as MyViewHolder).binding
        binding.itemData.text= datas!![position]*/

        /*val Todo: TodoData = datas[position]*/
        val item = datas[position]
        holder.bind(item)

        /*holder.txtTodo.text = Todo.todo
        holder.checkBox.isChecked = Todo.isChecked*/

        val seqCheckBox = holder.itemView.findViewById<CheckBox>(R.id.checkBox)
        seqCheckBox.setOnCheckedChangeListener(null)
        /*seqCheckBox.isChecked = TodoList.selectCheckBoxPosition.containsValue(position)*/
        seqCheckBox.setOnClickListener {
            /*val seq = seqCheckBox.tag.toString().toInt()*/
            if (seqCheckBox.isChecked) {
                /*TodoList.selectCheckBoxPosition[seq] = position*/
                todoDB.child(item.Uid).child(item.travelKey).child(item.todoKey).child("checked").setValue(true)
                todoDB.child(item.Uid).child(item.travelKey).child(item.todoKey).child("isChecked").setValue(true)
            } else {
                /*TodoList.selectCheckBoxPosition.remove(seq)*/
                todoDB.child(item.Uid).child(item.travelKey).child(item.todoKey).child("checked").setValue(false)
                todoDB.child(item.Uid).child(item.travelKey).child(item.todoKey).child("isChecked").setValue(false)
            }
        }

        if (item.isChecked) {
            holder.txtTodo.paintFlags = holder.txtTodo.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            holder.txtTodo.paintFlags = holder.txtTodo.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTodoActivity::class.java)
            intent.putExtra("uid", item.Uid)
            intent.putExtra("key", item.travelKey)
            intent.putExtra("sKey", item.todoKey)
            intent.putExtra("todo", item.todo)
            intent.putExtra("isChecked", item.isChecked)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }
    /*fun update(newList: MutableList<TodoData>) {
        this.datas = newList
        notifyDataSetChanged()
    }*/

    /*interface ItemCheckBoxClickListener {
        fun onClick(view: View, position: Int, itemId: String)
    }*/

    /*private lateinit var itemCheckBoxClickListener: ItemCheckBoxClickListener

    fun setItemCheckBoxClickListener(itemCheckBoxClickListener: ItemCheckBoxClickListener) {
        this.itemCheckBoxClickListener = itemCheckBoxClickListener
    }*/
}

class MySuppliesAdapter(private val context: SuppliesList) :
    RecyclerView.Adapter<MySuppliesAdapter.ViewHolder>() {

    var datas = mutableListOf<SuppliesData>()
    var suppliesDB = Firebase.database.reference.child("supplies")

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checklist, parent, false)
        return ViewHolder(view)
    }
    /*RecyclerView.ViewHolder
    = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))*/

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val txtSupplies: TextView = itemView.findViewById(R.id.tv_context)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)


        fun bind(item: SuppliesData) {
            txtSupplies.text = item.supplies
            checkBox.isChecked = item.isChecked

            /*checkBox.setOnCheckedChangeListener(object: CompoundButton.OnCheckedChangeListener{
                override fun onCheckedChanged(comButton: CompoundButton?, isCheck: Boolean){
                    if(isCheck){
                        item.isChecked = true
                    }else{
                        item.isChecked = false
                    }
                }
            })*/

            /*if (item.isChecked) {
                txtSupplies.paintFlags = txtSupplies.paintFlags or STRIKE_THRU_TEXT_FLAG
            } else {
                txtSupplies.paintFlags = txtSupplies.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
            }*/

            /*checkBox.setOnClickListener{
                itemCheckBoxClickListener.onClick(it, layoutPosition, datas[layoutPosition].todoKey)
            }*/
        }

    }

    private fun toBoolean(n: Int) = n != 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*val binding=(holder as MyViewHolder).binding
        binding.itemData.text= datas!![position]*/

        /*val Todo: TodoData = datas[position]*/
        val item = datas[position]
        holder.bind(item)

        /*holder.txtTodo.text = Todo.todo
        holder.checkBox.isChecked = Todo.isChecked*/

        val seqCheckBox = holder.itemView.findViewById<CheckBox>(R.id.checkBox)
        seqCheckBox.setOnCheckedChangeListener(null)
        /*seqCheckBox.isChecked = TodoList.selectCheckBoxPosition.containsValue(position)*/
        seqCheckBox.setOnClickListener {
            /*val seq = seqCheckBox.tag.toString().toInt()*/
            if (seqCheckBox.isChecked) {
                /*TodoList.selectCheckBoxPosition[seq] = position*/
                suppliesDB.child(item.Uid).child(item.travelKey).child(item.suppliesKey).child("checked").setValue(true)
                suppliesDB.child(item.Uid).child(item.travelKey).child(item.suppliesKey).child("isChecked").setValue(true)
            } else {
                /*TodoList.selectCheckBoxPosition.remove(seq)*/
                suppliesDB.child(item.Uid).child(item.travelKey).child(item.suppliesKey).child("checked").setValue(false)
                suppliesDB.child(item.Uid).child(item.travelKey).child(item.suppliesKey).child("isChecked").setValue(false)
            }
        }

        if (item.isChecked) {
            holder.txtSupplies.paintFlags = holder.txtSupplies.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            holder.txtSupplies.paintFlags = holder.txtSupplies.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateSuppliesActivity::class.java)
            intent.putExtra("uid", item.Uid)
            intent.putExtra("key", item.travelKey)
            intent.putExtra("sKey", item.suppliesKey)
            intent.putExtra("supplies", item.supplies)
            intent.putExtra("suppliesType", item.suppliesType)
            intent.putExtra("isChecked", item.isChecked)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }
    /* fun update(newList: MutableList<SuppliesData>) {
         this.datas = newList
         notifyDataSetChanged()
     }

     interface ItemCheckBoxClickListener {
         fun onClick(view: View, position: Int, itemId: String)
     }*/

    /*private lateinit var itemCheckBoxClickListener: ItemCheckBoxClickListener

    fun setItemCheckBoxClickListener(itemCheckBoxClickListener: ItemCheckBoxClickListener) {
        this.itemCheckBoxClickListener = itemCheckBoxClickListener
    }*/
}

class GalleryAdapter() : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    lateinit var imageList: ArrayList<Uri>
    lateinit var context: Context

    constructor(imageList: ArrayList<Uri>, context: Context): this(){
        this.imageList = imageList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }
    /*RecyclerView.ViewHolder
    = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(imageList[position])
            .into(holder.imageView)

        /*holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, mySchedule::class.java)
            intent.putExtra("uid", travel.Uid)
            intent.putExtra("key", travel.travelKey)
            intent.putExtra("name", travel.name)
            intent.putExtra("place", travel.place)
            intent.putExtra("sDate", travel.sDate)
            intent.putExtra("eDate", travel.eDate)
            intent.putExtra("diffDay", travel.diffDay)
            intent.putExtra("travelWhom", travel.travelWhom)
            intent.putExtra("travelStyle", travel.travelStyle)
            intent.putExtra("flags", travel.flags)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }*/
    }

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val imageView: ImageView = itemView.findViewById(R.id.img_view)
    }
}

class GalleryAdapter2() : RecyclerView.Adapter<GalleryAdapter2.ViewHolder>() {

    lateinit var imageList: ArrayList<Uri>
    lateinit var context: Context

    constructor(imageList: ArrayList<Uri>, context: Context): this(){
        this.imageList = imageList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }
    /*RecyclerView.ViewHolder
    = MyViewHolder(ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false))*/

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(imageList[position])
            .into(holder.imageView)

        /*holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, mySchedule::class.java)
            intent.putExtra("uid", travel.Uid)
            intent.putExtra("key", travel.travelKey)
            intent.putExtra("name", travel.name)
            intent.putExtra("place", travel.place)
            intent.putExtra("sDate", travel.sDate)
            intent.putExtra("eDate", travel.eDate)
            intent.putExtra("diffDay", travel.diffDay)
            intent.putExtra("travelWhom", travel.travelWhom)
            intent.putExtra("travelStyle", travel.travelStyle)
            intent.putExtra("flags", travel.flags)

            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }*/
    }

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val imageView: ImageView = itemView.findViewById(R.id.img_view)
    }
}

class MyDecoration(val context: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val index = parent.getChildAdapterPosition(view) + 1

        outRect.set(10, 10, 10, 0)

        /*if (index % 3 == 0) //left, top, right, bottom
            outRect.set(10, 10, 10, 60)
        else
            outRect.set(10, 10, 10, 0)*/

        view.setBackgroundColor(Color.parseColor("#28A0FF"))
        //음영 효과
        ViewCompat.setElevation(view, 20.0f)

    }
}

class MyDecoration2(val context: Context) : RecyclerView.ItemDecoration() {

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

        /*view.setBackgroundColor(Color.parseColor("#28A0FF"))*/
        view.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
        //음영 효과
        ViewCompat.setElevation(view, 20.0f)

    }
}

class MyDecoration3(val context: Context) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val index = parent.getChildAdapterPosition(view) + 1

        outRect.set(10, 5, 10, 0)

        /*if (index % 3 == 0) //left, top, right, bottom
            outRect.set(10, 10, 10, 60)
        else
            outRect.set(10, 10, 10, 0)*/

        /*view.setBackgroundColor(Color.parseColor("#28A0FF"))*/
        view.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
        //음영 효과
        ViewCompat.setElevation(view, 10.0f)

    }
}