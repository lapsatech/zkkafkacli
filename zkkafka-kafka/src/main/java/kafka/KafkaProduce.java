package kafka;

import org.lapsa.cli.SimpleArgsParser;
import org.slf4j.LoggerFactory;

public class KafkaProduce {

  public static void main(String[] args) throws Exception {
    KafkaProduceInputParams params = SimpleArgsParser.parse(KafkaProduceInputParamsArgsCollector.instance(), args);
    LoggerFactory.getLogger(KafkaProduce.class).info("Running {}", params);
    try (KafkaProducerHelper helper = new KafkaProducerHelper(params)) {
      helper.send();
    }
  }
}
