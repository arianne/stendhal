package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.annotations.AttributeName;
import games.stendhal.server.core.engine.annotations.RPClassAnnotation;
import games.stendhal.server.core.engine.annotations.RPSlotAnnotation;

import java.util.List;

@RPClassAnnotation(rpclassname="EntityGenerationTestEntity",isa="entity")
public class EntityGenerationTestEntity {
	
	private String name;
	
	@AttributeName(name="changedname")
	private int anotherAttribute;
	
	@RPSlotAnnotation
	private List slot;
	
	@RPSlotAnnotation(capacity=2)
	private List slotTwo;

}
