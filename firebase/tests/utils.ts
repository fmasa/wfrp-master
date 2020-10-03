import {uuid} from "uuidv4";
import * as firebase from "@firebase/testing";

export function withoutField<T>(object: T, field: string) : Partial<T> {
    return Object.fromEntries(Object.entries(object).filter(([fieldName]) => fieldName !== field)) as Partial<T>;
}

const validPartyGameMasterId = "user";

const validParty = () => ({
    id: uuid(),
    name: "Emperor's Fury",
    accessCode: "123456",
    gameMasterId: validPartyGameMasterId,
    ambitions: {
        shortTerm: 'Kill monsters!',
        longTerm: 'Buy a farm',
    },
    users: [validPartyGameMasterId],
});

function validCharacter (userId: string) {
    return {
        name: "Sigmar",
        userId: userId,
        career: "God",
        socialClass: "Warrior",
        race: "HUMAN",
        stats: {
            weaponSkill: 35,
            dexterity: 20,
            initiative: 10,
            ballisticSkill: 35,
            strength: 10,
            toughness: 10,
            agility: 10,
            intelligence: 10,
            willPower: 10,
            fellowship: 10,
        },
        maxStats: {
            weaponSkill: 35,
            dexterity: 20,
            initiative: 10,
            ballisticSkill: 35,
            strength: 10,
            toughness: 10,
            agility: 10,
            intelligence: 10,
            willPower: 10,
            fellowship: 10,
        },
        points: {
            corruption: 2,
            experience: 100,
            fate: 3,
            fortune: 2,
            wounds: 10,
            maxWounds: 10,
            resilience: 10,
            resolve: 5,
            sin: 4,
        },
        ambitions: {
            shortTerm: 'Kill monsters!',
            longTerm: 'Buy a farm',
        },
        psychology: "Hates greenskins",
        motivation: "Faith in humanity",
        mutation: "Three legs!",
        money: {
            pennies: 1000,
        }
    }
}

type Character = ReturnType<typeof validCharacter>;
type Party = ReturnType<typeof validParty>;

async function createValidParty(): Promise<Party> {
    const party = validParty();

    await this.authedApp(party.gameMasterId)
        .collection("parties")
        .doc(party.id)
        .set(party);

    return party
}

async function setUserInvitation(userId: string, partyId: string, accessCode: string): Promise<void> {
    await this.authedApp(userId)
        .collection("users")
        .doc(userId)
        .set({"invitations": [{partyId, accessCode}]})
}


/**
 * Returns Party
 */
async function createUserAccessibleParty(userId: string): Promise<Party> {
    const party = await createValidParty();

    await setUserInvitation(userId, party.id, party.accessCode);

    const updatedParty = {...party, users: [...party.users, userId]};

    await this.authedApp(userId)
        .collection("parties")
        .doc(party.id)
        .set(updatedParty);

    return updatedParty;
}

async function joinParty(party: Party, userId: string): Promise<void> {
    const app = this.authedApp(userId);

    await setUserInvitation(userId, party.id, party.accessCode);

    await app.collection("parties")
        .doc(party.id)
        .set({users: firebase.firestore.FieldValue.arrayUnion(userId)}, {merge: true});
}

async function createCharacter(partyId: string, userId: string): Promise<Character> {
    const document = this.authedApp(userId)
        .collection("parties")
        .doc(partyId)
        .collection("characters")
        .doc(userId);

    const character = validCharacter(userId);

    await document.set(character);

    return character;
}
