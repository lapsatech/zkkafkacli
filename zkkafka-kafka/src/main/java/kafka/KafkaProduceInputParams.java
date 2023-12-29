package kafka;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

public class KafkaProduceInputParams extends KafkaInputParams {

  public static class Builder extends KafkaInputParams.Builder {

    private MapBuilder<String> messagePairsBuilder = new MapBuilder<>();

    private Builder() {
    }

    public void addMessageComponent(String arg) {
      Objects.requireNonNull(arg, "arg");
      messagePairsBuilder.accept(arg);
    }

    public KafkaProduceInputParams build() {
      Map<String, String> messagePairs = messagePairsBuilder.build(HashMap::new);
      return new KafkaProduceInputParams(bootstrapServers, clientId, groupId, topicName, topicPartition, messagePairs);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final Map<String, String> messagePairs;

  public KafkaProduceInputParams(
      String bootstrapServers,
      String clientId,
      String groupId,
      String topicName,
      Integer topicPartition,
      Map<String, String> messagePairs) {
    super(bootstrapServers, clientId, groupId, topicName, topicPartition);
    this.messagePairs = ImmutableMap.copyOf(messagePairs);
  }

  public Map<String, String> getMessagePairs() {
    return messagePairs;
  }

  @Override
  public String toString() {
    return "KafkaProduceInputParams {"
        + "bootstrapServers=" + bootstrapServers
        + ", clientId=" + clientId
        + ", groupId=" + groupId
        + ", topicName=" + topicName
        + ", topicPartition=" + topicPartition
        + ", messagePairs=" + messagePairs
        + "}";
  }
}