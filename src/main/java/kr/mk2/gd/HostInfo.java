package kr.mk2.gd;

import java.util.Arrays;

public class HostInfo {
	private ServiceType hostType;
	
	@Override
	public String toString() {
		return "HostInfo [hostType=" + hostType + ", user=" + user + ", owners=" + Arrays.toString(owners) + ", ignore=" + Arrays.toString(ignore) + ", to=" + to + "]";
	}
	private String user;
	private String passwd;
	
	private OwnerInfo[] owners;
	private String[] ignore;
	private String to;
	public ServiceType getHostType() {
		return hostType;
	}
	public String getUser() {
		return user;
	}
	public String getPasswd() {
		return passwd;
	}
	public OwnerInfo[] getOwners() {
		return owners;
	}
	public String[] getIgnore() {
		return ignore;
	}
	public String getTo() {
		return to;
	}


}
