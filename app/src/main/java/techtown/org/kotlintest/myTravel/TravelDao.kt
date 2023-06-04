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
    fun add(travel: TravelData?): Task<Void> {
        return databaseReference!!.push().setValue(travel)
    }

    //조회
    fun getTravelList(): Query?{
        return databaseReference
    }

    //수정
    fun travelUpdate(key: String, hashMap: HashMap<String, Any>): Task<Void>{
        return databaseReference!!.child(key)!!.updateChildren(hashMap)
    }

    //삭제
    fun travelDelete(key: String): Task<Void> {
        return databaseReference!!.child(key).removeValue()
    }
}