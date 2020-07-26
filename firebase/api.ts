
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
    users: string[];
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
    name: string,
    userId: string,
    career: string,
    socialClass: string,
    race: string,
    stats: Stats,
    maxStats: Stats,
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
    note: string,
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
