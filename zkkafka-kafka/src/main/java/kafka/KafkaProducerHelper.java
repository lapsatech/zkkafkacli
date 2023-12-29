package kafka;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Futures;

public class KafkaProducerHelper implements AutoCloseable {
  private final Properties kafkaProperties;
  private final Logger logger;
  private final KafkaProducer<String, String> producer;
  private final KafkaProduceInputParams params;

  public KafkaProducerHelper(KafkaProduceInputParams params) {
    this.params = params;
    this.kafkaProperties = new Properties();
    this.kafkaProperties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, params.getBootstrapServers());
    this.kafkaProperties.setProperty(CommonClientConfigs.CLIENT_ID_CONFIG, params.getClientId());
    this.kafkaProperties.setProperty(CommonClientConfigs.GROUP_ID_CONFIG, params.getGroupId());
    this.logger = LoggerFactory.getLogger(
        KafkaProducerHelper.class.getName()
            + "_" + params.getClientId()
            + "_" + params.getGroupId());
    this.producer = new KafkaProducer<>(kafkaProperties, new StringSerializer(), new StringSerializer());
  }

  public void send() {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("H_m_s_SSS");
    String key = LocalTime.now().format(fmt);
    logger.info("Sending to t:{} p:{}", params.getTopicName(), params.getTopicPartition());

    Map<String, String> msgs = params.getMessagePairs();
    if (msgs.isEmpty()) {
      for (char c = 'A'; c <= 'D'; c++) {
        ProducerRecord<String, String> record = new ProducerRecord<>(
            params.getTopicName(),
            params.getTopicPartition(),
            "key." + key,
            "value." + String.valueOf(c));
        RecordMetadata result = Futures.getUnchecked(producer.send(record));
        logger.info("Sent t:{} p:{} k:{} v:{}", result.topic(), result.partition(), record.key(), record.value());
      }
    } else {
      msgs.forEach((k, v) -> {
        ProducerRecord<String, String> record = new ProducerRecord<>(
            params.getTopicName(),
            params.getTopicPartition(),
            k,
            v);
        RecordMetadata result = Futures.getUnchecked(producer.send(record));
        logger.info("Sent t:{} p:{} k:{} v:{}", result.topic(), result.partition(), record.key(), record.value());
      });
    }
  }

  @Override
  public void close() throws Exception {
    producer.close();
  }
}