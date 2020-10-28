package ch.hearc.ariahelper.models

/**
 * Little, partial data class to stock skills and their value
 * Value c [0;100]
 */
data class Skill(val name : String, val description : String, val value : Int = 50) {
}