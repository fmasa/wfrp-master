package cz.frantisekmasa.wfrp_master.combat.domain.initiative

data class InitiativeOrder(private val comparableValues: List<Int>) : Comparable<InitiativeOrder> {
    init {
        require(comparableValues.isNotEmpty()) { "There must be at least one comparable value" }
    }

    constructor(vararg values: Int) : this(values.toList())

    fun toInt(): Int = comparableValues[0]

    override fun compareTo(other: InitiativeOrder): Int {
        require(comparableValues.size == other.comparableValues.size) {
            "Compared initiative order must have same number of comparableValues"
        }

        var diff: Int

        for (i in comparableValues.indices) {
            diff = comparableValues[i].compareTo(other.comparableValues[i])

            if (diff != 0) {
                return diff
            }
        }

        return 0
    }
}


