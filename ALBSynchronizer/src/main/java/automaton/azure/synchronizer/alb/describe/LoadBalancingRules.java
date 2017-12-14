package automaton.azure.synchronizer.alb.describe;


import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.azure.management.network.LoadBalancer;
import com.microsoft.azure.management.network.LoadBalancingRule;

public class LoadBalancingRules {
	
public static void getLoadBalancingRules(JsonObject albResultJson,Map<String, LoadBalancingRule> loadBalancingRuleDescription,LoadBalancer loadBalancerDescription ){
	
	JsonArray ruleArray = new JsonArray();
	Gson gson = new Gson();
	
	if(loadBalancerDescription.loadBalancingRules()!=null){
	for(Entry<String, LoadBalancingRule> rule: loadBalancingRuleDescription.entrySet() ){
		JsonObject ruleObject = new JsonObject();
		ruleObject.addProperty("alb_loadbalancingrule_name", rule.getValue().name());
		ruleObject.addProperty("alb_loadbalancingrule_floatingIPEnabled", rule.getValue().floatingIPEnabled());
		ruleObject.addProperty("alb_loadbalancingrule_idleTimeoutInMinutes", rule.getValue().idleTimeoutInMinutes());
		ruleObject.add("alb_loadbalancingrule_loaddistributionmethod", (gson.toJsonTree(rule.getValue().loadDistribution())));
		ruleObject.add("alb_loadbalancingrule_protocol",gson.toJsonTree(rule.getValue().protocol()));
		ruleObject.add("alb_loadbalancingrule_backendport",gson.toJsonTree(rule.getValue().backendPort()));
		ruleObject.addProperty("alb_loadbalancingrule_frontendport", rule.getValue().frontendPort());
		ruleObject.add("alb_loadbalancingrule_backendport",gson.toJsonTree(rule.getValue().backendPort()));

    	ruleArray.add(ruleObject);
	}
	albResultJson.add("alb_rules",ruleArray);
	}
}
}
