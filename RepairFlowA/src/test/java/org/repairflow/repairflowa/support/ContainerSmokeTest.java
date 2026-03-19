package org.repairflow.repairflowa.support;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
/**
 * @author guangyang
 * @date 3/8/26 17:29
 * @description TODO: Description
 */

@Disabled
public class ContainerSmokeTest extends IntegrationTestBase {
    @Test
    void containerStart(){
        System.out.println("DOCKER_HOST=" + System.getenv("DOCKER_HOST"));
        System.out.println("user.home=" + System.getProperty("user.home"));
        assertTrue(postgres.isRunning());
    }
}
