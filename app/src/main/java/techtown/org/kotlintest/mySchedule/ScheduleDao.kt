package techtown.org.kotlintest.mySchedule

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import techtown.org.kotlintest.mySchedule.ScheduleData
import techtown.org.kotlintest.mySchedule.ScheduleListData

class ScheduleDao {
    private var databaseReference: DatabaseReference? = null

    init {
        val db = FirebaseDatabase.getInstance()
        databaseReference = db.getReference("schedule")
    }

    //등록
    fun add(schedule: ScheduleListData?): Task<Void> {
        return databaseReference!!.push().setValue(schedule)
    }

    fun add2(key: String, schedule: ScheduleData?): Task<Void> {
        return databaseReference!!.child(key).push().setValue(schedule)
    }

    //조회
    fun getScheduleList(key: String): Query?{
        return databaseReference!!.child(key)
    }

    //정렬
    fun sortScheduleList(): Query?{
        return databaseReference!!.child("schedule")
            .orderByChild("time")
    }

    //수정
    fun scheduleUpdate(key: String, skey: String, hashMap: HashMap<String, Any>): Task<Void>{
        return databaseReference!!.child(key).child(skey)!!.updateChildren(hashMap)
    }

    //삭제
    fun scheduleDelete(key: String, sKey: String): Task<Void> {
        return databaseReference!!.child(key).child(sKey).removeValue()
    }
}