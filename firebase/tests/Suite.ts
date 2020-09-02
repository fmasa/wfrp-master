import * as firebase from "@firebase/testing";
import {readFileSync} from "fs";
import {uuid} from "uuidv4";
import {Character, Party} from "../api";

type Firestore = firebase.firestore.Firestore;

export abstract class Suite {
    protected readonly validPartyGameMasterId = "user"
    private static readonly projectId = "firestore-emulator-example";
    private static readonly rules = readFileSync(__dirname + "/../firestore.rules", "utf8");

    static async before() {
        await firebase.loadFirestoreRules({
            projectId: this.projectId,
            rules: this.rules,
        });
    }

    async before() {
        // Clear the database between tests
        await firebase.clearFirestoreData({projectId: Suite.projectId});
    }

    static async after() {
        await Promise.all(firebase.apps().map(app => app.delete()));
    }

    /**
     * Creates a new app with authentication data matching the input.
     *
     * @param {object} uid the object to use for authentication (typically {uid: some-uid})
     * @return {object} the app.
     */
    protected authedApp(uid: string | null): Firestore {
        return firebase
            .initializeTestApp({projectId: Suite.projectId, auth: uid === null ? null : {uid}})
            .firestore();
    }

    protected validParty(): Party {
        return {
            id: uuid(),
            archived: false,
            name: "Emperor's Fury",
            accessCode: "123456",
            gameMasterId: this.validPartyGameMasterId,
            ambitions: {
                shortTerm: 'Kill monsters!',
                longTerm: 'Buy a farm',
            },
            users: ["user"],
            time: {
                imperialDay: 10,
                minutes: 10,
            }
        }
    }

    protected validCharacter(userId: string): Character {
        return {
            name: "Sigmar",
            userId: userId,
            career: "God",
            socialClass: "Warrior",
            race: "HUMAN",
            characteristicsAdvances: {
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
            characteristicsBase: {
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
                hardyWoundsBonus: 0,
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
            },
            note: "",
            hardyTalent: false,
        }
    }

    protected async createValidParty(): Promise<Party> {
        const party = this.validParty();

        await this.authedApp(party.gameMasterId)
            .collection("parties")
            .doc(party.id)
            .set(party);

        return party
    }

    protected async setUserInvitation(userId: string, partyId: string, accessCode: string): Promise<void> {
        await this.authedApp(userId)
            .collection("users")
            .doc(userId)
            .set({"invitations": [{partyId, accessCode}]})
    }


    /**
     * Returns Party
     */
    protected async createUserAccessibleParty(userId: string): Promise<Party> {
        const party = await this.createValidParty();

        await this.setUserInvitation(userId, party.id, party.accessCode);

        const updatedParty = {...party, users: [...party.users, userId]};

        await this.authedApp(userId)
            .collection("parties")
            .doc(party.id)
            .set(updatedParty);

        return updatedParty;
    }

    protected async joinParty(party: Party, userId: string): Promise<void> {
        const app = this.authedApp(userId);

        await this.setUserInvitation(userId, party.id, party.accessCode);

        await app.collection("parties")
            .doc(party.id)
            .set({users: firebase.firestore.FieldValue.arrayUnion(userId)}, {merge: true});
    }

    protected async createCharacter(partyId: string, userId: string): Promise<object> {
        const document = this.authedApp(userId)
            .collection("parties")
            .doc(partyId)
            .collection("characters")
            .doc(userId);

        const character = this.validCharacter(userId);

        await document.set(character);

        return character;
    }
}
