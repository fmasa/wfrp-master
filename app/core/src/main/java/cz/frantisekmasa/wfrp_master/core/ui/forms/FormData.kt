package cz.frantisekmasa.wfrp_master.core.ui.forms

@Deprecated("Use HydratedFormData")
interface FormData {
    fun isValid(): Boolean
}

interface HydratedFormData<T> {
    fun isValid(): Boolean

    /**
     * Returns value hydrated from form fields or throws exception.
     *
     * This method works in sync with `isValid`:
     * - If `isValid` returns true, this method SHOULD NOT throw.
     * - If `isValid` returns false, this method SHOULD throw.
     */
    fun toValue(): T
}
