package org.lapsa.cli;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.lapsa.cli.ArgsCollector.Option;

public final class SimpleArgsParser {

  public static <P> BiConsumer<P, String> toInteger(BiConsumer<P, Integer> intConsumer) {
    Collectors.joining();
    return (p, s) -> {
      Integer i;
      try {
        i = Integer.valueOf(s);
      } catch (NumberFormatException e) {
        throw new ArgsValidationException("Got " + s + " but integer value is expected");
      }
      intConsumer.accept(p, i);
    };
  }

  public static <P> BiConsumer<P, String> toBoolean(BiConsumer<P, Boolean> intConsumer) {
    return toBoolean(intConsumer, true);
  }

  public static <P> BiConsumer<P, String> toBoolean(BiConsumer<P, Boolean> intConsumer, boolean value) {
    return (p, s) -> intConsumer.accept(p, value);
  }

  public static <INTERMEDIATE, TARGET> TARGET parse(
      ArgsCollector<INTERMEDIATE, TARGET> argCollector,
      String... args) {

    LinkedList<String> inputArgsQueue = new LinkedList<>(Arrays.asList(args));
    LinkedList<String> argsQueue = new LinkedList<>();

    while (!inputArgsQueue.isEmpty()) {
      String v = inputArgsQueue.remove();
      if (v.startsWith("-") && !v.startsWith("--") && v.length() > 2) {
        char oneLetterArg = v.charAt(1);
        argsQueue.addLast("-" + oneLetterArg);
        argsQueue.addLast(v.substring(2));
      } else {
        argsQueue.addLast(v);
      }
    }

    final INTERMEDIATE p = argCollector.supplier().get();
    while (!argsQueue.isEmpty()) {
      final String arg = argsQueue.remove();

      final Optional<? extends Option<INTERMEDIATE>> optionFound;

      if (arg.equals("--")) {
        break;
      } else if (arg.startsWith("--")) {
        optionFound = argCollector.options() == null
            ? Optional.empty()
            : StreamSupport.stream(argCollector.options().spliterator(), false)
                .filter(o -> o.longName() != null)
                .filter(o -> ("--" + o.longName()).equals(arg))
                .findFirst();
      } else if (arg.startsWith("-")) {
        if (arg.length() != 2) {
          throw new ArgsValidationException("Invalid option/argument " + arg);
        }
        char argc = arg.charAt(1);
        optionFound = argCollector.options() == null
            ? Optional.empty()
            : StreamSupport.stream(argCollector.options().spliterator(), false)
                .filter(o -> o.oneLetterName() != 0)
                .filter(o -> o.oneLetterName() == argc)
                .findFirst();
      } else {
        if (argCollector.argConsumer() == null) {
          throw new ArgsValidationException("Invalid argument " + arg);
        }
        argCollector.argConsumer().accept(p, arg);
        continue;
      }

      final Option<INTERMEDIATE> option = optionFound
          .orElseThrow(() -> new ArgsValidationException("Invalid option " + arg));

      if (option.shouldHaveValue()) {
        final String value;
        try {
          value = argsQueue.remove();
        } catch (NoSuchElementException e) {
          throw new ArgsValidationException(arg + " option value is required");
        }
        option.optionConsumer().accept(p, value);
      } else {
        option.optionConsumer().accept(p, null);
      }
    }

    if (!argsQueue.isEmpty()) {
      if (argCollector.argConsumer() == null) {
        throw new ArgsValidationException("Invalid argument " + argsQueue.stream().collect(Collectors.joining(" ")));
      }
      argsQueue.forEach(s -> argCollector.argConsumer().accept(p, s));
    }

    TARGET t;
    try {
      t = argCollector.finisher().apply(p);
    } catch (RuntimeException e) {
      throw new ArgsValidationException(e.getMessage());
    }

    return t;
  }
}