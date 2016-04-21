package thesis.cloudfoundry.autoscaling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;

import thesis.cloudfoundry.MonitoringService;

public class CFCalls {
	
	static String oAuthToken; 
	
	public void cfLogin(){
		try {
			
			String cf_login = "cf login -a https://api.cf.sap.hana.ondemand.com -u XXXXXXX -p **********";   // CF Username and Passowrd
			
			Process login = Runtime.getRuntime().exec(cf_login);
			
			 // Printing output of login
            // Get input streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(login.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(login.getErrorStream()));
			
            // Read command standard output
			 String s;
			 while ((s = stdInput.readLine()) != null) {
				 System.out.println(s);
			 }
            
			 // Read command errors
			 while ((s = stdError.readLine()) != null) {
                System.out.println(s);	
			 }
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cfLogout(){
		try {
			
			String cf_logout = "cf logout";
			
			Process logout = Runtime.getRuntime().exec(cf_logout);
			
			 // Printing output of login
            // Get input streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(logout.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(logout.getErrorStream()));
			
            // Read command standard output
			 String s;
			 while ((s = stdInput.readLine()) != null) {
				 System.out.println(s);
			 }
            
			 // Read command errors
			 while ((s = stdError.readLine()) != null) {
                System.out.println(s);	
			 }
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void cfHorizontalScaling(int noInstances, String appName){
		try {
			
			if(!(noInstances >= Constants.min_instances && noInstances <= Constants.max_instances)){
				return;
			}
			
			String cf_scale = "cf scale ";			
			String computed_scale = cf_scale + appName + " -i " + noInstances;
			Process scale = Runtime.getRuntime().exec(computed_scale);
			
			 // Printing output of Scaling
            // Get input streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(scale.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(scale.getErrorStream()));
			
            // Read command standard output
			 String s;
			 while ((s = stdInput.readLine()) != null) {
				 System.out.println(s);
			 }
			 
			 // Read command errors
			 while ((s = stdError.readLine()) != null) {
                System.out.println(s);	
			 }
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	public void pingCFcpu(String appName){
		
		try {
		// String url = "http://www.google.com/search?q=harinigunabalan";
					String url = "https://api.cf.sap.hana.ondemand.com/v2/apps/55b4a61c-ee3b-484c-86c9-e656e99b2a96/stats";
					
					URL obj = new URL(url);
					
					//Proxy instance, proxy ip = 10.0.0.1 with port 8080
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy", 8080));
					
					HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);
					
					// optional default is GET
					con.setRequestMethod("GET");
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("Accept", "application/json");
					con.setRequestProperty("Authorization", oAuthToken);

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
					double avg_cpu_all_instances = ParseCFJsonResponses.parseJsonGetCPUAvg(respose_str);
					synchronized(MonitoringService.cpu){
						MonitoringService.cpu.add(avg_cpu_all_instances);
					}					
					
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
	
	public void getOAuthToken(){		
		
		try {			
			String cf_oauth = "cf oauth-token";			
			Process oAuthToken_process = Runtime.getRuntime().exec(cf_oauth);
			
			 // Printing output of login
            // Get input streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(oAuthToken_process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(oAuthToken_process.getErrorStream()));
			
            // Read command standard output
			 String s;
			 while ((s = stdInput.readLine()) != null) {
				 oAuthToken = s;
				 System.out.println("Key is" + s);
			 }
            
			 // Read command errors
			 while ((s = stdError.readLine()) != null) {
                System.out.println("Error:" + s);	
			 }
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(oAuthToken);
	}
	
	public int getCurrentRunningInstances(){

		try {
		// String url = "http://www.google.com/search?q=harinigunabalan";
					String url = "https://api.cf.sap.hana.ondemand.com/v2/apps/55b4a61c-ee3b-484c-86c9-e656e99b2a96/summary";
					
					URL obj = new URL(url);
					
					//Proxy instance, proxy ip = 10.0.0.1 with port 8080
					Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy", 8080));
					
					HttpURLConnection con = (HttpURLConnection) obj.openConnection(proxy);
					
					// optional default is GET
					con.setRequestMethod("GET");
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("Accept", "application/json");
					con.setRequestProperty("Authorization", oAuthToken);

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
					int curr_inst = ParseCFJsonResponses.parseJsonGetCurrentInstances(respose_str);
					System.out.println("Current Running Instances: " + curr_inst);
					return curr_inst;
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return 0;
				} catch (ProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return 0;
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					return 0;
				}

}	
	
}
