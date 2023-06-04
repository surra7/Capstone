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

    fun add2(key: String, todo: TodoData?): Task<Void> {
        return databaseReference!!.child(key).push().setValue(todo)
    }

    //조회
    fun getTodoList(key: String): Query?{
        return databaseReference!!.child(key)
    }

    //수정
    fun todoUpdate(key: String, sKey: String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference!!.child(key).child(sKey)!!.updateChildren(hashMap)
    }

    //삭제
    fun todoDelete(key: String, sKey: String): Task<Void> {
        return databaseReference!!.child(key).child(sKey).removeValue()
    }
}