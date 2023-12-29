package kafka;

public abstract class KafkaInputParams {

  protected static <T> T nullOrDefault(T o, T def) {
    return o == null ? def : o;
  }

  public abstract static class Builder {

    private static final String DEFAULT_BOOTSTRAP_SERVERS = "kafka-broker-0.kafka.svc.cluster.local:9090,kafka-broker-1.kafka.svc.cluster.local:9091,kafka-broker-2.kafka.svc.cluster.local:9092";
    private static final String DEFAULT_CLIENT_ID = "client-1";
    private static final String DEFAULT_GROUP_ID = "group-1";
    private static final String DEFAULT_TOPIC_NAME = "topic";
    private static final Integer DEFAULT_TOPIC_PARTITION = null;

    protected String bootstrapServers = DEFAULT_BOOTSTRAP_SERVERS;
    protected String clientId = DEFAULT_CLIENT_ID;
    protected String groupId = DEFAULT_GROUP_ID;
    protected String topicName = DEFAULT_TOPIC_NAME;
    protected Integer topicPartition = DEFAULT_TOPIC_PARTITION;

    public void setBootstrapServers(String bootstrapServers) {
      this.bootstrapServers = bootstrapServers;
    }

    public void setClientId(String clientId) {
      this.clientId = clientId;
    }

    public void setGroupId(String groupId) {
      this.groupId = groupId;
    }

    public void setTopicName(String topicName) {
      this.topicName = topicName;
    }

    public void setTopicPartition(Integer topicPartition) {
      this.topicPartition = topicPartition;
    }

  }

  protected final String bootstrapServers;
  protected final String clientId;
  protected final String groupId;
  protected final String topicName;
  protected final Integer topicPartition;

  protected KafkaInputParams(String bootstrapServers, String clientId, String groupId, String topicName,
      Integer topicPartition) {
    this.bootstrapServers = bootstrapServers;
    this.clientId = clientId;
    this.groupId = groupId;
    this.topicName = topicName;
    this.topicPartition = topicPartition;
  }

  public String getBootstrapServers() {
    return bootstrapServers;
  }

  public String getTopicName() {
    return topicName;
  }

  public Integer getTopicPartition() {
    return topicPartition;
  }

  public String getClientId() {
    return clientId;
  }

  public String getGroupId() {
    return groupId;
  }
}