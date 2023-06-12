package techtown.org.kotlintest.community

data class CommentData(
    var Uid: String, //코멘트 쓴사람 myUid
    var key: String,
    var postKey: String,
    var userName: String,
    var comment : String,
){
    constructor(): this("", "", "","","")
}
