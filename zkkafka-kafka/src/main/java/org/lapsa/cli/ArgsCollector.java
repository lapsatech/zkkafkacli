package org.lapsa.cli;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ArgsCollector<INTERMEDIATE, TARGET> {

  public interface Option<INTERMEDIATE> {

    boolean shouldHaveValue();

    String longName();

    char oneLetterName();

    BiConsumer<INTERMEDIATE, String> optionConsumer();
  }

  Supplier<INTERMEDIATE> supplier();

  Iterable<? extends Option<INTERMEDIATE>> options();

  BiConsumer<INTERMEDIATE, String> argConsumer();

  Function<INTERMEDIATE, TARGET> finisher();
}