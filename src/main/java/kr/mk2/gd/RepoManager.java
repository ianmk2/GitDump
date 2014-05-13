package kr.mk2.gd;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.CanceledException;
import org.eclipse.jgit.api.errors.DetachedHeadException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidConfigurationException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public abstract class RepoManager {
	HostInfo hostInfo;
	UsernamePasswordCredentialsProvider userInfo;

	public RepoManager(HostInfo hostInfo) {
		this.hostInfo = hostInfo;
	}

	public void start() throws Exception {

		for(OwnerInfo owner : hostInfo.getOwners()) {
			List<RepoInfo> repoInfos = getRepoInfos(owner);
			userInfo = new UsernamePasswordCredentialsProvider(hostInfo.getUser(), hostInfo.getPasswd());
			for(RepoInfo repo : repoInfos) {
//				System.out.println(repo);
//				if(true)
//					continue;
				try {
					File to = new File(hostInfo.getTo(), owner.getName());
					if(to.exists() == false) {
						to.mkdirs();
					}
					File repoDir = getTargetDir(to, repo);
					if(repoDir.exists()) {
						Git localGitRepo = Git.open(repoDir);
						System.out.println(String.format("[Pull] %s", repo.name));
						pullRepo(localGitRepo);
					} else {
						System.out.println(String.format("[Clone] %s from %s to %s", repo.name, repo.href, repoDir.getAbsoluteFile()));
						cloneRepo(repo, repoDir);
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void cloneRepo(RepoInfo info, File repoDir) throws InvalidRemoteException, TransportException, GitAPIException {
		Git.cloneRepository().setURI(info.href).setDirectory(repoDir).setCredentialsProvider(userInfo).call();
	}

	protected void pullRepo(Git git) throws WrongRepositoryStateException, InvalidConfigurationException, DetachedHeadException, InvalidRemoteException, CanceledException, RefNotFoundException,
			NoHeadException, TransportException, GitAPIException {
		git.pull().setCredentialsProvider(userInfo).call();

	}

	protected List<RepoInfo> getRepoInfos(OwnerInfo info) throws Exception {
		LinkedList<RepoInfo> totalReuslt = new LinkedList<RepoInfo>();
		ObjectMapper mapper = new ObjectMapper();
		int page = 0;
		while(true) {
			String result = downloadData(info, ++page);
			JsonNode node = mapper.readTree(result);
			List<RepoInfo> parseResult = parseResult(node);
			totalReuslt.addAll(parseResult);
			if(node.has("next") == false) {
				return totalReuslt;
			}
		}

	}

	protected abstract String getUrl(OwnerInfo info);

	protected File getTargetDir(File base, RepoInfo repo) {
		return new File(base, repo.extractRepoName());
	}

	protected String downloadData(OwnerInfo info, int page) throws Exception {
		InputStream is = null;
		try {
			URL url = new URL(getUrl(info) + "?page=" + page);

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			String userCredentials = hostInfo.getUser() + ":" + hostInfo.getPasswd();
			String basicAuth = "Basic " + new String(new Base64().encode(userCredentials.getBytes()));
			con.setRequestProperty("Authorization", basicAuth);
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			BufferedReader in = null;
			is = con.getInputStream();
			in = new BufferedReader(new InputStreamReader(is));
			String line = null;
			StringBuffer buff = new StringBuffer();
			while((line = in.readLine()) != null) {
				buff.append(line);
			}
			// System.out.println(buff.toString());
			return buff.toString();
		} finally {
			if(is != null)
				is.close();
		}
	}

	protected abstract List<RepoInfo> parseResult(JsonNode node);
}
