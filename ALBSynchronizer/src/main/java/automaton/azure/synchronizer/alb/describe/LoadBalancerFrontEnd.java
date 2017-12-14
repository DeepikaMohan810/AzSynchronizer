package automaton.azure.synchronizer.alb.describe;

import java.util.Map;
import java.util.Map.Entry;





import com.google.gson.Gson;
//import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.azure.management.network.LoadBalancer;
import com.microsoft.azure.management.network.LoadBalancerFrontend;
import com.microsoft.azure.management.network.LoadBalancingRule;

public class LoadBalancerFrontEnd {
   
	public static void getLoadBalancerFrontEnd(JsonObject albResultJson ,Map<String, LoadBalancerFrontend> loadBalancerFrontEndDescription,LoadBalancer loadBalancerDescription){
        Gson gson = new Gson();
		JsonArray frontendArray = new JsonArray();
		if(loadBalancerDescription.frontends()!=null){
		for(Entry<String, LoadBalancerFrontend> frontend : loadBalancerFrontEndDescription.entrySet())
		{
			JsonObject loadBalancingRules = new JsonObject();
			loadBalancingRules.addProperty("alb_frontend_name",frontend.getValue().name());
			loadBalancingRules.add("elb_frontend_inboundnatpool",gson.toJsonTree(frontend.getValue().inboundNatPools()));
			loadBalancingRules.addProperty("alb_frondend_publicip", frontend.getValue().isPublic());
			loadBalancingRules.add("elb_frontend_inboundnatrule",gson.toJsonTree(frontend.getValue().inboundNatRules()));
			getFrontendLBRules(frontend.getValue().loadBalancingRules(),loadBalancingRules);

            frontendArray.add(loadBalancingRules);
		}
		albResultJson.add("alb_frontend",frontendArray);
		}
	}

	private static void getFrontendLBRules(Map<String, LoadBalancingRule> loadBalancingRulesFrontEnd, JsonObject loadBalancingFrontEnd) {

		JsonArray frontendRuleArray = new JsonArray();
		if(loadBalancingRulesFrontEnd != null){
			for(Entry<String, LoadBalancingRule> rule :loadBalancingRulesFrontEnd.entrySet())
			{
				JsonObject feJson = new JsonObject();
				feJson.addProperty("alb_frontend_lbrule_name",rule.getValue().name());
				frontendRuleArray.add(feJson);

			}
			loadBalancingFrontEnd.add("alb_frontend_lbrule", frontendRuleArray);

			
		}
		
	}
}
