/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package org.mikeneck.duration;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
        name = "duration",
        mixinStandardHelpOptions = true,
        resourceBundle = "Duration",
        version = {
                "duration",
                "  Version: ${bundle:app.version:-develop}",
                "  Build OS: ${os.name}, ${os.version}, ${os.arch}",
                "  Picocli: " + CommandLine.VERSION
        },
        description = {"Converts an input date into duration string from now."})
public class App implements Callable<Integer> {

    private final Clock clock;

    @CommandLine.Parameters(arity = "1", description = {"datetime string"})
    String dateTimeString;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public App(Clock clock) {
        this.clock = clock;
    }

    public static void main(String[] args) {
        Clock clock = Clock.systemUTC();
        int exitCode = new CommandLine(new App(clock)).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        OffsetDateTime now = Instant.now(clock).atOffset(ZoneOffset.UTC);
        Either<String, OffsetDateTime> dateTime = datetime();
        if (dateTime.isLeft()) {
            System.err.println(dateTime.error());
            return 1;
        }
        Duration duration = Duration.between(now, dateTime.get());
        System.out.println(duration.toString());
        return 0;
    }

    private Either<String, OffsetDateTime> datetime() {
        try {
            OffsetDateTime dateTime = OffsetDateTime.parse(dateTimeString, formatter);
            return Either.right(dateTime);
        } catch (DateTimeException e) {
            return Either.left(String.format("invalid parameter: %s", e.getMessage()));
        }
    }
}
