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

    fun add2(key: String, supplies: SuppliesData?): Task<Void> {
        return databaseReference!!.child(key).push().setValue(supplies)
    }

    //조회
    fun getSuppliesList(key: String): Query?{
        return databaseReference!!.child(key)
    }

    //수정
    fun suppliesUpdate(key: String, sKey: String, hashMap: HashMap<String, Any>): Task<Void> {
        return databaseReference!!.child(key).child(sKey)!!.updateChildren(hashMap)
    }

    //삭제
    fun suppliesDelete(key: String, sKey: String): Task<Void> {
        return databaseReference!!.child(key).child(sKey).removeValue()
    }
}