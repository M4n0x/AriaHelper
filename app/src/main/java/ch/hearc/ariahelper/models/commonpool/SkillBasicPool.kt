package ch.hearc.ariahelper.models.commonpool

import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.Skill
import java.util.ArrayList

/**
 * Basic pool of skills so that the app is not empty
 */
object SkillBasicPool {
    val SKILLS: MutableList<Skill> = ArrayList()

    init {
        //add basic skills here
    }
}