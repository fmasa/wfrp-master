package cz.muni.fi.rpg.model

import java.util.*


fun generateAccessCode(): String {
    // TODO: Replace with better strategy
    return UUID.randomUUID().toString();
}