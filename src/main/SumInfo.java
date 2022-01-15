package main;

import java.util.ArrayList;
import java.util.List;

public class SumInfo {
	String name, id, puuid, accountId, history;
	List<String> matchHistory;
	List<Long> wardTimes;
	
	public SumInfo(String name) {
		matchHistory = new ArrayList<String>();
		wardTimes = new ArrayList<Long>();
		this.name = name;
	}
	
	public SumInfo() {
		this("LSXYZ");
	}
	
	public String toString() {
		return "name: " + name
				+ "\nid: " + id
				+ "\npuuid: " + puuid
				+ "\naccountId: " + accountId
				+ "\nmatch history ids: " + history;
	}
}
