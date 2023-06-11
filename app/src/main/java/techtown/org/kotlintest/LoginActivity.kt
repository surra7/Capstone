package techtown.org.kotlintest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.mindrot.jbcrypt.BCrypt
import techtown.org.kotlintest.databinding.ActivityAdd2Binding
import techtown.org.kotlintest.databinding.ActivityLogInBinding
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLogInBinding

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        mAuth = Firebase.auth

        //로그인
        binding.signinButton.setOnClickListener{
            val email = binding.emailEdit.text.toString().trim()
            val password = binding.passwordEdit.text.toString().trim()
            login(email, password)
        }

        //회원가입
        binding.signupButton.setOnClickListener{
            val intent: Intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    // 비밀번호 SHA-256 Hashing
    private fun getHash(str: String): String {
        var digest: String = ""
        digest = try {

            //암호화
            val sh = MessageDigest.getInstance("SHA-256") // SHA-256 해시함수를 사용
            sh.update(str.toByteArray()) // str의 문자열을 해싱하여 sh에 저장
            val byteData = sh.digest() // sh 객체의 다이제스트를 얻는다.


            //얻은 결과를 hex string으로 변환
            val hexChars = "0123456789ABCDEF"
            val hex = CharArray(byteData.size * 2)
            for (i in byteData.indices) {
                val v = byteData[i].toInt() and 0xff
                hex[i * 2] = hexChars[v shr 4]
                hex[i * 2 + 1] = hexChars[v and 0xf]
            }

            String(hex) //최종 결과를 String 으로 변환

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            "" //오류 뜰경우 stirng은 blank값임
        }
        return digest
    }

    private fun login(email: String, password: String) {
        //val passwordHashed = BCrypt.hashpw(password, BCrypt.gensalt(10))
        //val isValidPassword = BCrypt.checkpw(password, passwordHashed)
        mAuth.signInWithEmailAndPassword(email, getHash(password))
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // 로그인 성공
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "SignIn SUCCESS", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // 로그인 실패
                    Toast.makeText(this, "SignIn FAIL", Toast.LENGTH_SHORT).show()
                }
            }
    }
}