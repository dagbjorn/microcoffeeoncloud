package study.microcoffee.creditrating.behavior;

/**
 * Class that implements slow service behavior using a fixed delay before returning.
 */
public class SlowServiceBehavior extends AbstractServiceBehavior {

    private int delaySecs;

    public SlowServiceBehavior(int delaySecs) {
        this.delaySecs = delaySecs;
    }

    @Override
    public void execute() {
        sleep(delaySecs * 1000);
    }
}
