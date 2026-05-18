package converter;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BenchmarkRmi {

    private static final int[] DEFAULT_CONCURRENCY = new int[] {1, 2, 4, 8, 16};
    private static final String BINDING_NAME = "CalculatorBenchmarkService";

    public static void main(String[] args) throws Exception {
        BenchmarkConfig config = BenchmarkConfig.fromArgs(args);
        int registryPort = findFreePort();
        Registry registry = LocateRegistry.createRegistry(registryPort);
        SilentCalculatorImpl service = new SilentCalculatorImpl();
        boolean bound = false;

        try {
            registry.rebind(BINDING_NAME, service);
            bound = true;

            Registry clientRegistry = LocateRegistry.getRegistry("localhost", registryPort);
            Calculator stub = (Calculator) clientRegistry.lookup(BINDING_NAME);

            runWarmup(stub, config.warmupRequests);

            forceGc();
            long memoryBefore = usedMemoryBytes();
            LatencyStats sequentialLatency = runSequentialBenchmark(stub, config.sequentialRequests);
            forceGc();
            long memoryAfter = usedMemoryBytes();

            List<ScalabilityPoint> scalability = runScalabilityBenchmark(stub, config.scalabilityRequests,
                    config.concurrencyLevels);
            int cyclomaticComplexity = estimateCyclomaticComplexity(Paths.get(System.getProperty("user.dir"),
                    "src", "main", "java", "converter", "CalculatorImpl.java"));

            printReport("RMI", "add(15, 7)", sequentialLatency, memoryBefore, memoryAfter,
                    cyclomaticComplexity, scalability);
        } finally {
            if (bound) {
                try {
                    registry.unbind(BINDING_NAME);
                } catch (Exception ignored) {
                }
            }
            try {
                UnicastRemoteObject.unexportObject(service, true);
            } catch (Exception ignored) {
            }
            try {
                UnicastRemoteObject.unexportObject(registry, true);
            } catch (Exception ignored) {
            }
        }
    }

    private static void runWarmup(Calculator stub, int warmupRequests) throws Exception {
        for (int i = 0; i < warmupRequests; i++) {
            stub.add(15, 7);
        }
    }

    private static LatencyStats runSequentialBenchmark(Calculator stub, int requestCount) throws Exception {
        List<Long> latencies = new ArrayList<Long>(requestCount);
        for (int i = 0; i < requestCount; i++) {
            long start = System.nanoTime();
            stub.add(15, 7);
            latencies.add(System.nanoTime() - start);
        }
        return LatencyStats.fromNanos(latencies);
    }

    private static List<ScalabilityPoint> runScalabilityBenchmark(final Calculator stub, int totalRequests,
                                                                  int[] concurrencyLevels) throws Exception {
        List<ScalabilityPoint> points = new ArrayList<ScalabilityPoint>(concurrencyLevels.length);

        for (int concurrency : concurrencyLevels) {
            ExecutorService executor = Executors.newFixedThreadPool(concurrency);
            CountDownLatch ready = new CountDownLatch(concurrency);
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(concurrency);
            ConcurrentLinkedQueue<Long> latencies = new ConcurrentLinkedQueue<Long>();
            AtomicReference<Throwable> error = new AtomicReference<Throwable>();
            int baseWork = totalRequests / concurrency;
            int remainder = totalRequests % concurrency;

            for (int worker = 0; worker < concurrency; worker++) {
                final int requestsForWorker = baseWork + (worker < remainder ? 1 : 0);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        ready.countDown();
                        try {
                            startSignal.await();
                            for (int j = 0; j < requestsForWorker; j++) {
                                long start = System.nanoTime();
                                stub.add(15, 7);
                                latencies.add(System.nanoTime() - start);
                            }
                        } catch (Throwable t) {
                            error.compareAndSet(null, t);
                        } finally {
                            done.countDown();
                        }
                    }
                });
            }

            ready.await();
            long benchmarkStart = System.nanoTime();
            startSignal.countDown();
            done.await();
            long elapsedNanos = System.nanoTime() - benchmarkStart;
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);

            if (error.get() != null) {
                throw new RuntimeException("Error durante benchmark de escalabilidad RMI", error.get());
            }

            LatencyStats stats = LatencyStats.fromNanos(new ArrayList<Long>(latencies));
            double throughput = totalRequests / (elapsedNanos / 1_000_000_000.0);
            points.add(new ScalabilityPoint(concurrency, throughput, stats.averageMs(), stats.p95Ms()));
        }
        return points;
    }

    private static int estimateCyclomaticComplexity(Path sourceFile) {
        if (!Files.exists(sourceFile)) {
            return -1;
        }

        try {
            String source = new String(Files.readAllBytes(sourceFile), StandardCharsets.UTF_8);
            source = source.replaceAll("\"(\\\\.|[^\"\\\\])*\"", "\"\"");

            int decisions = 0;
            decisions += countMatches(source, "\\bif\\b");
            decisions += countMatches(source, "\\bfor\\b");
            decisions += countMatches(source, "\\bwhile\\b");
            decisions += countMatches(source, "\\bcase\\b");
            decisions += countMatches(source, "\\bcatch\\b");
            decisions += countMatches(source, "&&");
            decisions += countMatches(source, "\\|\\|");
            decisions += countMatches(source, "\\?");
            return 1 + decisions;
        } catch (IOException e) {
            return -1;
        }
    }

    private static int countMatches(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static int findFreePort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        int port = socket.getLocalPort();
        socket.close();
        return port;
    }

    private static void forceGc() throws InterruptedException {
        System.gc();
        Thread.sleep(120);
    }

    private static long usedMemoryBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static void printReport(String protocol, String operation, LatencyStats latency,
                                    long memoryBefore, long memoryAfter, int cyclomaticComplexity,
                                    List<ScalabilityPoint> scalability) {
        System.out.println("BENCHMARK_PROTOCOL=" + protocol);
        System.out.println("BENCHMARK_OPERATION=" + operation);
        System.out.println(formatMetric("LATENCY_MIN_MS", latency.minMs()));
        System.out.println(formatMetric("LATENCY_AVG_MS", latency.averageMs()));
        System.out.println(formatMetric("LATENCY_P95_MS", latency.p95Ms()));
        System.out.println(formatMetric("LATENCY_P99_MS", latency.p99Ms()));
        System.out.println(formatMetric("LATENCY_MAX_MS", latency.maxMs()));
        System.out.println(formatMetric("MEMORY_BEFORE_MB", bytesToMb(memoryBefore)));
        System.out.println(formatMetric("MEMORY_AFTER_MB", bytesToMb(memoryAfter)));
        System.out.println(formatMetric("MEMORY_DELTA_MB", bytesToMb(memoryAfter - memoryBefore)));
        System.out.println("COMPLEXITY_ALGORITHMIC=O(1)");
        System.out.println("COMPLEXITY_CYCLOMATIC_ESTIMATE=" + cyclomaticComplexity);

        double throughputSingle = -1;
        double throughputMax = -1;
        for (ScalabilityPoint point : scalability) {
            if (point.concurrency == 1) {
                throughputSingle = point.throughputRps;
            }
            throughputMax = point.throughputRps;
            System.out.println(String.format(Locale.US,
                    "SCALABILITY_LEVEL=%d|THROUGHPUT_RPS=%.2f|AVG_MS=%.4f|P95_MS=%.4f",
                    point.concurrency, point.throughputRps, point.avgLatencyMs, point.p95LatencyMs));
        }

        if (throughputSingle > 0 && throughputMax > 0) {
            System.out.println(formatMetric("SCALABILITY_FACTOR", throughputMax / throughputSingle));
        } else {
            System.out.println("SCALABILITY_FACTOR=NA");
        }
    }

    private static String formatMetric(String key, double value) {
        return String.format(Locale.US, "%s=%.6f", key, value);
    }

    private static double bytesToMb(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    private static final class BenchmarkConfig {
        private final int warmupRequests;
        private final int sequentialRequests;
        private final int scalabilityRequests;
        private final int[] concurrencyLevels;

        private BenchmarkConfig(int warmupRequests, int sequentialRequests, int scalabilityRequests,
                                int[] concurrencyLevels) {
            this.warmupRequests = warmupRequests;
            this.sequentialRequests = sequentialRequests;
            this.scalabilityRequests = scalabilityRequests;
            this.concurrencyLevels = concurrencyLevels;
        }

        private static BenchmarkConfig fromArgs(String[] args) {
            int warmup = 300;
            int requests = 3000;
            int scalability = 6000;
            int[] levels = Arrays.copyOf(DEFAULT_CONCURRENCY, DEFAULT_CONCURRENCY.length);

            for (int i = 0; i < args.length; i++) {
                if ("--warmup".equals(args[i]) && i + 1 < args.length) {
                    warmup = Integer.parseInt(args[++i]);
                } else if ("--requests".equals(args[i]) && i + 1 < args.length) {
                    requests = Integer.parseInt(args[++i]);
                } else if ("--scalabilityRequests".equals(args[i]) && i + 1 < args.length) {
                    scalability = Integer.parseInt(args[++i]);
                } else if ("--concurrency".equals(args[i]) && i + 1 < args.length) {
                    levels = parseConcurrencyLevels(args[++i]);
                }
            }

            return new BenchmarkConfig(warmup, requests, scalability, levels);
        }

        private static int[] parseConcurrencyLevels(String value) {
            String[] tokens = value.split(",");
            List<Integer> parsed = new ArrayList<Integer>();
            for (String token : tokens) {
                String cleaned = token.trim();
                if (!cleaned.isEmpty()) {
                    parsed.add(Integer.parseInt(cleaned));
                }
            }
            if (parsed.isEmpty()) {
                return Arrays.copyOf(DEFAULT_CONCURRENCY, DEFAULT_CONCURRENCY.length);
            }
            Collections.sort(parsed);
            int[] levels = new int[parsed.size()];
            for (int i = 0; i < parsed.size(); i++) {
                levels[i] = parsed.get(i);
            }
            return levels;
        }
    }

    private static final class ScalabilityPoint {
        private final int concurrency;
        private final double throughputRps;
        private final double avgLatencyMs;
        private final double p95LatencyMs;

        private ScalabilityPoint(int concurrency, double throughputRps, double avgLatencyMs, double p95LatencyMs) {
            this.concurrency = concurrency;
            this.throughputRps = throughputRps;
            this.avgLatencyMs = avgLatencyMs;
            this.p95LatencyMs = p95LatencyMs;
        }
    }

    private static final class LatencyStats {
        private final double minMs;
        private final double avgMs;
        private final double p95Ms;
        private final double p99Ms;
        private final double maxMs;

        private LatencyStats(double minMs, double avgMs, double p95Ms, double p99Ms, double maxMs) {
            this.minMs = minMs;
            this.avgMs = avgMs;
            this.p95Ms = p95Ms;
            this.p99Ms = p99Ms;
            this.maxMs = maxMs;
        }

        private static LatencyStats fromNanos(List<Long> latenciesNanos) {
            if (latenciesNanos.isEmpty()) {
                return new LatencyStats(0, 0, 0, 0, 0);
            }

            Collections.sort(latenciesNanos);
            long min = latenciesNanos.get(0);
            long max = latenciesNanos.get(latenciesNanos.size() - 1);
            long p95 = percentile(latenciesNanos, 95);
            long p99 = percentile(latenciesNanos, 99);
            double sum = 0.0;
            for (Long value : latenciesNanos) {
                sum += value;
            }
            double avg = sum / latenciesNanos.size();
            return new LatencyStats(nanosToMs(min), nanosToMs(avg), nanosToMs(p95), nanosToMs(p99), nanosToMs(max));
        }

        private static long percentile(List<Long> sorted, int percentile) {
            int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
            index = Math.max(0, Math.min(index, sorted.size() - 1));
            return sorted.get(index);
        }

        private static double nanosToMs(double nanos) {
            return nanos / 1_000_000.0;
        }

        private double minMs() {
            return minMs;
        }

        private double averageMs() {
            return avgMs;
        }

        private double p95Ms() {
            return p95Ms;
        }

        private double p99Ms() {
            return p99Ms;
        }

        private double maxMs() {
            return maxMs;
        }
    }

    private static final class SilentCalculatorImpl extends UnicastRemoteObject implements Calculator {
        private SilentCalculatorImpl() throws java.rmi.RemoteException {
            super();
        }

        public double multiply(double a, double b) {
            return a * b;
        }

        public double divide(double a, double b) {
            if (b == 0) {
                throw new ArithmeticException("No se puede dividir entre 0");
            }
            return a / b;
        }

        public double power(double a, double b) {
            return Math.pow(a, b);
        }

        public double add(double a, double b) {
            return a + b;
        }

        public double subtract(double a, double b) {
            return a - b;
        }

        public double sqrt(double a) {
            if (a < 0) {
                throw new ArithmeticException("No se puede calcular la raiz de un numero negativo");
            }
            return Math.sqrt(a);
        }

        public double modulo(double a, double b) {
            return a % b;
        }
    }
}
