package kafka;

import static org.lapsa.cli.SimpleArgsParser.toInteger;

import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.lapsa.cli.ArgsCollector;

public class KafkaProduceInputParamsArgsCollector
    implements ArgsCollector<KafkaProduceInputParams.Builder, KafkaProduceInputParams> {

  public enum KafkaProduceOptionDescriptor implements Option<KafkaProduceInputParams.Builder> {
    CLIENT("client", 'c', KafkaProduceInputParams.Builder::setClientId),
    GROUP("group", 'g', KafkaProduceInputParams.Builder::setGroupId),
    TOPIC_NAME("topic", 't', KafkaProduceInputParams.Builder::setTopicName),
    TOPIC_PARTITION("partition", 'p', toInteger(KafkaProduceInputParams.Builder::setTopicPartition));

    private final String longName;
    private final char oneLetterName;
    private final boolean shouldHaveValue;
    private final BiConsumer<KafkaProduceInputParams.Builder, String> optionConsumer;

    private KafkaProduceOptionDescriptor(
        String longName,
        char oneLetterName,
        boolean shouldHaveValue,
        BiConsumer<KafkaProduceInputParams.Builder, String> optionConsumer) {
      this.longName = longName;
      this.oneLetterName = oneLetterName;
      this.shouldHaveValue = shouldHaveValue;
      this.optionConsumer = optionConsumer;
    }

    private KafkaProduceOptionDescriptor(
        String longName,
        char oneLetterName,
        BiConsumer<KafkaProduceInputParams.Builder, String> applyFunction) {
      this(longName, oneLetterName, true, applyFunction);
    }

    @Override
    public boolean shouldHaveValue() {
      return shouldHaveValue;
    }

    @Override
    public BiConsumer<KafkaProduceInputParams.Builder, String> optionConsumer() {
      return optionConsumer;
    }

    @Override
    public String longName() {
      return longName;
    }

    @Override
    public char oneLetterName() {
      return oneLetterName;
    }
  }

  private static final KafkaProduceInputParamsArgsCollector INSTANCE = new KafkaProduceInputParamsArgsCollector();

  public static KafkaProduceInputParamsArgsCollector instance() {
    return INSTANCE;
  }

  private KafkaProduceInputParamsArgsCollector() {
  }

  @Override
  public Supplier<KafkaProduceInputParams.Builder> supplier() {
    return KafkaProduceInputParams::builder;
  }

  @Override
  public Iterable<? extends Option<KafkaProduceInputParams.Builder>> options() {
    return EnumSet.allOf(KafkaProduceOptionDescriptor.class);
  }

  @Override
  public BiConsumer<KafkaProduceInputParams.Builder, String> argConsumer() {
    return KafkaProduceInputParams.Builder::addMessageComponent;
  }

  @Override
  public Function<KafkaProduceInputParams.Builder, KafkaProduceInputParams> finisher() {
    return KafkaProduceInputParams.Builder::build;
  }

}