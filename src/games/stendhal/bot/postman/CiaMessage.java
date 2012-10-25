package games.stendhal.bot.postman;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
		int prefixSize = calculateCommonPrefix(files);
		if (prefixSize <= 0) {
			return "";
		}

		String prefix = files.get(0).substring(0, prefixSize);

		StringBuilder res = new StringBuilder();
		res.append(prefix);

		List<String> endings = new LinkedList<String>();
		for (String file : files) {
			String temp = file.substring(prefixSize);
			if (!temp.isEmpty()) {
				endings.add(temp);
			}
		}
		if (!endings.isEmpty()) {
			String endingsStr = endings.toString();
			endingsStr = endingsStr.substring(1, endingsStr.length() - 2);
			if (endingsStr.length() > 60) {
				endingsStr = countDirsAndFiles(endings);
			}
			res.append(" (");
			res.append(endingsStr);
			res.append(")");
		}
		return res.toString();
	}

	/**
	 * calculates the size of the common prefix
	 *
	 * @param list list
	 * @return size of the common prefix
	 */
	int calculateCommonPrefix(List<String> list) {
		if (list.isEmpty()) {
			return 0;
		}
		String org = list.get(0);
		int max = org.length();
		for (String str : list) {
			for (int i = 0; i < max; i++) {
				if ((str.length() < i + 1) || (org.charAt(i) != str.charAt(i))) {
					max = i;
					break;
				}
			}
		}
		return max;
	}

	/**
	 * counts the numbers of directories and files
	 *
	 * @param endings file list
	 * @return summary
	 */
	private String countDirsAndFiles(List<String> endings) {
		Set<String> dirs = new HashSet<String>();
		for (String file : endings) {
			if (file.lastIndexOf("/") > -1) {
				dirs.add(file.substring(0, file.lastIndexOf("/")));
			}
		}
		String res = endings.size() + " files";
		if (dirs.size() > 1) {
			res = res + " in " + dirs.size() + " dirs";
		}
		return res;
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
