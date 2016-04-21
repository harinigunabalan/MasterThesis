package thesis.cloudfoundry.PaperTrail;

import static java.lang.Math.toIntExact;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.Base64;
import java.util.Date;

import thesis.cloudfoundry.MonitoringService;

public class PaperTrailAPI {

	public void callPaperTrailAPI(){
		try {
				String min_time, max_time;	
				int curr_time = getCurrentUnixTS();
				max_time = Integer.toString(curr_time);
				min_time = Integer.toString(curr_time - 60);
				String url = "https://papertrailapp.com/api/v1/events/search.json?min_time=" + min_time + "&&max_time=" + max_time;
				
				URL obj = new URL(url);
				
				//Proxy instance, proxy ip = 10.0.0.1 with port 8080
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy", 8080));
				
				HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);
				
				// optional default is GET
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/json");
				con.setRequestProperty("Accept", "application/json");
				con.setRequestProperty("Authorization", "Basic " + encodedLogin());
	
				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);
	
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
	
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
	
				//print result
				System.out.println(response.toString());						
				String respose_str = response.toString();
				ParsePTJsonResponse.setResponseTimeAndThroughput(respose_str);
				
				MonitoringService.modelDataObject.setTimeStamp(curr_time);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ProtocolException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	}
	
	public String encodedLogin(){
		String authStr = "harinigunabalan24@gmail.com" + ":" + "*********";       //Password
	    String authEncoded = Base64.getEncoder().encodeToString(authStr.getBytes());	    
	    return authEncoded;
	}

	public int getCurrentUnixTS(){
		
		try {
			 Date date = new Date();			   
			 long unixTime = (long) date.getTime()/1000;
			 int time = toIntExact(unixTime);
			 System.out.println(unixTime); 		
			 System.out.println(time);
			 return time;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}

	}
	
}
