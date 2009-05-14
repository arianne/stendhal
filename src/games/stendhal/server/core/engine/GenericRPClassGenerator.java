package games.stendhal.server.core.engine;

import games.stendhal.server.core.engine.annotations.RPClassAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import marauroa.common.game.RPClass;

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
			String isa = clazz.getAnnotation(RPClassAnnotation.class).isa();
			this.generatedRPClass.isA(isa );
		}
	}

	private void handleAttributes(final Class<?> clazz) {
		Field[] fields = clazz.getFields();
		if((fields != null) && (fields.length > 0)) {
			for (int i = 0; i < fields.length; i++) {
				
			}
		}
	}
	
	

}
