package cz.frantisekmasa.wfrp_master.common.firebase.firestore

class SetOptions private constructor(
    internal val fieldsMask: List<String>?,
) {
    companion object {
        private val MERGE = SetOptions(fieldsMask = null)

        fun mergeFields(fields: Iterable<String>): SetOptions {
            return SetOptions(fieldsMask = fields.toList())
        }
    }
}
