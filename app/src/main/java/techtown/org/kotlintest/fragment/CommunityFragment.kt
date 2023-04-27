package techtown.org.kotlintest.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import techtown.org.kotlintest.*
import techtown.org.kotlintest.databinding.FragmentCommunityBinding

class CommunityFragment: Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCommunityBinding.inflate(inflater, container, false)

        /*val requestLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            it.data!!.getStringExtra("result")?.let {
            }
        }

        binding.addNewPost.setOnClickListener{
            val intent = Intent(context, AddNewPost::class.java)
            requestLauncher.launch(intent)
        }*/

        binding.addNewPost.setOnClickListener(({
            val intent = Intent(context, AddNewPost::class.java)
            startActivity(intent)
        }))

        return binding.root
    }
}