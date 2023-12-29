package kafka;

import java.io.Console;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Throwables;

public class ConsoleHelper implements AutoCloseable {

  private final ExecutorService threadpool = Executors.newSingleThreadExecutor();

  public boolean shallContinue(int trueAfterTimeoutMilis) {
    Console c = System.console();

    CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
        () -> {
          c.printf("Next? (Y/n)\n");
          String s = c.readLine();
          if ("n".equals(s)) {
            return false;
          }
          return true;
        },
        threadpool);

    try {
      return future.get(trueAfterTimeoutMilis, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    } catch (ExecutionException e) {
      Throwables.throwIfUnchecked(e.getCause());
      throw new RuntimeException(e.getCause());
    } catch (TimeoutException e) {
      return true;
    }
  }

  @Override
  public void close() throws Exception {
    threadpool.shutdown();
    if (!threadpool.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
      threadpool.shutdownNow();
    }
  }

}