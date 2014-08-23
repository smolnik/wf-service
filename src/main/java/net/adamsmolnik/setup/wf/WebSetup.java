package net.adamsmolnik.setup.wf;

import java.util.Map;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import net.adamsmolnik.setup.ServiceNameResolver;
import net.adamsmolnik.util.Configuration;
import net.adamsmolnik.util.ConfigurationKeys;
import net.adamsmolnik.workflow.DataProcessingWorkflowImpl;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflow;
import com.amazonaws.services.simpleworkflow.AmazonSimpleWorkflowClient;
import com.amazonaws.services.simpleworkflow.flow.WorkflowWorker;

/**
 * @author ASmolnik
 *
 */
@WebListener("workflowSetup")
public class WebSetup implements ServletContextListener {

    @Inject
    private ServiceNameResolver snr;

    @Inject
    private Configuration conf;

    private WorkflowWorker wfw;;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ClientConfiguration config = new ClientConfiguration().withSocketTimeout(70 * 1000);
        AmazonSimpleWorkflow service = conf.isSystemCredentialsExist() ? new AmazonSimpleWorkflowClient(new BasicAWSCredentials(
                conf.getGlobalValue(ConfigurationKeys.ACCESS_KEY_ID.getKey()), conf.getGlobalValue(ConfigurationKeys.SECRET_KEY.getKey())), config)
                : new AmazonSimpleWorkflowClient(config);

        Map<String, String> confMap = conf.getServiceConfMap(snr.getServiceName());
        service.setEndpoint(confMap.get("swf.endpoint"));
        wfw = new WorkflowWorker(service, confMap.get("swf.domain"), confMap.get("swf.tasks"));
        try {
            wfw.addWorkflowImplementationType(DataProcessingWorkflowImpl.class);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        wfw.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        wfw.shutdownNow();
    }

}
