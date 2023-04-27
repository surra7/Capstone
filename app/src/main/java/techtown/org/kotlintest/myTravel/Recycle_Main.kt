package techtown.org.kotlintest.myTravel

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import techtown.org.kotlintest.*
import techtown.org.kotlintest.databinding.ActivityRecycleMainBinding

class Recycle_Main : AppCompatActivity() //BottomNavigationView.OnNavigationItemSelectedListener
{

    var datas: MutableList<String>? = null

    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var binding : ActivityRecycleMainBinding

    class MyFragmentPagerAdapter(activity: FragmentActivity): FragmentStateAdapter(activity){
        val fragments: List<Fragment>
        init {
            fragments = listOf(OneFragment(), TwoFragment(), ThreeFragment())
        }

        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRecycleMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        //툴바에 타이틀 없애기
        supportActionBar?.setDisplayShowTitleEnabled(false)
        /*toggle = ActionBarDrawerToggle(this, binding.drawer, R.string.drawer_opened,
            R.string.drawer_closed
        )*/
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //toggle.syncState()

        //뷰 페이저 어댑터
        val adapters = MyFragmentPagerAdapter(this)
        binding.viewpager.adapter = adapters

        TabLayoutMediator(binding.tabs, binding.viewpager){ tab, position ->
            if (position == 0) {
                tab.text = "My Travel Schedule"
            }
            if (position == 1) {
                tab.text = "Supplies"
            }
            if (position == 2) {
                tab.text = "To Do"
            }
        }.attach()

        val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            it.data!!.getStringExtra("result")?.let {
                datas?.add(it)
                adapters.notifyDataSetChanged()
            }
        }

        binding.fab.setOnClickListener{
            val intent = Intent(this, AddActivity::class.java)
            requestLauncher.launch(intent)
        }

        //bundle로 넘어온 datas값이 null이 아니면 목록에 적용
        datas = savedInstanceState?.let {
            it.getStringArrayList("datas")?.toMutableList()
        }?: let { //만약 null이면 목록 초기화
            mutableListOf<String>()
        }

        /*val layoutManager = LinearLayoutManager(this)
        binding.add.layoutManager = RecyclerView.LayoutManager
        var adapter= MyAdapter(datas)
        binding.recycleContent.adapter=adapter
        binding.drawer.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        )*/

        //binding.bottomNavigation.setOnItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.recycler_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //토글 버튼이 눌렀을 때 일어나는 이벤트
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }*/

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("datas", ArrayList(datas))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = intent
                setResult(Activity.RESULT_OK, intent)
                finish()
                return true
            }
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.recycler_menu, menu)

        //사용자가 검색한 내용을 받기
        val menuItem = menu?.findItem(R.id.menu_search)
        val searchView = menuItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                //사용자가 검색창에 글자를 입력할 때마다 동작하는 함수
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d("kkang", "search text: $query")
                return true
            }
        })
        return true
    }*/

}