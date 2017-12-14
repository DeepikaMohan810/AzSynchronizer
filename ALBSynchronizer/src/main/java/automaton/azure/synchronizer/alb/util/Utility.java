package automaton.azure.synchronizer.alb.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import automaton.ingester.storage.IStorage;
import automaton.ingester.storage.StorageFactory;
import automaton.mapper.Mapper;
import automaton.utility.date.DateUtil;
import automaton.utility.elasticsearch.Elasticsearch;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Utility {
	private static final Log LOG = LogFactory.getLog(Utility.class);
	private Utility(){}
	private static JsonObject properties;
	private static Mapper mapper;
	private static IStorage storage;
	
	public static void setProperties(String serviceId) throws Exception{
		try{
			if (properties == null){
				String esIndex = "esi_master_job_configuration";
				String esType = "master_service_configuration";
				String query = "{\"size\":1,\"query\":{\"bool\":{\"must\":{\"term\":{\"service_id\":\""+serviceId+"\"}}}}}";
				JsonArray results = Elasticsearch.getQueryResult(esIndex, esType, query);
				if (results.size() != 0)
					properties = results.get(0).getAsJsonObject();	
			}
		}catch(Exception e){
			LOG.info("Error in getting properties from elasticsearch: "+e);
			throw new Exception(e);
		}
	}
	
	public static Mapper getMapper(){
		if (mapper == null){
			if (properties.has("mapperProperties")){
				mapper =  new Mapper(properties.get("mapperProperties").getAsJsonObject());
			}
		}
		return mapper;
	}
	
	public static IStorage getStorage(){
		if (storage == null){
			storage = StorageFactory.getInstance(properties.get("storageProperties").getAsJsonObject());
		}
		return storage;
	}	
	public static void getBackup(String serviceId) throws Exception{
		try{
			setProperties(serviceId);
			if (properties != null && properties.get("backup").getAsBoolean()){
				Map<String, String> script = new LinkedHashMap<String,String>();
				script.put("inline", "ctx._source.audit_time = \'"+DateUtil.getCurrentTime()+"\'; ctx._id = ctx._source.asset_id");
				JsonObject backupProperties = getBackupProperties();
				JsonObject storageProperties = getStorageProperties();
				boolean state = Elasticsearch.reindex(storageProperties.get("index").getAsString(),
						backupProperties.get("index").getAsString(), storageProperties.get("type").getAsString(), 
						backupProperties.get("type").getAsString(), script);
			    if (!state) LOG.error("Backup failed");
			}
		}catch(Exception exception){
			LOG.error("Error in taking backup: "+exception);
			throw exception;
		}
	}
	public static JsonObject getStorageProperties(){
		JsonObject result = new JsonObject();
		if (properties != null && properties.has("storageProperties")){
			result = properties.get("storageProperties").getAsJsonObject();
		}
		return result;
	}
	
	public static JsonObject getBackupProperties(){
		JsonObject result = new JsonObject();
		if (properties != null){
			if (properties.has("backupProperties")){
				result.addProperty("index", properties.get("backupProperties").getAsJsonObject().get("index").getAsString());
				result.addProperty("type", properties.get("backupProperties").getAsJsonObject().get("type").getAsString());
			}
			else if (properties.has("storageProperties")){
				result.addProperty("index", properties.get("storageProperties").getAsJsonObject().get("index").getAsString()+"_audit");
				result.addProperty("type", properties.get("storageProperties").getAsJsonObject().get("type").getAsString());
			}
		}
		return result;
	}
}
