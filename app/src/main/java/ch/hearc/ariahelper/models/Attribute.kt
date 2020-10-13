package ch.hearc.ariahelper.models

/**
 * An attribute is a simple name->value class to stock attributes such as strenght, charisma, etc...
 */
data class Attribute(val name : String, val value : Int=0) {
}