package cz.frantisekmasa.wfrp_master.common.firebase.firestore

class SetOptions private constructor(
    internal val merge: Boolean,
    internal val fieldsMask: List<String>?,
) {
    companion object {
        private val MERGE = SetOptions(merge = true, fieldsMask = null)

        fun mergeFields(fields: Iterable<String>): SetOptions {
            return SetOptions(merge = true, fieldsMask = fields.toList())
        }

        fun merge() = MERGE
    }
}