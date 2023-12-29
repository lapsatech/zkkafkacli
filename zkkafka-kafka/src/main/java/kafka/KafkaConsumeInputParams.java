package kafka;

public final class KafkaConsumeInputParams extends KafkaInputParams {

  public static class Builder extends KafkaInputParams.Builder {
    private static final Integer DEFAULT_MAX_RECORD = null;
    private static final Boolean DEFAULT_PROMPT_FOR_CONTINUE = Boolean.FALSE;

    protected Integer maxRecord = DEFAULT_MAX_RECORD;
    protected Boolean promptForContinue = DEFAULT_PROMPT_FOR_CONTINUE;

    private Builder() {
    }

    public void setMaxRecord(Integer maxRecord) {
      this.maxRecord = maxRecord;
    }

    public void setPromptForContinue(Boolean promptForContinue) {
      this.promptForContinue = promptForContinue;
    }

    public KafkaConsumeInputParams build() {
      return new KafkaConsumeInputParams(bootstrapServers, clientId, groupId, topicName, topicPartition, maxRecord,
          promptForContinue);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private KafkaConsumeInputParams(String bootstrapServers,
      String clientId,
      String groupId,
      String topicName,
      Integer topicPartition,
      Integer maxRecord,
      Boolean promptForContinue) {
    super(bootstrapServers, clientId, groupId, topicName, topicPartition);
    this.maxRecord = maxRecord;
    this.promptForContinue = promptForContinue;
  }

  protected final Integer maxRecord;
  protected final Boolean promptForContinue;

  public Integer getMaxRecord() {
    return maxRecord;
  }

  public Boolean isPromptForContinue() {
    return promptForContinue;
  }

  @Override
  public String toString() {
    return "KafkaConsumeInputParams {"
        + "bootstrapServers=" + bootstrapServers
        + ", clientId=" + clientId
        + ", groupId=" + groupId
        + ", topicName=" + topicName
        + ", topicPartition=" + topicPartition
        + ", maxRecord=" + maxRecord
        + ", promptForContinue=" + promptForContinue
        + "}";
  }
}