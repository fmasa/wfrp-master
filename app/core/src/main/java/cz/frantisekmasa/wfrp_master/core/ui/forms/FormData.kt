package cz.frantisekmasa.wfrp_master.core.ui.forms

/**
 * If there is a value object/DTO that makes sense as form value, consider using HydratedFormData
 * (optionally with FormDialog) instad.
 */
interface FormData {
    fun isValid(): Boolean
}

interface HydratedFormData<T> : FormData {

    /**
     * Returns value hydrated from form fields or throws exception.
     *
     * This method works in sync with `isValid`:
     * - If `isValid` returns true, this method SHOULD NOT throw.
     * - If `isValid` returns false, this method SHOULD throw.
     */
    fun toValue(): T
}
