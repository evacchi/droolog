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

package org.drools.droolog.examples;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.drools.droolog.examples.v2.Address;
import org.drools.droolog.examples.v2.AddressObject;
import org.drools.droolog.examples.v2.Person;
import org.drools.droolog.examples.v2.PersonObject;
import org.drools.droolog.examples.v2.Phone;
import org.drools.droolog.examples.v2.PhoneObject;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.LinuxPerfProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
public class LambdasVsSwitch {

    static final int L1_CACHE_SIZE = 32000;
    static final int PTR_SIZE = 4;
    static final int NUM_REFS = L1_CACHE_SIZE / PTR_SIZE;

    public LambdasVsSwitch() {
        try {
            handles = new VarHandle[]{
                    MethodHandles.lookup().findVarHandle(p.getClass(), "name", String.class),
                    MethodHandles.lookup().findVarHandle(p.getClass(), "address", Address.class)
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    Object valueAtSwitch(Person p, int index) {
        switch (index) {
            case 0:
                return p.name();
            case 1:
                return p.address();
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    final Function<Person, Object>[] lambdas = new Function[]{
            p -> ((Person) p).name(),
            p -> ((Person) p).address()
    };

    Object valueAtLambda(Person p, int index) {
        return lambdas[index].apply(p);
    }

    Person p = new PersonObject("Paul",
                                new AddressObject(null, "Liverpool"),
                                new PhoneObject("123-123-123"));

    final VarHandle[] handles;

    Object valueAtHandle(Person p, int index) {
        return handles[index].get(p);
    }


    @Setup
    public void init() {
    }

    @Benchmark
    public void lambdas(Blackhole bh) {
        bh.consume(valueAtLambda(p, 0));
        bh.consume(valueAtLambda(p, 1));
    }

    @Benchmark
    public void handle(Blackhole bh) {
        bh.consume(valueAtHandle(p, 0));
        bh.consume(valueAtHandle(p, 1));
    }

    @Benchmark
    public void switchTable(Blackhole bh) {
        bh.consume(valueAtSwitch(p, 0));
        bh.consume(valueAtSwitch(p, 1));
    }

    public static void main(String[] args) throws RunnerException {
        runBenchmark(LambdasVsSwitch.class);
    }

    public static void runBenchmark(Class<?> benchmarkClass) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(benchmarkClass.getSimpleName())
                .addProfiler(LinuxPerfProfiler.class)
                .build();
        new Runner(opt).run();
    }
}