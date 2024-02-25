import * as admin from 'firebase-admin';
import { changeCharacterAvatar } from './functions/changeCharacterAvatar';
import { removeCharacterAvatar } from './functions/removeCharacterAvatar';
import { duplicateCharacter } from './functions/duplicateCharacter';

admin.initializeApp();

export {
    changeCharacterAvatar,
    removeCharacterAvatar,
    duplicateCharacter,
}

