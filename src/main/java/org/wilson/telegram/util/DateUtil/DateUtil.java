package org.wilson.telegram.util.DateUtil;

import java.net.InetAddress;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.wilson.telegram.client.Cache;

public final class DateUtil {
	
	static final String[] TIME_SERVERS = {"time-a.nist.gov", "time-b.nist.gov", "time-c.nist.gov", "nist-time-server.eoni.com", "nist1-macon.macon.ga.us",
			"wolfnisttime.com", "nist.netservicesgroup.com", "nisttime.carsoncity.k12.mi.us"};
	
	static final NTPUDPClient timeClient = new NTPUDPClient();
	static final int MAX_TRIES = TIME_SERVERS.length;



	public static LocalDateTime getCurrentTime() {

		timeClient.setDefaultTimeout(5000);
		int tries = 0;
		boolean completed = false;
		long returnTime = 0;
		
		while(tries<MAX_TRIES && !completed){
			try {
				System.out.println("contacting time servers...");

				InetAddress inetAddress = InetAddress.getByName(TIME_SERVERS[tries]);
				TimeInfo timeInfo = timeClient.getTime(inetAddress);
				returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
				LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(returnTime), ZoneId.systemDefault());
				Cache.getInstance().setCurrentTime(date);
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
