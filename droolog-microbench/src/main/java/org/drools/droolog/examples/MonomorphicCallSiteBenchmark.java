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
import org.drools.droolog.examples.v4.Address;
import org.drools.droolog.examples.v4.Person;
import org.drools.droolog.meta.lib2.Term;
import org.drools.droolog.meta.lib2.Term.Atom;
import org.drools.droolog.meta.lib2.Term.Structure;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

import static org.drools.droolog.meta.lib2.Term.atom;
import static org.drools.droolog.meta.lib2.Term.variable;

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

    Person v4 = new Person("Paul",
                           new Address(null, "Liverpool"),
                           new org.drools.droolog.examples.v4.Phone("123-123-123"));

    org.drools.droolog.meta.lib4.Structure.Factory[] structures = new org.drools.droolog.meta.lib4.Structure.Factory[NUM_REFS * 3];

    Atom<String> paul = atom("Paul");
    PersonMeta person = PersonMeta.Instance;
    AddressMeta address = AddressMeta.Instance;
    PhoneMeta phone = PhoneMeta.Instance;

    Structure<org.drools.droolog.examples.v2.Address> liverpool =
            address.of(variable(), atom("liverpool"));
    Structure<Phone> number = phone.of(atom("123-123-123"));
    Structure<PersonObject> p1 = person.of(paul, liverpool, number);

    Structure[] structures2 = new Structure[NUM_REFS * 3];

    @Setup
    public void init() {
        for (int i = 0; i < NUM_REFS*3; i+=3) {
            structures[i] = v4.meta().structure();
            structures[i+1] = v4.address().meta().structure();
            structures[i+2] = v4.phone().meta().structure();
            structures2[i] = p1;
            structures2[i+1] = liverpool;
            structures2[i+2] = number;
        }
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public void v4(Blackhole bh) {
        for (int i = 0; i < NUM_REFS * 3; i+=3) {
            bh.consume(structures[i].valueAt(v4, 0));
            bh.consume(structures[i+1].valueAt(v4.address(), 0));
            bh.consume(structures[i+2].valueAt(v4.phone(), 0));
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.EXCLUDE)
    public void v2(Blackhole bh) {
        for (int i = 0; i < NUM_REFS * 2; i+=2) {
            bh.consume(structures2[i].term(0));
            bh.consume(structures2[i+1].term(0));
            bh.consume(structures2[i+2].term(0));
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