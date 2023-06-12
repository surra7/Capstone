package techtown.org.kotlintest.account

data class OrcData(
    var OCRresult: String?,
    var type: String,
    var countryCode: String,
    var passportNo: String, // 암호화 필요
    var surname: String,
    var givenName: String,
    var nameInKorean: String,
    var dateOfBirth: Int,
    var sex: String,
    var nationality: String,
    var authority: String,
    var dateOfIssue: Int,
    var dateOfExpiry: Int,
){
    constructor(): this("", "","", "", "", "", "", 0, "", "", "", 0, 0)
}
