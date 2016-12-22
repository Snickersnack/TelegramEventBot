package org.wilson.telegram.util;

import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.wilson.telegram.config.BotConfig;

public class DateUtil {

	public static LocalDateTime getCurrentTime() {
		String[] TIME_SERVERS = {"time-a.nist.gov", "time-b.nist.gov", "time-c.nist.gov", "nist-time-server.eoni.com", "nist1-macon.macon.ga.us",
				"wolfnisttime.com", "nist.netservicesgroup.com", "nisttime.carsoncity.k12.mi.us"};
		NTPUDPClient timeClient = new NTPUDPClient();
		timeClient.setDefaultTimeout(5000);
		int tries = 0;
		int MAX_TRIES = TIME_SERVERS.length;
		boolean completed = false;
		long returnTime = 0;
		
		while(tries<MAX_TRIES && !completed){
			try {
				System.out.println("contacting servers...");

				InetAddress inetAddress = InetAddress.getByName(TIME_SERVERS[tries]);
				TimeInfo timeInfo = timeClient.getTime(inetAddress);
				returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
				LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(returnTime), ZoneId.systemDefault());
				completed = true;
				return date;
			} catch (Exception e) {
				System.out.println("Unable to get Current time: " + e);
				tries++;
				
			} finally{
				timeClient.close();
			}
		}


		return null;
	}

}
