package org.lapsa.cli;

@SuppressWarnings("serial")
public class ArgsValidationException extends RuntimeException {

  public ArgsValidationException(String message) {
    super(message);
  }
}