package kr.mk2.gd;

import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

public class GithubManager extends RepoManager {

	public GithubManager(HostInfo hostInfo) {
		super(hostInfo);
	}

	@Override
	protected String getUrl(OwnerInfo info) {
		
		StringBuilder url = new StringBuilder("https://api.github.com/");//orgs/cosmiccolor/repos");
		if(info.type==RepoType.user){
			url.append("users/");
		}else{
			url.append("orgs/");
		}
		url.append(info.getName());
		url.append("/repos");
		return url.toString();
	}

	@Override
	protected List<RepoInfo> parseResult(JsonNode node) {
		LinkedList<RepoInfo> result = new LinkedList<RepoInfo>();
		for(int i = 0, c = node.size(); i < c; i++) {
			JsonNode item = node.get(i);
			String name = item.get("name").asText();
			String href = item.get("clone_url").asText();
			RepoInfo repo = new RepoInfo(name, href);
			result.add(repo);
		}
		return result;
	}

}
