package games.stendhal.bot.postman;

import java.util.LinkedList;
import java.util.List;

/**
 * a CIA message
 *
 * @author hendrik
 */
public class CiaMessage {
	private String author;
	private String branch;
	private String project;
	private String revision;
	private String module;
	private final List<String> files = new LinkedList<String>();
	private String message;

	/**
	 * @return the author
	 */
	protected String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	protected void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the branch
	 */
	protected String getBranch() {
		return branch;
	}

	/**
	 * @param branch the branch to set
	 */
	protected void setBranch(String branch) {
		this.branch = branch;
	}

	/**
	 * @return the revision
	 */
	protected String getRevision() {
		return revision;
	}

	/**
	 * @param revision the revision to set
	 */
	protected void setRevision(String revision) {
		this.revision = revision;
	}

	/**
	 * @return the module
	 */
	protected String getModule() {
		return module;
	}

	/**
	 * @param module the module to set
	 */
	protected void setModule(String module) {
		this.module = module;
	}

	/**
	 * @return the project
	 */
	protected String getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	protected void setProject(String project) {
		this.project = project;
	}


	/**
	 * @return the files
	 */
	protected String getFiles() {
		// TODO: format nicely
		return files.toString();
	}

	/**
	 * @param file to add
	 */
	protected void addFile(String file) {
		this.files.add(file);
	}

	/**
	 * @return the message
	 */
	protected String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	protected void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((branch == null) ? 0 : branch.hashCode());
		result = prime * result + ((files == null) ? 0 : files.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((module == null) ? 0 : module.hashCode());
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		result = prime * result + ((revision == null) ? 0 : revision.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CiaMessage other = (CiaMessage) obj;
		if (author == null) {
			if (other.author != null) {
				return false;
			}
		} else if (!author.equals(other.author)) {
			return false;
		}
		if (branch == null) {
			if (other.branch != null) {
				return false;
			}
		} else if (!branch.equals(other.branch)) {
			return false;
		}
		if (files == null) {
			if (other.files != null) {
				return false;
			}
		} else if (!files.equals(other.files)) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (module == null) {
			if (other.module != null) {
				return false;
			}
		} else if (!module.equals(other.module)) {
			return false;
		}
		if (project == null) {
			if (other.project != null) {
				return false;
			}
		} else if (!project.equals(other.project)) {
			return false;
		}
		if (revision == null) {
			if (other.revision != null) {
				return false;
			}
		} else if (!revision.equals(other.revision)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CiaMessage [author=" + author + ", branch=" + branch + ", revision=" + revision + ", module="
				+ module + ", project=" + project + ", files=" + files + ", message=" + message + "]";
	}

}
