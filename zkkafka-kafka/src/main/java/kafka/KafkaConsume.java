package kafka;

import org.lapsa.cli.SimpleArgsParser;
import org.slf4j.LoggerFactory;

public class KafkaConsume {

  public static void main(String[] args) throws Exception {
    KafkaConsumeInputParams params = SimpleArgsParser.parse(KafkaConsumeInputParamsArgsCollector.instance(), args);
    LoggerFactory.getLogger(KafkaConsume.class).info("Running {}", params);

    try (KafkaConsumerHelper consumerHelper = new KafkaConsumerHelper(params);
        ConsoleHelper cc = new ConsoleHelper()) {
      consumerHelper.init();
      while (params.getMaxRecord() == null || consumerHelper.getReceived() < params.getMaxRecord().intValue()) {
        consumerHelper.poll();
        if (params.isPromptForContinue()) {
          if (!cc.shallContinue(10_000)) {
            break;
          }
        }
      }
    }
  }

}
