package techtown.org.kotlintest.community

data class PostData(
    var Uid: String,
    var key: String, //포스트 key
    var postKey: String, //storage접근을 위한 임의 key
    var userName: String,
    var userId : String,
    var postContext : String,
    var location: String,
    var postTime: String,
    var cntHeart: Int,
    var cntComment: Int,
    var cntBookmark: Int,
    var postImg: ArrayList<String>,
){
    constructor(): this("", "", "","","", "", "", "", 0, 0, 0, arrayListOf())
}
