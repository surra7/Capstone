package techtown.org.kotlintest.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
//import kotlinx.android.synthetic.main.fragment_main.*
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.FragmentMainBinding


class MainFragment : Fragment() {
    private var _binding : FragmentMainBinding? = null
    private val binding get() = _binding!!

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*        binding = FragmentMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.btnNext.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_questionFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}