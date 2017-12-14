package automaton.azure.synchronizer.alb.describe;

import java.util.Map;
import java.util.Map.Entry;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.azure.management.network.LoadBalancer;
import com.microsoft.azure.management.network.LoadBalancerHttpProbe;

public class LoadBalancerProbeHttp {

	public static void getLoadBalancerProbeHttp(JsonObject albResultJson,Map<String, LoadBalancerHttpProbe> loadBalancerHttpProbeDescription,LoadBalancer loadBalancerDescription )
	{
	    Gson gson = new Gson();
		JsonArray probeArray = new JsonArray();
		if(loadBalancerDescription.httpProbes()!=null){
			
		for(Entry<String, LoadBalancerHttpProbe> probe : loadBalancerHttpProbeDescription.entrySet() ){
			
			JsonObject probeObject = new JsonObject();
			probeObject.addProperty("alb_httpprobe_name", probe.getValue().name());
			probeObject.addProperty("alb_httpprobe_port", probe.getValue().port());
			probeObject.addProperty("alb_httpprobe_intervalinseconds", probe.getValue().intervalInSeconds());
			probeObject.addProperty("alb_httpprobe_requestpath", probe.getValue().requestPath());
			probeObject.add("alb_httpprobe_protocol",gson.toJsonTree(probe.getValue().protocol()));
			probeObject.addProperty("alb_httpprobe_no.ofprobes", probe.getValue().numberOfProbes());

	        probeArray.add(probeObject);
	
	}
		albResultJson.add("alb_httpprobes",probeArray);
}
}
}
