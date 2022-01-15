package main;

import static main.Main.ot;
import static main.Main.workingShard;
import static main.Main.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;

import no.stelar7.api.r4j.basic.cache.impl.FileSystemCacheProvider;
import no.stelar7.api.r4j.basic.calling.DataCall;
import no.stelar7.api.r4j.basic.constants.types.lol.WardType;
import no.stelar7.api.r4j.basic.utils.LazyList;
import no.stelar7.api.r4j.impl.lol.builders.matchv5.match.MatchBuilder;
import no.stelar7.api.r4j.impl.lol.builders.matchv5.match.MatchListBuilder;
import no.stelar7.api.r4j.impl.lol.builders.matchv5.match.TimelineBuilder;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLMatch;
import no.stelar7.api.r4j.pojo.lol.match.v5.LOLTimeline;
import no.stelar7.api.r4j.pojo.lol.match.v5.MatchParticipant;
import no.stelar7.api.r4j.pojo.lol.match.v5.TimelineFrame;
import no.stelar7.api.r4j.pojo.lol.match.v5.TimelineFrameEvent;
import no.stelar7.api.r4j.pojo.lol.match.v5.TimelinePosition;
import no.stelar7.api.r4j.pojo.lol.summoner.Summoner;

public class Analysis {
	public static void analyzeLiveGame() {
		fillSumInfo(ot);
	}

	public static void analyzeMatchHistory(SumInfo target) {
		fillSumInfo(target); // gets all their identifiers
		
		int start = 0, count = 3;
		//fillSumHistory(target, start, count); // fills target.history string
		fillSumHistory(target, start, count);
		if(Objects.isNull(target.matchHistory)
				|| target.matchHistory.size() == 0) return;
		
		DataCall.setCacheProvider(new FileSystemCacheProvider());
		
		for(String matchId : target.matchHistory) {
			analyzePastMatch(matchId, target);
		}
	}
	
	private static void fillSumHistory(SumInfo target, int start, int count) {
		MatchListBuilder mlb = new MatchListBuilder();
		mlb = mlb.withPuuid(target.puuid).withPlatform(
				workingShard);
		target.matchHistory = mlb.withCount(count)
				.get();
	}

	private static void analyzePastMatch(String matchId, SumInfo target) {
		LOLMatch match = LOLMatch.get(workingShard, matchId);
		
		//getWardLocations(match, target);
		getWardTimes(match, target);
	}

	// this just sets up the container for the ward times
	// and then calls to fill that container
	// poor design. The call to fill the container should be separate
	private static void getWardTimes(LOLMatch match, SumInfo target) {
		TimelinePosition[][] wardPlacements =
				new TimelinePosition[4][200];
		// there are 4 types of wards, x index
		// then we need the time the wards were placed, max 200 wards assumed
		
		for(MatchParticipant mp : match.getParticipants()) {
			//System.out.println(mp.getSummonerName());
			if(mp.getSummonerName().equals(target.name)) {
				//System.out.println(mp.getSummonerName());
				wardPlacements =
					new TimelinePosition[4][mp.getWardsPlaced()];
			}
		}
		
		LOLTimeline timeline = match.getTimeline();
		
		List<TimelinePosition[][]> wardTimes = new ArrayList<TimelinePosition[][]>();
		wardTimes.add(getPlayerWardTimes(timeline, wardPlacements));
	}

	private static TimelinePosition[][] getPlayerWardTimes(LOLTimeline timeline, TimelinePosition[][] wardPlacements) {
		int wi = 0; // ward index
		// i should use 0 or 4 for error, but this isnt serious
		
		for(TimelineFrame tf : timeline.getFrames()) {
			//System.out.println("Checking frame " + tf.getTimestamp());
			for(TimelineFrameEvent tfe : tf.getEvents()) {
				WardType wt = tfe.getWardType();
				if(Objects.isNull(wt)) continue;
				System.out.print(wt.prettyName() + ";");
				String pn = wt.prettyName().toLowerCase();

				// 0=Ytrinket 1=Btrinket 2=itemWard 3=control
				int wardTypeIndex = 
						pn.contains("trinket")?
								(pn.contains("yellow")?
										0:1):
								(pn.contains("sight")?
										2:3);
					
				System.out.println("(" + wardTypeIndex + ",  " + (wi+1) + ")");
				wardPlacements[wardTypeIndex][wi++] =
						tfe.getPosition();
				
				try {
					System.out.println("Position: " + tfe.getPosition().getX() + ", " + tfe.getPosition().getY());
				}catch(Exception e) {
					/*System.out.println("\nPosition grab failed.");
					e.printStackTrace();*/
					// RIOT API NO LONGER PROVIDES WARD PLACEMENT LOCATIONS
					continue;
				}
			}
		}
		
		return wardPlacements;
	}

	@SuppressWarnings("unused")
	private static void getWardLocations(LOLMatch match, SumInfo target) {
		TimelinePosition[][] wardPlacements =
				new TimelinePosition[4][200];
		for(MatchParticipant mp : match.getParticipants()) {
			//System.out.println(mp.getSummonerName());
			if(mp.getSummonerName().equals(target.name)) {
				//System.out.println(mp.getSummonerName());
				wardPlacements =
					new TimelinePosition[4][mp.getWardsPlaced()];
			}
		}
		
		LOLTimeline timeline = match.getTimeline();
		
		List<TimelinePosition[][]> wardSpots = new ArrayList<TimelinePosition[][]>();
		wardSpots.add(getPlayerWardSpots(timeline, wardPlacements));
		
		Iterator<TimelinePosition[][]> wsi = wardSpots.iterator();
		while(wsi.hasNext()) {
			TimelinePosition[][] tp = wsi.next();
			for(int i = 0; i < tp.length; i++) {
				for(int j = 0; j < 4; j++) {
					if(Objects.isNull(tp[j][i])) continue;
					System.out.println("Position: " + tp[j][i].getX() + ", " + tp[j][i].getY());
				}
			}
		}
	}

	private static TimelinePosition[][] getPlayerWardSpots(LOLTimeline tl, TimelinePosition[][] wardPlacements) {
		int wi = 0; // ward index
		
		for(TimelineFrame tf : tl.getFrames()) {
			System.out.println("Checking frame " + tf.getTimestamp());
			for(TimelineFrameEvent tfe : tf.getEvents()) {
				WardType wt = tfe.getWardType();
				if(Objects.isNull(wt)) continue;
				System.out.print(wt.prettyName() + ";");
				String pn = wt.prettyName().toLowerCase();

				// 0=Ytrinket 1=Btrinket 2=itemWard 3=control
				int wardTypeIndex = 
						pn.contains("trinket")?
								(pn.contains("yellow")?
										0:1):
								(pn.contains("sight")?
										2:3);
					
				System.out.println("(" + wardTypeIndex + ",  " + (wi+1) + ")");
				wardPlacements[wardTypeIndex][wi++] =
						tfe.getPosition();
				
				try {
					System.out.println("Position: " + tfe.getPosition().getX() + ", " + tfe.getPosition().getY());
				}catch(Exception e) {
					/*System.out.println("\nPosition grab failed.");
					e.printStackTrace();*/
					// RIOT API NO LONGER PROVIDES WARD PLACEMENT LOCATIONS
					continue;
				}
			}
		}
		
		return wardPlacements;
	}

	/*private static void fillSumHistory(SumInfo target, int start, int count) {
		String matchHistoryByPUUID =
				"https://americas.api.riotgames.com/lol/match/v5/matches/by-puuid/"
				+ target.puuid + "/ids?start="
						+ start + "&count=" + count + "&api_key=" + Main.apik;
		
		try {
			String doc = Jsoup.connect(matchHistoryByPUUID)
					.ignoreContentType(true)
					.get().toString();
			//System.out.println(doc);
			doc = doc.split("\\[")[1]
							.split("\\]")[0]
							.replace("\"", "")
							.replace(",", "\n");
			//System.out.println(doc);
			target.history = doc;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	private static void fillSumInfo(SumInfo target) {
		String sumByName =
			"https://na1.api.riotgames.com/lol/summoner/v4/summoners/by-name/"
			+ target.name + "?api_key=" + Main.apik;
		System.out.println(sumByName);
		try {
			String doc = Jsoup.connect(sumByName)
				.ignoreContentType(true)
				.get().toString();
			//System.out.println(doc + "\n");
			doc = doc.split("<body>")[1]
					.split("</body>")[0]
					.replace("\",\"", "\"\n\"")
					.substring(4)
					.replace("}", "");
			
			//System.out.println(doc);
			ot.id = parseSumInfo(doc, "id");
			ot.puuid = parseSumInfo(doc, "puuid");
			ot.accountId = parseSumInfo(doc, "accountId");
			//System.out.println(ot.toString());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String parseSumInfo(String doc, String string) {
		return doc.split(string)[1]
				.split("\"")[2]
				.split("\"")[0];
	}
}

