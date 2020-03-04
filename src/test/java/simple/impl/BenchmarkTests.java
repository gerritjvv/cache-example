package simple.impl;

import org.junit.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BenchmarkTests {
    @Test
    public void launchBenchmark() throws Exception {

        Options opt = new OptionsBuilder()
                // Specify which benchmarks to run.
                // You can be more specific if you'd like to run only one benchmark per test.
                .include(this.getClass().getName() + ".*")
                // Set the following options as needed
                .mode(Mode.SampleTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementTime(TimeValue.seconds(1))
                .measurementIterations(2)
                .threads(2)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                //.jvmArgs("-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining")
                //.addProfiler(WinPerfAsmProfiler.class)
                .build();

        new Runner(opt).run();
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Thread)
    public static class BenchmarkState {
        ExpireCache<String, String> cache;
        String VAL = "DEFAULT VALUE";
        int i;

        @Setup(Level.Trial)
        public void initialize() {

            cache = new ExpireCache<>();

            i = new Random().nextInt(100000);

            // set 10 million objects
            for (int i = 0; i < 10000000; i++) {
                cache.put(String.valueOf(i), VAL);
            }
        }
    }

    @Benchmark
    public void benchmarkPut(BenchmarkState state, Blackhole bh) {

        state.cache.put(String.valueOf(1), state.VAL);

    }

    @Benchmark
    public void benchmarkGet(BenchmarkState state, Blackhole bh) {
        bh.consume(state.cache.get(String.valueOf(state.i), state.VAL));
    }
}
