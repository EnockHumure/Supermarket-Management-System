package notification;

import org.apache.activemq.broker.BrokerService;

public class EmbeddedActiveMQBroker {
    
    private BrokerService broker;
    
    public void start() throws Exception {
        broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.setPersistent(false);
        broker.start();
        System.out.println("✓ Embedded ActiveMQ Broker started on tcp://localhost:61616");
    }
    
    public void stop() throws Exception {
        if (broker != null) {
            broker.stop();
            System.out.println("✓ Embedded ActiveMQ Broker stopped");
        }
    }
}
