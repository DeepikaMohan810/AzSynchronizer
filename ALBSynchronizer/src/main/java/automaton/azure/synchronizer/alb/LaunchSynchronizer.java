package automaton.azure.synchronizer.alb;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import automaton.assets.status.templates.JobStatus;
import automaton.assets.status.templates.JobStatus.status;
import automaton.azure.synchronizer.alb.util.Utility;
import automaton.utility.common.CommonUtils;
import automaton.utility.date.DateUtil;
import automaton.utility.id.IDUtil;

import com.google.gson.JsonObject;


public class LaunchSynchronizer {

	private static final Logger LOG = Logger.getLogger(ALBSynchronizer.class);
	private static String runId, id, startTime;
	
	public static void main(String[] args) {
		
		LOG.info("Starting AzureLoadBalanacer Synchronizer");
		List<JsonObject> statistics = new ArrayList<JsonObject>();

		if (args.length != 2) return;
		String accountId = args[0];
		String jobId = args[1];
		
		if (!CommonUtils.isJobIdValid(jobId)) return;
		
		LOG.debug("validated job id: "+jobId);
		
		try{
			runId = CommonUtils.getRunId(jobId, accountId);
			id = IDUtil.getID(jobId+runId,"md5");
			startTime = DateUtil.getCurrentTime();
			
			Utility.getBackup(CommonUtils.getServiceId(jobId));
			
			JobStatus.updateJobStatusById(id, jobId, runId, accountId, startTime, status.RUNNING, "LoadBalancer", "Synchronizer", null, null, null);
			
			ResourceSynchronizerTemplate synchronizer = new ALBSynchronizer(accountId);
					synchronizer.startSynchronizer();
					statistics.add(synchronizer.getStatistics());
				
			JobStatus.updateJobStatusById(id,jobId, runId, accountId, startTime, status.COMPLETED, "LoadBalancer", "Synchronizer", DateUtil.getCurrentTime(),"",statistics );
		
		}catch(Exception exception){
			LOG.error("Error in building Synchronizer data in account <"+accountId+">: "+exception);
			JobStatus.updateJobStatusById(id,jobId, runId, accountId, startTime, status.ERROR,"LoadBalancer", "Synchronizer", DateUtil.getCurrentTime(), exception.getMessage(), statistics);
		}
	}
}
