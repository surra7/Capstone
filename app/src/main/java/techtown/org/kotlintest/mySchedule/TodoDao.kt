package techtown.org.kotlintest.mySchedule

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import techtown.org.kotlintest.myTravel.TravelData

class TodoDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("todo")
    }

    //등록
    fun add(todo: TodoData?): Task<Void> {
        return databaseReference!!.push().setValue(todo)
    }

    fun add2(uid: String, key: String, todo: TodoData?): Task<Void> {
        return databaseReference!!.child(uid).child(key).push().setValue(todo)
    }

    //조회
    fun getTodoList(uid: String, key: String): Query?{
        return databaseReference!!.child(uid).child(key)
    }

    //수정
    fun todoUpdate(uid: String, key: String, sKey: String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference!!.child(uid).child(key).child(sKey)!!.updateChildren(hashMap)
    }

    //삭제
    fun todoDelete(uid: String, key: String, sKey: String): Task<Void> {
        return databaseReference!!.child(uid).child(key).child(sKey).removeValue()
    }

    fun todoAllDelete(uid: String, key: String): Task<Void> {
        return databaseReference!!.child(uid).child(key).removeValue()
    }
}