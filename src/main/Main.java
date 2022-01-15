package main;

import java.util.Scanner;

import main.Credentials;
import no.stelar7.api.r4j.basic.constants.api.regions.LeagueShard;
import no.stelar7.api.r4j.impl.R4J;
import no.stelar7.api.r4j.impl.R4J.LOLAPI;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

@SuppressWarnings("unused")
public class Main {
	public static String apik;
	public static SumInfo ot = new SumInfo(); // original target
	
	public static String region = "NA";
	public static Summoner ots; // original target summoner
	public static R4J r4j;
	public static LOLAPI api;
	public static LeagueShard workingShard;

	public static void main(String[] args) {
		boolean dev = true, live = false;
		
		if(!dev) {
			System.out.print("API key: "); // RGAPI-12048812-3e5f-408a-9380-33f5bc05bd06
			Scanner ui = new Scanner(System.in);
			apik = ui.nextLine();
			
			System.out.print("\nName: ");
			ot.name = ui.nextLine();
			
			System.out.print("\nLive game? [Y/N] ");
			String temp = ui.nextLine();
			live = temp.toLowerCase().contains("y");
			
			ui.close();
		}else {
			apik = "RGAPI-b93b6c2f-75ec-4273-91e6-14f8ba50bc40";
			
			ot.name = "LSXYZ";
			live = false;
		}
		workingShard = LeagueShard.NA1;
		Credentials.lolkey = apik;
		r4j = new R4J(Credentials.getCreds());
		api = r4j.getLoLAPI();
		ots = api.getSummonerAPI()
				.getSummonerByName(workingShard, ot.name);
		
		if(live) Analysis.analyzeLiveGame();
		else Analysis.analyzeMatchHistory(ot);
	}

}