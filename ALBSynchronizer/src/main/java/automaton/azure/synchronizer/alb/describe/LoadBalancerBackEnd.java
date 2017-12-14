package automaton.azure.synchronizer.alb.describe;


import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.microsoft.azure.management.network.LoadBalancer;
import com.microsoft.azure.management.network.LoadBalancerBackend;
import com.microsoft.azure.management.network.LoadBalancingRule;

public class LoadBalancerBackEnd {
   
	public static void getLoadBalancerBackEnd(JsonObject albResultJson ,Map<String, LoadBalancerBackend> loadBalancerBackEndDescription,LoadBalancer loadBalancerDescription){
        Gson gson = new Gson();
		JsonArray backendArray = new JsonArray();
		
		if(loadBalancerDescription.backends()!=null){
			
		for(Entry<String, LoadBalancerBackend> backend : loadBalancerBackEndDescription.entrySet())
		{
			JsonObject backendObject = new JsonObject();
			backendObject.addProperty("alb_backend_name",backend.getValue().name());
			backendObject.add("alb_backend_nicipconfig",gson.toJsonTree(backend.getValue().backendNicIPConfigurationNames()));
			getBackendLBRules(backend.getValue().loadBalancingRules(),backendObject);
            getVirtualMachineIds(backend.getValue().getVirtualMachineIds(),backendObject);
            backendArray.add(backendObject);
		}
		albResultJson.add("alb_backend",backendArray);
		}
	}

	private static void getVirtualMachineIds(Set<String> virtualMachineIds,JsonObject loadBalancingRulesVM) {

		JsonArray backendVmArray = new JsonArray();
		if(virtualMachineIds != null)
		{
			for(String virtualMachineId : virtualMachineIds)
			{
				JsonObject backendVmObject = new JsonObject();
				backendVmObject.addProperty("alb_backend_vm_id",virtualMachineId );
				backendVmArray.add(backendVmObject);
			}
			loadBalancingRulesVM.add("alb_backend_vm",backendVmArray );
			
		}
		
		
	}

	private static void getBackendLBRules(Map<String, LoadBalancingRule> loadBalancingRulesBackEnd, JsonObject loadBalancingBackEnd) {

		JsonArray lbRuleArray = new JsonArray();
		if(loadBalancingRulesBackEnd != null){
			for(Entry<String, LoadBalancingRule> rules :loadBalancingRulesBackEnd.entrySet())
			{
				JsonObject lbRuleObject = new JsonObject();
				lbRuleObject.addProperty("alb_backend_lbrule_name",rules.getValue().name());
				lbRuleArray.add(lbRuleObject);

			}
			loadBalancingBackEnd.add("alb_backend_lbrule", lbRuleArray);

			
		}
		
		
	}
}

