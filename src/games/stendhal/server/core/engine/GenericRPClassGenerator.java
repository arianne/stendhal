package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.annotations.AttributeName;
import games.stendhal.server.core.engine.annotations.RPClassAnnotation;
import games.stendhal.server.core.engine.annotations.RPSlotAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

/**
 * Generates RPClasses for all Entities by the use of {@link Annotation}s.
 * 
 * @author madmetzger
 */
public class GenericRPClassGenerator {
	
	private RPClass generatedRPClass;
	
	public void generate(final Class<?> clazz) {
		if(clazz.isAnnotationPresent(RPClassAnnotation.class)) {
			this.generatedRPClass = new RPClass(clazz.getAnnotation(RPClassAnnotation.class).rpclassname());
			final String isa = clazz.getAnnotation(RPClassAnnotation.class).isa();
			this.generatedRPClass.isA(isa );
			this.handleAttributes(clazz);
		}
	}

	private void handleAttributes(final Class<?> clazz) {
		final Field[] fields = clazz.getDeclaredFields();
		if((fields != null) && (fields.length > 0)) {
			for (int i = 0; i < fields.length; i++) {
			   final Field current = fields[i];
			   handleField(current);
			}
		}
	}

	private void handleField(final Field current) {
		final String name = determineNameForAttribute(current);
		final Class<?> fieldType = current.getType();
		if(fieldType.equals("".getClass())) {
			this.generatedRPClass.addAttribute(name,Type.STRING);
		}
		if(fieldType.equals(int.class)) {
			this.generatedRPClass.addAttribute(name,Type.INT);
		}
		if(current.isAnnotationPresent(RPSlotAnnotation.class)) {
			int capacity = current.getAnnotation(RPSlotAnnotation.class).capacity();
			this.generatedRPClass.addRPSlot(name,capacity );
		}
	}

	private String determineNameForAttribute(final Field field) {
		if(field.isAnnotationPresent(AttributeName.class)) {
			return field.getAnnotation(AttributeName.class).name();
		}
		return field.getName();
	}
	
	

}
