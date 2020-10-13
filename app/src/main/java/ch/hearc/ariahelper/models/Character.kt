package ch.hearc.ariahelper.models

data class Character(val name: String, val level: Int,
                     val itemList : ArrayList<Item>,
                     val skillList : ArrayList<Skill>) {
}