package kr.mk2.gd;

import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;

public class BitbucketManager extends RepoManager {

	public BitbucketManager(HostInfo hostInfo) {
		super(hostInfo);
	}

	@Override
	protected String getUrl(OwnerInfo info) {
		StringBuilder url = new StringBuilder(
				"https://bitbucket.org/api/2.0/repositories/");
		url.append(info.getName());
		return url.toString();
	}

	@Override
	protected List<RepoInfo> parseResult(JsonNode node) {
		LinkedList<RepoInfo> result = new LinkedList<RepoInfo>();
		JsonNode values = node.get("values");
		for (int i = 0, c = values.size(); i < c; i++) {
			JsonNode item = values.get(i);
			String scm = item.get("scm").asText();
			if ("git".equalsIgnoreCase(scm) == false)
				continue;
			JsonNode links = item.get("links");
			JsonNode clone = links.get("clone");
			String name = item.get("name").asText(); 
			for (int j = 0, x = clone.size(); j < x; j++) {
				JsonNode sItem = clone.get(j);
				String typeName = sItem.get("name").asText();
				if ("ssh".equalsIgnoreCase(typeName) == true)
					continue;
				String href = sItem.get("href").asText();
				RepoInfo repo = new RepoInfo(name,href);
				result.add(repo);
			}
		}

		return result;
	}

}
