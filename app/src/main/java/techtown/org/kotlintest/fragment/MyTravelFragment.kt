package techtown.org.kotlintest.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import techtown.org.kotlintest.*
import techtown.org.kotlintest.databinding.FragmentMyTravelBinding

class MyTravelFragment: Fragment(){
    lateinit var myAdapter: MyAdapter
    val datas = mutableListOf<ListData>()
    private lateinit var binding : FragmentMyTravelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMyTravelBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(activity)
        binding.gridRecycle.layoutManager=layoutManager
        myAdapter = MyAdapter(this)
        binding.gridRecycle.adapter = myAdapter
        binding.gridRecycle.addItemDecoration(MyDecoration(activity as Context))

        datas.apply {
            add(ListData(name = "Osaka|Tokyo", place = "2023.03.24-03.28"))
            add(ListData(name = "Otaru", place = "2023.02.14-02.17"))
            add(ListData(name = "Nha Trang", place="2022.12.26-12.30"))
            add(ListData(name = "Fukuoka", place = "2022.10.05-10.08"))
            add(ListData(name = "Bangkok", place = "2022.06.21-06.25"))
            add(ListData(name = "Tokyo", place = "2022.01.05-01.10"))

            myAdapter.datas = datas
            myAdapter.notifyDataSetChanged()
    }

        /*myAdapter.SetOnItemClickListener(object : MyAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: ListData, pos : Int) {
                Intent(this, Recycle_Main::class.java).apply {
                    putExtra("data", data)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { ContextCompat.startActivity(this) }
            }

        })*/

        /*val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            *//*it.data!!.getStringExtra("result")?.let {
                datas?.add(ListData(it,it))
                myAdapter.notifyDataSetChanged()
            }*//*
        }

        binding.addNewPlan.setOnClickListener{
            val intent = Intent(context, AddActivity2::class.java)
            requestLauncher.launch(intent)
        }*/

        binding.addNewPlan.setOnClickListener(({
            val intent = Intent(context, AddActivity2::class.java)
            startActivity(intent)
        }))

        return binding.root

    /*private fun initRecycler() {
        myAdapter = MyAdapter(this)
        binding.gridRecycle.adapter = myAdapter


        datas.apply {
            add(ListData(name = "mary", age = 24))
            add(ListData(name = "jenny", age = 26))
            add(ListData(name = "jhon", age = 27))
            add(ListData(name = "ruby", age = 21))
            add(ListData(name = "yuna", age = 23))

            myAdapter.datas = datas
            myAdapter.notifyDataSetChanged()

        }*/
    }
}


/*AppCompatActivity() {
    lateinit var myAdapter: MyAdapter
    val datas = mutableListOf<ListData>()
    private lateinit var binding : FragmentGridBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentGridBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        initRecycler()
    }
    private fun initRecycler() {
        myAdapter = MyAdapter(this)
        binding.gridRecycle.adapter = myAdapter


        datas.apply {
            add(ListData(name = "mary", age = 24))
            add(ListData(name = "jenny", age = 26))
            add(ListData(name = "jhon", age = 27))
            add(ListData(name = "ruby", age = 21))
            add(ListData(name = "yuna", age = 23))

            myAdapter.datas = datas
            myAdapter.notifyDataSetChanged()

        }
    }
}*/
/*Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentGridBinding.inflate(inflater, container, false)

        val datas = mutableListOf<String>()
        for (i in 1..15){
            datas.add("Item $i")
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.girdRecycle.layoutManager=layoutManager
        val adapter= MyAdapter(datas)
        binding.girdRecycle.adapter=adapter
        binding.girdRecycle.addItemDecoration(MyDecoration(activity as Context))
        return binding.root
    }
}*/