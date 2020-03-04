# Simple cache example

This is an example for a simple in memory concurrent cache.

## Usage

```java

ExpireCache<String, Integer> cache = new ExpireCache<>();
cache.put("a", 1);

int a = cache.get("a");

// remove "a" from cache
cache.delete("a");

// set a key that will expire in 1 second
cache.put("b", 2, 1, TimeUnit.SECONDS);

int b = cache.get("b");

// cleanout any resources used by cache.
cache.close();

```
## Thread safety

Each instance of ExpireCache is threadsafe.

## Enhancements left out

  * Monitoring metrics
  * Expire on write, on read settings

## Performance

The expire cache performance is dependant on its storage.  
The storage used here is the Java `ConcurrentHasMap`, which as a hashmap has a constant lookup time `O(1)`

The default expire thread does a linear search over all cache instances in `O(n)`.  
This means if we have 2 cache instances of each 5K entries, the expire thread will go over all 10K entries one
at a time. In practice for a cache where accurate cache expires are not required this should not be an issue.  


JMH Benchmarks:  

```
Benchmark                                           Mode     Cnt   Score    Error  Units
BenchmarkTests.benchmarkGet                       sample  117612  ≈ 10⁻⁴           ms/op
BenchmarkTests.benchmarkGet:benchmarkGet·p0.00    sample          ≈ 10⁻⁶           ms/op
BenchmarkTests.benchmarkGet:benchmarkGet·p0.50    sample          ≈ 10⁻⁴           ms/op
BenchmarkTests.benchmarkGet:benchmarkGet·p0.90    sample          ≈ 10⁻⁴           ms/op
BenchmarkTests.benchmarkGet:benchmarkGet·p0.95    sample          ≈ 10⁻⁴           ms/op
BenchmarkTests.benchmarkGet:benchmarkGet·p0.99    sample          ≈ 10⁻³           ms/op
BenchmarkTests.benchmarkGet:benchmarkGet·p0.999   sample           0.007           ms/op
BenchmarkTests.benchmarkGet:benchmarkGet·p0.9999  sample           0.029           ms/op
BenchmarkTests.benchmarkGet:benchmarkGet·p1.00    sample           0.695           ms/op
BenchmarkTests.benchmarkPut                       sample   91054  ≈ 10⁻⁴           ms/op
BenchmarkTests.benchmarkPut:benchmarkPut·p0.00    sample          ≈ 10⁻⁵           ms/op
BenchmarkTests.benchmarkPut:benchmarkPut·p0.50    sample          ≈ 10⁻⁴           ms/op
BenchmarkTests.benchmarkPut:benchmarkPut·p0.90    sample          ≈ 10⁻⁴           ms/op
BenchmarkTests.benchmarkPut:benchmarkPut·p0.95    sample          ≈ 10⁻⁴           ms/op
BenchmarkTests.benchmarkPut:benchmarkPut·p0.99    sample          ≈ 10⁻³           ms/op
BenchmarkTests.benchmarkPut:benchmarkPut·p0.999   sample           0.013           ms/op
BenchmarkTests.benchmarkPut:benchmarkPut·p0.9999  sample           0.020           ms/op
BenchmarkTests.benchmarkPut:benchmarkPut·p1.00    sample           0.049           ms/op
```