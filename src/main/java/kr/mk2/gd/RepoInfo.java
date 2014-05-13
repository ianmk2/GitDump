package kr.mk2.gd;

public class RepoInfo {
	String name;
	String href;
	@Override
	public String toString() {
		return "RepoInfo [name=" + name + ", href=" + href + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	
	protected String extractRepoName() {
		String[] tokens = href.split("/");
		return tokens[tokens.length - 1];
	}
	public RepoInfo(String name, String href) {
		this.name = name;
		this.href = href;
	}
}
