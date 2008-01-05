package games.stendhal.server.entity.npc.parser;

import java.io.PrintWriter;

/**
 * Word list entry, used to categorize words. Nouns and verbs can be associated
 * with their plural form.
 * 
 * @author Martin Fuchs
 */
public class WordEntry {
	/** normalized word. */
	private String normalized;

	/** pluralized word (or singular for entries of type ...-PLU .*/
	private String plurSing;

	/** Expression type, e.g. VER, ADJ, OBJ, OBJ-FOO, SUB, SUB-ANI, ... */
	private ExpressionType type;

	/** numeric value for words of type NUM. */
	private Integer value;

	/** database ID. */
	private int id;

	public void print(PrintWriter pw, String key) {
		pw.printf("%s\t", key);

		if (type != null) {
			pw.print(type);
		}

		if (!normalized.equals(key)) {
			pw.printf("\t=%s", normalized);
		}

		if (value != null) {
			pw.printf("\t%d", value);
		}

		if (plurSing != null) {
			pw.printf("\t%s", plurSing);
		}
	}

	protected void setNormalized(String normalized) {
		this.normalized = normalized;
	}

	public String getNormalized() {
		return normalized;
	}

	protected void setType(ExpressionType type) {
		this.type = type;
	}

	public ExpressionType getType() {
		return type;
	}

	public String getTypeString() {
		return type != null ? type.getTypeString() : "";
	}

	public String getNormalizedWithTypeString() {
		return normalized + "/" + getTypeString();
    }

	protected void setPlurSing(String plurSing) {
		this.plurSing = plurSing;
	}

	public String getPlurSing() {
		return plurSing;
	}

	public boolean isPlural() {
	    return type != null && type.isPlural();
    }

	protected void setValue(Integer value) {
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setId(int id) {
	    this.id = id;	    
    }

	public int getId() {
	    return id;	    
    }

}
