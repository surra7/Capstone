package techtown.org.kotlintest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import techtown.org.kotlintest.account.UserFragment
import techtown.org.kotlintest.databinding.ActivityMainBinding
import techtown.org.kotlintest.community.*
import techtown.org.kotlintest.myTravel.MyTravelFragment

/*
class MainActivity : AppCompatActivity() {
    //private lateinit var binding : ActivityMainBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        var navController = navHostFragment.navController
        //navController = findNavController(R.id.nav_host_fragment)

    }
}
*/

class MainActivity : AppCompatActivity() {

    private val fragmentManager = supportFragmentManager
    private lateinit var binding : ActivityMainBinding

    lateinit var Uid : String

    val bundle = Bundle()

    private var homeFragment: HomeFragment? = null
    private var myTravelFragment: MyTravelFragment? = null
    private var communityFragment: CommunityFragment? = null
    private var userFragment: UserFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (intent.hasExtra("uid")) {
            Uid = intent.getStringExtra("uid")!!
        }

        bundle.putString("uid", Uid)

        initBottomNavigation()
    }

    /*myAdapter.SetOnItemClickListener(object : MyAdapter.OnItemClickListener{
        override fun onItemClick(v: View, data: ListData, pos : Int) {
            Intent(this, Recycle_Main::class.java).apply {
                putExtra("data", data)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { startActivity(this) }
        }

    })*/

    private fun initBottomNavigation(){
        // 최초로 보이는 프래그먼트
        homeFragment = HomeFragment()
        fragmentManager.beginTransaction().replace(R.id.main_content,homeFragment!!).commit()

        binding.bottomNavigation.setOnItemSelectedListener {

            // 최초 선택 시 fragment add, 선택된 프래그먼트 show, 나머지 프래그먼트 hide
            when(it.itemId){
                R.id.action_home ->{
                    if(homeFragment == null){
                        homeFragment = HomeFragment()
                        homeFragment!!.arguments = bundle
                        fragmentManager.beginTransaction().add(R.id.main_content,homeFragment!!).commit()
                    }
                    if(homeFragment != null) fragmentManager.beginTransaction().show(homeFragment!!).commit()
                    if(myTravelFragment != null) fragmentManager.beginTransaction().hide(myTravelFragment!!).commit()
                    if(communityFragment != null) fragmentManager.beginTransaction().hide(communityFragment!!).commit()
                    if(userFragment != null) fragmentManager.beginTransaction().hide(userFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.action_my_travel ->{
                    if(myTravelFragment == null){
                        myTravelFragment = MyTravelFragment()
                        myTravelFragment!!.arguments = bundle
                        fragmentManager.beginTransaction().add(R.id.main_content,myTravelFragment!!).commit()
                    }
                    if(homeFragment != null) fragmentManager.beginTransaction().hide(homeFragment!!).commit()
                    if(myTravelFragment != null) fragmentManager.beginTransaction().show(myTravelFragment!!).commit()
                    if(communityFragment != null) fragmentManager.beginTransaction().hide(communityFragment!!).commit()
                    if(userFragment != null) fragmentManager.beginTransaction().hide(userFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.action_community ->{
                    if(communityFragment == null){
                        communityFragment = CommunityFragment()
                        myTravelFragment!!.arguments = bundle
                        fragmentManager.beginTransaction().add(R.id.main_content,communityFragment!!).commit()
                    }
                    if(homeFragment != null) fragmentManager.beginTransaction().hide(homeFragment!!).commit()
                    if(myTravelFragment != null) fragmentManager.beginTransaction().hide(myTravelFragment!!).commit()
                    if(communityFragment != null) fragmentManager.beginTransaction().show(communityFragment!!).commit()
                    if(userFragment != null) fragmentManager.beginTransaction().hide(userFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.action_account ->{
                    if(userFragment == null){
                        userFragment = UserFragment()
                        userFragment!!.arguments = bundle
                        fragmentManager.beginTransaction().add(R.id.main_content,userFragment!!).commit()
                    }
                    if(homeFragment != null) fragmentManager.beginTransaction().hide(homeFragment!!).commit()
                    if(myTravelFragment != null) fragmentManager.beginTransaction().hide(myTravelFragment!!).commit()
                    if(communityFragment != null) fragmentManager.beginTransaction().hide(communityFragment!!).commit()
                    if(userFragment != null) fragmentManager.beginTransaction().show(userFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                else ->{
                    return@setOnItemSelectedListener true
                }
            }
        }
    }
}


/*
class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener{

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        var homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().add(R.id.main_content, homeFragment).commit()

        binding.bottomNavigation.setOnNavigationItemSelectedListener(this)
    }

    */
/*myAdapter.SetOnItemClickListener(object : MyAdapter.OnItemClickListener{
        override fun onItemClick(v: View, data: ListData, pos : Int) {
            Intent(this, Recycle_Main::class.java).apply {
                putExtra("data", data)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }.run { startActivity(this) }
        }

    })*//*


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.action_home -> {
                var homeFragment = HomeFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,homeFragment).commit()
                return true
            }
            R.id.action_my_travel -> {
                var gridFragment = GridFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,gridFragment).commit()
                return true
            }
            R.id.action_community -> {
                var communityFragment = CommunityFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,communityFragment).commit()
                return true
            }
            R.id.action_account -> {
                var userFragment = UserFragment()
                supportFragmentManager.beginTransaction().replace(R.id.main_content,userFragment).commit()
                return true
            }
        }
        return false
    }
}

*/
