package automaton.azure.synchronizer.alb;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import automaton.azure.synchronizer.alb.ResourceSynchronizerTemplate;
import automaton.azure.synchronizer.alb.describe.LoadBalancerBackEnd;
import automaton.azure.synchronizer.alb.describe.LoadBalancerFrontEnd;
import automaton.azure.synchronizer.alb.describe.LoadBalancerProbeHttp;
import automaton.azure.synchronizer.alb.describe.LoadBalancerProbeTcp;
import automaton.azure.synchronizer.alb.describe.LoadBalancingRules;
import automaton.azure.synchronizer.alb.util.Utility;
import automaton.mapper.Mapper;
import automaton.utility.id.IDUtil;

import com.microsoft.azure.*;
import com.microsoft.azure.credentials.ApplicationTokenCredentials;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.network.LoadBalancer;

public class ALBSynchronizer extends ResourceSynchronizerTemplate
{
	Azure azure;
	private static final Logger LOG = Logger.getLogger(ALBSynchronizer.class);
	private Map<String, JsonObject> albDescribeResult = new HashMap<String, JsonObject>();
	private String accountId;
	private String region;
	private Gson gson = new Gson();
	protected JsonObject statistics = new JsonObject();
	
	public ALBSynchronizer (String accountId)
	{
		this.accountId = accountId;
	}


	protected void storeInTarget() throws Exception {
		try {		
			if (!albDescribeResult.isEmpty()){
				JsonObject dataToLoad =  new JsonObject();
				dataToLoad.add("data",gson.toJsonTree(albDescribeResult));
				dataToLoad.addProperty("type", "bulk");
				dataToLoad.addProperty("timestamp", true);
				dataToLoad.addProperty("timestampAttributeName", "asset_time");
				Utility.getStorage().store(dataToLoad);
				albDescribeResult.clear();
				LOG.info("Results are successfully stored in elasticsearch");
			}
		} catch (Exception exception) {
			LOG.error("Error in storing results: "+exception);
			statistics.addProperty("error_message", exception.getMessage());
			statistics.addProperty("details", "failed to store data");
			throw exception;
		}
	}

	protected void buildSyncData() throws Exception {

		 List<LoadBalancer> loadBalancerDescriptions = azure.loadBalancers().list();
		 JsonObject albResultJson;
	     statistics.addProperty("total_resources_count", loadBalancerDescriptions.size());
	     String resourceName = null;
	     try{
	    	 for (LoadBalancer loadBalancerDescription : loadBalancerDescriptions) {
	    		 LOG.info("Building ALB sync data");
			     resourceName = loadBalancerDescription.name();
			     albResultJson = new JsonObject();
			     albResultJson = getRelations(loadBalancerDescription,albResultJson);
		
				albResultJson = getMappedJson(albResultJson);
				albDescribeResult.put(IDUtil.getID(resourceName+region+accountId, "md5"), albResultJson);
				LOG.info(albResultJson);
			}		
			LOG.info(albDescribeResult);
			
		}catch (Exception exception){
			LOG.error("Error in building sync data: "+exception);
			statistics.addProperty("error_message", exception.getMessage());
			statistics.addProperty("details", "failed to build data");
			statistics.addProperty("region", region);
			statistics.addProperty("error_at_resource", resourceName);
			throw exception;
		}
			   	
	  }
	     
	private JsonObject getMappedJson(JsonObject albResultJson) throws Exception {

		try{
			Mapper mapper = Utility.getMapper();
			if (mapper != null)
				albResultJson = mapper.getMappedJson(albResultJson);
		} catch (Exception exception) {
			LOG.error("Error in doing mapping: "+exception);
			throw exception;
		}
		LOG.info("Mapping completed");
		
		return albResultJson;
	}


	private JsonObject getRelations(LoadBalancer loadBalancerDescription,
			JsonObject albResultJson) {
		
		albResultJson.addProperty("account_id", accountId);
		albResultJson.addProperty("alb_name", loadBalancerDescription.name());
		albResultJson.addProperty("alb_id",loadBalancerDescription.id() );
		albResultJson.add("alb_regionname", gson.toJsonTree(loadBalancerDescription.regionName()));
		albResultJson.add("alb_resourcegroupname", gson.toJsonTree(loadBalancerDescription.resourceGroupName()));
		albResultJson.add("alb_publicipid", gson.toJsonTree(loadBalancerDescription.publicIPAddressIds()));
		albResultJson.addProperty("alb_rules_count", loadBalancerDescription.loadBalancingRules().size());
		LoadBalancingRules.getLoadBalancingRules(albResultJson, loadBalancerDescription.loadBalancingRules(),loadBalancerDescription);
		albResultJson.addProperty("alb_httpprobe_count", loadBalancerDescription.httpProbes().size());
        LoadBalancerProbeHttp.getLoadBalancerProbeHttp(albResultJson, loadBalancerDescription.httpProbes(),loadBalancerDescription);
        albResultJson.addProperty("alb_tcpprobe_count", loadBalancerDescription.tcpProbes().size());
        LoadBalancerProbeTcp.getLoadBalancerProbeTcp(albResultJson, loadBalancerDescription.tcpProbes(),loadBalancerDescription);
        albResultJson.addProperty("alb_frontend_count",loadBalancerDescription.frontends().size());
        LoadBalancerFrontEnd.getLoadBalancerFrontEnd(albResultJson,loadBalancerDescription.frontends(),loadBalancerDescription);
        albResultJson.addProperty("alb_backend_count", loadBalancerDescription.backends().size());
        LoadBalancerBackEnd.getLoadBalancerBackEnd(albResultJson, loadBalancerDescription.backends(),loadBalancerDescription);
        
		return albResultJson;
	
	}

	protected void createClient() throws Exception {

		try{
			
			ApplicationTokenCredentials credentials = new ApplicationTokenCredentials("83af228f-b7dc-4bf3-aeda-17add6806641","39282642-8418-47f5-bdec-4c1dfbcf42e9","Deepika@810",AzureEnvironment.AZURE);
    		azure = Azure.authenticate(credentials).withSubscription("da9668c3-7699-4079-83d5-1a3f66b2d635");
    		LOG.info("ALB client has been created");

		}catch (Exception exception) {
			LOG.info("Error in generating ALB client " + exception);
			statistics.addProperty("error_message", exception.getMessage());
			statistics.addProperty("details", "failed to create client");
			throw exception;
		}
	}

	protected JsonObject getStatistics() {

		return statistics;
	}
   
}
