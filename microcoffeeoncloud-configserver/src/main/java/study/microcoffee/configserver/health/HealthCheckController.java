package study.microcoffee.configserver.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check controller for use by Kubernetes as readiness and liveness probes.
 */
@RestController
@RequestMapping(path = "/internal")
public class HealthCheckController {

    private final Logger logger = LoggerFactory.getLogger(HealthCheckController.class);

    @GetMapping(path = "/isready")
    public void isReady() {
        logger.debug("/isready");
    }

    @GetMapping(path = "/isalive")
    public void isAlive() {
        logger.debug("/isalive");
    }
}
