package kafka;

import static org.lapsa.cli.SimpleArgsParser.toBoolean;
import static org.lapsa.cli.SimpleArgsParser.toInteger;

import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.lapsa.cli.ArgsCollector;

public class KafkaConsumeInputParamsArgsCollector
    implements ArgsCollector<KafkaConsumeInputParams.Builder, KafkaConsumeInputParams> {

  public enum KafkaConsumeOptionsDescriptor implements Option<KafkaConsumeInputParams.Builder> {

    BOOTSTRAP_SERVERS("bootstrap-servers", 'b', KafkaConsumeInputParams.Builder::setBootstrapServers),
    CLIENT("client", 'c', KafkaConsumeInputParams.Builder::setClientId),
    GROUP("group", 'g', KafkaConsumeInputParams.Builder::setGroupId),
    TOPIC_NAME("topic", 't', KafkaConsumeInputParams.Builder::setTopicName),
    TOPIC_PARTITION("partition", 'p', toInteger(KafkaConsumeInputParams.Builder::setTopicPartition)),
    MAX_RECORDS("max-records", 'm', toInteger(KafkaConsumeInputParams.Builder::setTopicPartition)),
    PROMPT_FOR_VALUE("prompt", 'q', false, toBoolean(KafkaConsumeInputParams.Builder::setPromptForContinue));

    private final String longName;
    private final char oneLetterName;
    private final boolean shouldHaveValue;
    private final BiConsumer<KafkaConsumeInputParams.Builder, String> optionConsumer;

    private KafkaConsumeOptionsDescriptor(
        String longName,
        char oneLetterName,
        boolean shouldHaveValue,
        BiConsumer<KafkaConsumeInputParams.Builder, String> optionConsumer) {
      this.longName = longName;
      this.oneLetterName = oneLetterName;
      this.shouldHaveValue = shouldHaveValue;
      this.optionConsumer = optionConsumer;
    }

    private KafkaConsumeOptionsDescriptor(
        String longName,
        char oneLetterName,
        BiConsumer<KafkaConsumeInputParams.Builder, String> optionConsumer) {
      this(longName, oneLetterName, true, optionConsumer);
    }

    @Override
    public boolean shouldHaveValue() {
      return shouldHaveValue;
    }

    @Override
    public String longName() {
      return longName;
    }

    @Override
    public char oneLetterName() {
      return oneLetterName;
    }

    @Override
    public BiConsumer<KafkaConsumeInputParams.Builder, String> optionConsumer() {
      return optionConsumer;
    }
  }

  private static final KafkaConsumeInputParamsArgsCollector INSTANCE = new KafkaConsumeInputParamsArgsCollector();

  public static KafkaConsumeInputParamsArgsCollector instance() {
    return INSTANCE;
  }

  private KafkaConsumeInputParamsArgsCollector() {
  }

  @Override
  public Supplier<KafkaConsumeInputParams.Builder> supplier() {
    return KafkaConsumeInputParams::builder;
  }

  @Override
  public Iterable<? extends Option<KafkaConsumeInputParams.Builder>> options() {
    return EnumSet.allOf(KafkaConsumeOptionsDescriptor.class);
  }

  @Override
  public BiConsumer<KafkaConsumeInputParams.Builder, String> argConsumer() {
    return null;
  }

  @Override
  public Function<KafkaConsumeInputParams.Builder, KafkaConsumeInputParams> finisher() {
    return KafkaConsumeInputParams.Builder::build;
  }

}