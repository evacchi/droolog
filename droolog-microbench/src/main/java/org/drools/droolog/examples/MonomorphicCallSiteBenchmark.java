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

import org.drools.droolog.examples.v2.AddressMeta;
import org.drools.droolog.examples.v2.PersonMeta;
import org.drools.droolog.examples.v2.PersonObject;
import org.drools.droolog.examples.v2.Phone;
import org.drools.droolog.examples.v2.PhoneMeta;
import org.drools.droolog.examples.v3.Address;
import org.drools.droolog.examples.v3.Person;
import org.drools.droolog.meta.lib.v2.Term.Atom;
import org.drools.droolog.meta.lib.v2.Term.Structure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.LinuxPerfProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static org.drools.droolog.meta.lib.v2.Term.atom;
import static org.drools.droolog.meta.lib.v2.Term.variable;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
@Fork(2)
public class MonomorphicCallSiteBenchmark {

    static final int L1_CACHE_SIZE = 32000;
    static final int PTR_SIZE = 4;
    static final int NUM_REFS = L1_CACHE_SIZE / PTR_SIZE;

    Person v3 = new Person("Paul",
                           new Address(null, "Liverpool"),
                           new org.drools.droolog.examples.v3.Phone("123-123-123"));

    org.drools.droolog.meta.lib.v3.Structure.Factory[] v3s =
            new org.drools.droolog.meta.lib.v3.Structure.Factory[NUM_REFS * 3];

    Atom<String> paul = atom("Paul");
    PersonMeta person = PersonMeta.Instance;
    AddressMeta address = AddressMeta.Instance;
    PhoneMeta phone = PhoneMeta.Instance;

    Structure<org.drools.droolog.examples.v2.Address> liverpool =
            address.of(variable(), atom("liverpool"));
    Structure<Phone> number = phone.of(atom("123-123-123"));
    Structure<PersonObject> p1 = person.of(paul, liverpool, number);

    Structure[] v2s = new Structure[NUM_REFS * 3];

    @Setup
    public void init() {
        for (int i = 0; i < NUM_REFS*3; i+=3) {
            v3s[i] = v3.meta().structure();
            v3s[i+1] = v3.address().meta().structure();
            v3s[i+2] = v3.phone().meta().structure();
            v2s[i] = p1;
            v2s[i+1] = liverpool;
            v2s[i+2] = number;
        }
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public void v2(Blackhole bh) {
        for (int i = 0; i < NUM_REFS * 2; i+=2) {
            bh.consume(v2s[i].term(0));
            bh.consume(v2s[i+1].term(0));
            bh.consume(v2s[i+2].term(0));
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public void v3(Blackhole bh) {
        for (int i = 0; i < NUM_REFS * 3; i+=3) {
            bh.consume(v3s[i].valueAt(v3, 0));
            bh.consume(v3s[i+1].valueAt(v3.address(), 0));
            bh.consume(v3s[i+2].valueAt(v3.phone(), 0));
        }
    }



    public static void main(String[] args) throws RunnerException {
        runBenchmark(MonomorphicCallSiteBenchmark.class);
    }

    public static void runBenchmark(Class<?> benchmarkClass) throws RunnerException {
        final Options opt = new OptionsBuilder()
                .include(benchmarkClass.getSimpleName())
                .addProfiler(LinuxPerfProfiler.class)
                .build();
        new Runner(opt).run();
    }

}