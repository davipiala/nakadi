package org.zalando.nakadi.service.subscription.zk;

import org.zalando.nakadi.service.subscription.model.Partition;
import org.zalando.nakadi.service.subscription.model.Session;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public final class ZkSubscriptionNode {

    private Partition[] partitions;
    private Session[] sessions;

    public ZkSubscriptionNode() {
        this.partitions = new Partition[0];
        this.sessions = new Session[0];
    }

    public ZkSubscriptionNode(final Partition[] partitions, final Session[] sessions) {
        this.partitions = partitions;
        this.sessions = sessions;
    }

    public void setPartitions(final Partition[] partitions) {
        this.partitions = partitions;
    }

    public void setSessions(final Session[] sessions) {
        this.sessions = sessions;
    }

    public Partition[] getPartitions() {
        return partitions;
    }

    public Session[] getSessions() {
        return sessions;
    }

    public Partition.State guessState(final String eventType, final String partition) {
        return getPartitionWithActiveSession(eventType, partition)
                .map(Partition::getState)
                .orElse(Partition.State.UNASSIGNED);
    }

    private Optional<Partition> getPartitionWithActiveSession(final String eventType, final String partition) {
        return Stream.of(partitions)
                .filter(p -> p.getPartition().equals(partition) && p.getEventType().equals(eventType))
                .filter(p -> Stream.of(sessions).anyMatch(s -> s.getId().equalsIgnoreCase(p.getSession())))
                .findAny();
    }

    @Nullable
    public String guessStream(final String eventType, final String partition) {
        return getPartitionWithActiveSession(eventType, partition)
                .map(Partition::getSession)
                .orElse(null);
    }

}
