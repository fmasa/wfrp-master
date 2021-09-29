import * as admin from 'firebase-admin';
import { changeCharacterAvatar } from './functions/changeCharacterAvatar';
import { removeCharacterAvatar } from './functions/removeCharacterAvatar';

admin.initializeApp();

export {
    changeCharacterAvatar,
    removeCharacterAvatar,
}

