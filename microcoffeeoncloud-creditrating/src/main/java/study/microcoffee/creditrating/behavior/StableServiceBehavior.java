package study.microcoffee.creditrating.behavior;

/**
 * Class that implements stable service behavior.
 */
public class StableServiceBehavior extends AbstractServiceBehavior {

    @Override
    public void execute() {
        sleep(DEFAULT_EXECUTION_TIME_MS);
    }
}
