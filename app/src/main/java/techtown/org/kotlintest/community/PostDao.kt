package techtown.org.kotlintest.community

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class PostDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("post")
    }

    //등록
    fun add(post: PostData?): Task<Void> {
        return databaseReference!!.push().setValue(post)
    }

    //조회
    fun getPostList(): Query?{
        return databaseReference
    }

    //수정
    fun postUpdate(key: String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference!!.child(key)!!.updateChildren(hashMap)
    }

    //삭제
    fun postDelete(key: String): Task<Void> {
        return databaseReference!!.child(key).removeValue()
    }
}