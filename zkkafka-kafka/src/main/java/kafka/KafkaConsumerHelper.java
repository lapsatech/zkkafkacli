package kafka;

import java.time.Duration;
import java.util.Collection;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

public class KafkaConsumerHelper implements AutoCloseable {
  private final Properties kafkaProperties;
  private final Logger logger;
  private final KafkaConsumer<String, String> consumer;
  private final KafkaConsumeInputParams params;

  public KafkaConsumerHelper(KafkaConsumeInputParams params) {
    this.params = params;
    this.kafkaProperties = new Properties();
    this.kafkaProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, params.getBootstrapServers());
    this.kafkaProperties.setProperty(CommonClientConfigs.CLIENT_ID_CONFIG, params.getClientId());
    this.kafkaProperties.setProperty(CommonClientConfigs.GROUP_ID_CONFIG, params.getGroupId());
    this.logger = LoggerFactory.getLogger(
        KafkaConsumerHelper.class.getName()
            + "_" + params.getClientId()
            + "_" + params.getGroupId());
    this.consumer = new KafkaConsumer<>(kafkaProperties, new StringDeserializer(),
        new StringDeserializer());
  }

  private int receivedTotal = 0;

  public void init() {
    if (params.getTopicPartition() != null) {
      TopicPartition tp = new TopicPartition(params.getTopicName(), params.getTopicPartition().intValue());
      Collection<TopicPartition> partitions = ImmutableSet.of(tp);
      consumer.assign(partitions);
      logger.info("Assigned to {}", tp);
      logger.info("Beginning offsets {}", consumer.beginningOffsets(partitions));
      logger.info("End offsets {}", consumer.endOffsets(partitions));
    } else {
      consumer.subscribe(ImmutableSet.of(params.getTopicName()));
      logger.info("Subscribed to {}", params.getTopicName());
    }
  }

  public void poll() {
    int received = 0;
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));

      received += records.count();
      records.forEach(rec -> {
        logger.info("Received t:{} p:{} k:{} v:{}", rec.topic(), rec.partition(), rec.key(), rec.value());
      });
      if (records.isEmpty()) {
        break;
      }
      Thread.yield();
    }
    if (received > 0) {
      receivedTotal += received;
      logger.info("Received {}, total {}", received, receivedTotal);
    }
  }

  public int getReceived() {
    return receivedTotal;
  }

  @Override
  public void close() throws Exception {
    consumer.close();
  }
}