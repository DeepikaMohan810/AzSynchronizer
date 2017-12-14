package automaton.azure.synchronizer.alb.describe;

import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.azure.management.network.LoadBalancer;
import com.microsoft.azure.management.network.LoadBalancerTcpProbe;

public class LoadBalancerProbeTcp {
	
	public static void getLoadBalancerProbeTcp(JsonObject albResultJson,Map<String, LoadBalancerTcpProbe> loadBalancerTcpProbeDescription,LoadBalancer loadBalancerDescription )
	{
	    Gson gson = new Gson();
		JsonArray tcpProbeArray = new JsonArray();
		if(loadBalancerDescription.tcpProbes()!=null){
			
		for(Entry<String, LoadBalancerTcpProbe> probe: loadBalancerTcpProbeDescription.entrySet() ){
			JsonObject tcpProbeObject = new JsonObject();
			tcpProbeObject.addProperty("alb_tcpprobe_name", probe.getValue().name());
			tcpProbeObject.addProperty("alb_tcpprobe_port", probe.getValue().port());
			tcpProbeObject.addProperty("alb_tcpprobe_intervalinseconds", probe.getValue().intervalInSeconds());
			tcpProbeObject.add("alb_tcpprobe_protocol",gson.toJsonTree(probe.getValue().protocol()));
			tcpProbeObject.addProperty("alb_tcpprobe_no.ofprobes", probe.getValue().numberOfProbes());

	    	tcpProbeArray.add(tcpProbeObject);
	
	}
		albResultJson.add("alb_tcpprobes",tcpProbeArray);
}
}

}
