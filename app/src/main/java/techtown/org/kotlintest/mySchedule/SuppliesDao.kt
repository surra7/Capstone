package techtown.org.kotlintest.mySchedule

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class SuppliesDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("supplies")
    }

    //등록
    fun add(supplies: SuppliesData?): Task<Void> {
        return databaseReference!!.push().setValue(supplies)
    }

    fun add2(uid: String, key: String, supplies: SuppliesData?): Task<Void> {
        return databaseReference!!.child(uid).child(key).push().setValue(supplies)
    }

    //조회
    fun getSuppliesList(uid: String, key: String): Query?{
        return databaseReference!!.child(uid).child(key)
    }

    //수정
    fun suppliesUpdate(uid: String, key: String, sKey: String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference!!.child(uid).child(key).child(sKey)!!.updateChildren(hashMap)
    }

    //삭제
    fun suppliesDelete(uid: String, key: String, sKey: String): Task<Void> {
        return databaseReference!!.child(uid).child(key).child(sKey).removeValue()
    }

    fun suppliesAllDelete(uid: String, key: String): Task<Void> {
        return databaseReference!!.child(uid).child(key).removeValue()
    }
}