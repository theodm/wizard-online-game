package de.theodm.pwf.user

data class User(
    val userPublicID: String,
    val userKey: String,
    val userName: UserName
)

data class UserName(
    private val origin: String
) {
    init {
        require(origin.trim().length > 2) { "Der Benutzername '$origin' muss mindestens 2 Zeichen lang sein." }
    }

    override fun toString() = origin

}
