
export interface Party {
    id: string;
    name: string;
    accessCode: string;
    gameMasterId: string;
    archived: boolean;
    ambitions: {
        shortTerm: string;
        longTerm: string;
    };
    time: {
        imperialDay: number;
        minutes: number;
    }
    users: string[];
    activeCombat: Combat | null;
    settings: PartySettings,
}

interface PartySettings {
    initiativeStrategy: "INITIATIVE_CHARACTERISTIC" | "INITIATIVE_TEST" | "INITIATIVE_PLUS_1D10" | "BONUSES_PLUS_1D10"
}

export interface Combat {
    encounterId: string;
    turn: number;
    round: number;
    combatants: (CharacterCombatant | NpcCombatant)[]
}

interface Combatant {
    advantage: number;
    initiative: number;
}

export interface CharacterCombatant extends Combatant {
    characterId: string;
}

export interface NpcCombatant extends Combatant {
    npcId: string;
}

export interface Stats {
    weaponSkill: number;
    dexterity: number;
    initiative: number;
    ballisticSkill: number;
    strength: number;
    toughness: number;
    agility: number;
    intelligence: number;
    willPower: number;
    fellowship: number;
}

export interface Stats {

}

export interface Character {
    id?: string,
    name: string,
    userId: string | null,
    career: string,
    socialClass: string,
    race: string,
    characteristicsAdvances: Stats,
    characteristicsBase: Stats,
    points: {
        corruption: number,
        experience: number,
        fate: number,
        fortune: number,
        wounds: number,
        maxWounds: number,
        resilience: number,
        resolve: number,
        sin: number,
        hardyWoundsBonus: number
    },
    ambitions: {
        shortTerm: string,
        longTerm: string,
    },
    psychology: string,
    motivation: string,
    mutation: string,
    money: {
        pennies: number,
    },
    hardyTalent: boolean,
    note: string,
    conditions: Conditions,
}

export interface Conditions {
    conditions: { [condition: string]: number},
}

export interface Armor {
    head: number;
    body: number;
    leftArm: number;
    rightArm: number;
    leftLeg: number;
    rightLeg: number;
    shield: number;
}
