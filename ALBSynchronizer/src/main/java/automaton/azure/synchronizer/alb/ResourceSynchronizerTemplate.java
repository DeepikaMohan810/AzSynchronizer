package automaton.azure.synchronizer.alb;
import com.google.gson.JsonObject;

public abstract class ResourceSynchronizerTemplate {
	
	abstract protected void storeInTarget() throws Exception;
	abstract protected void buildSyncData() throws Exception;
	abstract protected void createClient() throws Exception;
	abstract protected JsonObject getStatistics();
	
	public final void startSynchronizer() throws Exception{
		
		createClient();
		buildSyncData();
		storeInTarget();
		
	}	
}
