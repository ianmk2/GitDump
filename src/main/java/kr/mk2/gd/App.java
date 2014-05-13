package kr.mk2.gd;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.JsonLocation;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonStreamContext;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.ObjectCodec;
import org.codehaus.jackson.JsonParser.NumberType;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.jgit.api.Git;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		File configFile = new File("config.json");
		ObjectMapper mapper = new ObjectMapper();

		try {
			List<HostInfo> list = mapper.readValue(configFile, new TypeReference<List<HostInfo>>() {
			});
			System.out.println(list);
			for(HostInfo info : list) {
				RepoManager manager = null;
				ServiceType type = info.getHostType();
				switch (type) {
				case bitbucket:
					manager = new BitbucketManager(info);
					break;
				case github:
					manager = new GithubManager(info);
					break;
				default:
					continue;
				}
				manager.start();
			}

		} catch(JsonParseException e) {
			e.printStackTrace();
		} catch(JsonMappingException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
