/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.droolog.examples.v4;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static org.drools.droolog.examples.v4.Person_.*;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
public class MonomorphicCallSiteBenchmark {


    @Benchmark
    public Object v4() {
        Person p1 = new Person("Paul", new Address(null, "Liverpool"));
        org.drools.droolog.meta.lib4.Structure.Meta<Person> meta = p1.meta();
        return meta.structure().valueAt(p1, name);
    }

    @Benchmark
    public Object v2() {
        Person p1 = new Person("Paul", new Address(null, "Liverpool"));
        org.drools.droolog.meta.lib4.Structure.Meta<Person> meta = p1.meta();
        return meta.structure().valueAt(p1, name);
    }



    public static void main(String[] args) throws RunnerException {
        runBenchmark(MonomorphicCallSiteBenchmark.class);
    }

    public static void runBenchmark(Class<?> benchmarkClass) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(benchmarkClass.getSimpleName())
                .addProfiler(LinuxPerfAsmProfiler.class)
                .build();
        new Runner(opt).run();
    }

}