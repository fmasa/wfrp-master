import {Suite} from "./Suite";

export abstract class CharacterSubCollectionSuite extends Suite {
    private _partyId: string
    private _gameMasterId: string;
    protected readonly userId1 = 'user123';
    protected readonly userId2 = 'user345';

    protected get partyId(): string {
        return this._partyId;
    }

    protected get gameMasterId(): string {
        return this._gameMasterId;
    }

    async before() {
        await super.before();

        const party = await this.createValidParty();
        this._partyId = party.id;
        this._gameMasterId = party.gameMasterId;

        await this.joinParty(party, this.userId1);
        await this.joinParty(party, this.userId2);

        await this.createCharacter(party.id, this.userId1);
        await this.createCharacter(party.id, this.userId2);
    }
}
