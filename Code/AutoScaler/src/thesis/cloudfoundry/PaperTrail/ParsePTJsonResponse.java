package thesis.cloudfoundry.PaperTrail;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import thesis.cloudfoundry.MonitoringService;

public class ParsePTJsonResponse {

	public static void setResponseTimeAndThroughput(String json){
		
		List<Double> response_times = new ArrayList<Double>();
		
		 try {

	            JsonParser jsonParser = new JsonParser();
	            JsonObject jo = (JsonObject)jsonParser.parse(json);
	            JsonArray ja = (JsonArray)jo.get("events");
	           
	            int count_nonRTRmsgs = 0;
	            for(int i = 0; i< ja.size(); i++){	       
	            	// Getting CPU Utilization%
	            	String message =  ja.get(i).getAsJsonObject().get("message").getAsString();
	            	int index = message.indexOf("response_time");
	            	if(index == -1){
	            		count_nonRTRmsgs++;
	            		continue;
	            		}
	            	Double rt = Double.parseDouble(message.substring(index+14, index+22));	            	
	            	response_times.add(rt);	               	
	            }
	            MonitoringService.modelDataObject.setRequestsPerSecond(ja.size() - count_nonRTRmsgs);	 						// Set the Throughput
            
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		 
         // Calculating average Response time of all the requests
		 if(!(response_times.isEmpty())){
	         double rt_avg = (response_times.parallelStream().mapToDouble(e -> e.doubleValue()).average().getAsDouble())*1000;		// In milliseconds
	         System.out.println(rt_avg);	            	                     
	         MonitoringService.modelDataObject.setAvgResponseTime(rt_avg);	 														// Set the Average Response Times  
		 }
        
	}	
	
}
