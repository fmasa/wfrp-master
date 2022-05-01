package cz.frantisekmasa.wfrp_master.common

class FirebaseTokenHolder {
    private var token: String? = null

    fun setToken(token: String) {
        this.token = token
    }

    fun getToken(): String {
        return token ?: error("Token was not set")
    }
}
