package cz.muni.fi.rpg.ui.characterCreation

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import cz.muni.fi.rpg.model.domain.character.Character

abstract class CharacterFormStep<T : Any>(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId) {

    /**
     * Sets default values from existing character
     */
    abstract fun setCharacterData(character: Character)

    /**
     * Returns T if step is correctly filled, otherwise NULL is returned
     */
    abstract fun submit(): T?
}