package techtown.org.kotlintest.account

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import techtown.org.kotlintest.community.CommentData

class fileDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("file")
    }

    //등록
    fun add(uid: String, file: fileData): Task<Void> {
        return databaseReference!!.child(uid).push().setValue(file)
    }

    //조회
    fun getFileList(uid: String): Query?{
        return databaseReference!!.child(uid)
    }

    //삭제
    fun filetDelete(uid: String, key: String): Task<Void> {
        return databaseReference!!.child(uid).child(key).removeValue()
    }

    fun commentAllDelete(postKey: String): Task<Void> {
        return databaseReference!!.child(postKey).removeValue()
    }
}