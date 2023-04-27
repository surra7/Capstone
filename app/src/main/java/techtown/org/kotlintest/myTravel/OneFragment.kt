package techtown.org.kotlintest.myTravel

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import techtown.org.kotlintest.MyAdapter1
import techtown.org.kotlintest.MyDecoration
import techtown.org.kotlintest.databinding.FragmentOneBinding


class OneFragment : Fragment(){
    lateinit var myAdapter: MyAdapter1
    val datas = mutableListOf<ScheduleData>()
    private lateinit var binding : FragmentOneBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentOneBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(activity)
        binding.oneRecycle.layoutManager=layoutManager
        myAdapter = MyAdapter1(this)
        binding.oneRecycle.adapter = myAdapter
        binding.oneRecycle.addItemDecoration(MyDecoration(activity as Context))

        datas.apply {
            add(ScheduleData(name = "Place1", time = "09:00"))
            add(ScheduleData(name = "Place2", time = "09:30"))
            add(ScheduleData(name = "Place3", time = "10:30"))
            add(ScheduleData(name = "Place4", time = "11:30"))
            add(ScheduleData(name = "Place5", time = "13:00"))
            add(ScheduleData(name = "Place6", time = "15:30"))
            add(ScheduleData(name = "Place7", time = "18:00"))

            myAdapter.datas = datas
            myAdapter.notifyDataSetChanged()
        }

        return binding.root
    }

    /*private fun initRecycler() {
        myAdapter = MyAdapter1(this)
        binding.oneRecycle.adapter = myAdapter


        datas.apply {
            add(ListData(name = "Japan", place = "Osaka|Tokyo"))
            add(ListData(name = "With Friend", place = "Otaru"))
            add(ListData(name = "Family Trip", place="Nha Trang"))
            add(ListData(name = "vacation", place = "Fukuoka"))
            add(ListData(name = "Friends", place = "Bangkok"))
            add(ListData(name = "Tokyo", place = "Tokyo"))

            myAdapter.datas = datas
            myAdapter.notifyDataSetChanged()

        }
    }*/
}

/*Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentOneBinding.inflate(inflater, container, false)

        val datas = mutableListOf<String>()
        for (i in 1..15){
            datas.add("Item $i")
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.fragment1.layoutManager=layoutManager
        val adapter= MyAdapter(datas)
        binding.fragment1.adapter=adapter
        binding.fragment1.addItemDecoration(MyDecoration(activity as Context))
        return binding.root
    }

}*/
