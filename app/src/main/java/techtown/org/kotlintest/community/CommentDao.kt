package techtown.org.kotlintest.community

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class CommentDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("comment")
    }

    //등록
    fun add(key: String, comment: CommentData): Task<Void> {
        return databaseReference!!.child(key).push().setValue(comment)
    }

    //조회
    fun getCommentList(key: String): Query?{
        return databaseReference!!.child(key)
    }

    //수정
    fun commentUpdate(key: String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference!!.child(key)!!.updateChildren(hashMap)
    }

    //삭제
    fun commentDelete(postKey: String, key: String): Task<Void> {
        return databaseReference!!.child(postKey).child(key).removeValue()
    }

    fun commentAllDelete(postKey: String): Task<Void> {
        return databaseReference!!.child(postKey).removeValue()
    }
}