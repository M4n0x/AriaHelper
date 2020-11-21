package ch.hearc.ariahelper.models

data class CharacterIdSpinnerContainer(var name : String, var id : Int) {
    override fun toString(): String {
        return name
    }
}