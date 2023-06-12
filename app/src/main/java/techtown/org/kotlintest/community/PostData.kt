package techtown.org.kotlintest.community

data class PostData(
    var Uid: String,
    var postKey: String,
    var userName: String,
    var userId : String,
    var postContext : String,
    var profileUri: String,
    var postTime: String,
    var cntHeart: Int,
    var cntComment: Int,
    var cntBookmark: Int,
    var postImg: ArrayList<String>,
){
    constructor(): this("", "","","","", "", "", 0, 0, 0, arrayListOf())
}
