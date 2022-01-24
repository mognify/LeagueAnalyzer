package main;

import java.util.Scanner;

import main.Credentials;
import no.stelar7.api.r4j.basic.cache.impl.FileSystemCacheProvider;
import no.stelar7.api.r4j.basic.calling.DataCall;
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

	/*
	 * args should be 1 thing and thats the api key
	 */
	public static void main(String[] args) {
		run(args);
	}

	private static void run(String[] args) {
		
		boolean dev = false, live = true;
		
		Scanner ui = new Scanner(System.in);
		
		if(args.length == 1) {
			apik = args[0];
			
			System.out.print("\nName: ");
			ot.name = ui.nextLine();
			
			System.out.print("\nLive game? [Y/N] ");
			String temp = ui.nextLine();
			live = temp.toLowerCase().equals("y");
		}else if(args.length == 2) {
			// apikey, name
			apik = args[0];
			ot.name = args[1];
			
			System.out.print("\nLive game? [Y/N] ");
			String temp = ui.nextLine();
			live = temp.toLowerCase().equals("y");
		}else if(args.length == 3) {
			// apikey, name, live/dev: if "true", live; if "1", dev; else not live not dev
			apik = args[0];
			ot.name = args[1];
			String t = args[2];
			if(t.equals("true")) live = true;
			else if(t.equals("1")) {
				dev = true; live = false;
			}else {
				dev = false; live = false;
			}
		}else if(!dev) {
			//System.out.print("API key: "); // RGAPI-12048812-3e5f-408a-9380-33f5bc05bd06
			System.out.print("Api key: ");
			apik = ui.nextLine();
			
			System.out.print("\nName: ");
			ot.name = ui.nextLine();
			
			System.out.print("\nLive game? [Y/N] ");
			String temp = ui.nextLine();
			live = temp.toLowerCase().equals("y");
			
			if(!live) {
				System.out.print("Theirs (0) or their last opponents' (1)? ");
				temp = ui.nextLine();
				if(temp.equals("1")) dev = true;
			}
		}else {
			apik = "RGAPI-15f38fd6-e58e-48d5-ba40-1e7905095438";
			
			ot.name = "LSXYZ";
			live = false;
		}
		
		ui.close();
		
		DataCall.setCacheProvider(new FileSystemCacheProvider());
		workingShard = LeagueShard.NA1;
		Credentials.lolkey = apik;
		r4j = new R4J(Credentials.getCreds());
		api = r4j.getLoLAPI();
		ots = api.getSummonerAPI()
				.getSummonerByName(workingShard, ot.name);
		
		if(live) Analysis.analyzeLiveGame();
		else if(!dev) {
			Analysis.analyzeMatchHistory(ot);
			System.out.println(ot.wardHistory);
		}
		else Analysis.analyzeLastMatchLikeLiveGame();
		/*
		try {
			Thread.sleep(9999);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		//run(args);
		
		/*
		 * TODO: next steps...
		 * 1. rerun?
		 * 		dont need to. Just press up arrow and enter to run the command with args again
		 * 2. min-max build path recommendation
		 * 		this should probably take at least 10x as long as just the ward windows
		 * 		for mmbpr... needs to take into account how the character scales
		 * 		how the units scale
		 * 		defense/damage types
		 */
	}

}
