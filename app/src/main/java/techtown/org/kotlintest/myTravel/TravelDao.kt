package techtown.org.kotlintest.myTravel

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class TravelDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("travel")
    }

    //등록
    fun add(uid: String, travel: TravelData?): Task<Void> {
        return databaseReference!!.child(uid).push().setValue(travel)
    }

    //조회
    fun getTravelList(uid: String): Query?{
        return databaseReference!!.child(uid)
    }

    //수정
    fun travelUpdate(uid: String, key: String, hashMap: HashMap<String, Any>): Task<Void>{
        return databaseReference!!.child(uid).child(key)!!.updateChildren(hashMap)
    }

    //삭제
    fun travelDelete(uid: String, key: String): Task<Void> {
        return databaseReference!!.child(uid).child(key).removeValue()
    }
}