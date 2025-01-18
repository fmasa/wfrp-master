import {changeCharacterAvatar} from "./functions/changeCharacterAvatar";
import {removeCharacterAvatar} from "./functions/removeCharacterAvatar";
import {duplicateCharacter} from "./functions/duplicateCharacter";
import {initializeApp} from "firebase-admin/app";

initializeApp();

export {
    changeCharacterAvatar,
    removeCharacterAvatar,
    duplicateCharacter,
};

