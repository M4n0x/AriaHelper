package ch.hearc.ariahelper.models.commonpool

import ch.hearc.ariahelper.models.Attribute
import ch.hearc.ariahelper.models.Item
import ch.hearc.ariahelper.models.Skill
import java.util.ArrayList

/**
 * Basic pool of skills so that the app is not empty
 */
object SkillBasicPool {
    val SKILLS: MutableList<Skill> =
        mutableListOf<Skill>(
            Skill("Courir, Sauter", "Permet de passer des obstacles"),
            Skill("Mentir / convaincre", "Persuade une tièrce personne d'un fait, réel ou non"),
            Skill("Escalader", "Permet de franchir des parroies abruptes"),
            Skill("Combat corps-à-corps", "Donne un coup rapproché"),
            Skill("Combat à distance", "Lance un projectile"),
            Skill("Intimider", "Force la main à une tièrce personne par la peur"),
            Skill("Parler aux animaux", "Discute sommairement avec un animal"),
            Skill("Lonnnnng skill", "Discute sommairement avec un animal, Discute sommairement avec un animal, Discute sommairement avec un animal"),
            )
}