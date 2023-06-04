package techtown.org.kotlintest.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import techtown.org.kotlintest.R
import techtown.org.kotlintest.databinding.FragmentSelectionBinding

class SelectionFragment : Fragment(), View.OnClickListener {
    private var _binding : FragmentSelectionBinding? = null
    private val binding get() = _binding!!

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSelectionBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.btnBack.setOnClickListener(this)
        binding.option1.setOnClickListener(this)
        binding.option2.setOnClickListener(this)
        binding.option3.setOnClickListener(this)
        binding.option4.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.option_1 -> {navigateWithIndex(1)}
            R.id.option_2 -> {navigateWithIndex(2)}
            R.id.option_3 -> {navigateWithIndex(3)}
            R.id.option_4 -> {navigateWithIndex(4)}
            R.id.btn_back -> {
                navController.popBackStack()
            }
        }
    }

    fun navigateWithIndex(index: Int){
        val bundle = bundleOf("index" to index)
        navController.navigate(R.id.action_selectionFragment_to_resultFragment2, bundle)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}