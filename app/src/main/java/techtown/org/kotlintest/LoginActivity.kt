package techtown.org.kotlintest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityLogInBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signinButton.setOnClickListener(({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }))

        binding.signupButton.setOnClickListener(({
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }))
    }
}