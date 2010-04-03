package games.stendhal.tools.newrp;

/*
 * Experiment to define skills.
 * It is for sure not the definitive way to go.
 */
enum SkillType {
	SWORDING,
	LIGHT_ARMOR,
	ILLUSION,
	ALCHEMY,
	SHIELDING,
}

/**
 * A skill is composed of a type and the experience in that type of skill.
 *
 * @author miguel
 *
 */
public class Skill {
	SkillType type;
	int level;
	int xp;
}
