package net.adamsmolnik.setup.wf;

import javax.inject.Singleton;
import net.adamsmolnik.setup.ServiceNameResolver;

/**
 * @author ASmolnik
 *
 */
@Singleton
public class WorkflowServiceNameResolver implements ServiceNameResolver {

    @Override
    public String getServiceName() {
        return "wf-service";
    }

}
