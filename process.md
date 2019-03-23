1) sbt clean assembly

2) java -agentpath:${HOME}/.local/share/graalvm-ce-1.0.0-rc14/jre/lib/amd64/libnative-image-agent.so=output=trace.json -jar target/scala-2.12/bot.jar

3) native-image --tool:native-image-configure

4) ./native-image-configure process-trace --output-dir=native/config/ trace.json

5)
WARNING: Error processing log entry: java.lang.NullPointerException: {caller_class=com.mysql.cj.util.TimeUtil, result=true, args=[nanoTime, null], function=getMethod, tracer=reflect, class=java.lang.System}

6)
native-image -J-Xmx8G -H:+ReportExceptionStackTraces \
             -H:ReflectionConfigurationFiles=native/config/reflect-config.json \
             -H:ResourceConfigurationFiles=native/config/resource-config.json                                    \
             -H:DynamicProxyConfigurationFiles=native/config/proxy-config.json                                    \
             --enable-http --enable-https --verbose --no-server -jar ./target/scala-2.12/bot.jar

Error: Detected a started Thread in the image heap. Threads running in the image generator are no longer running at image run time. The object was probably created by a class initializer and is reachable from a static field. By default, all class initialization is done during native image building.You can manually delay class initialization to image run time by using the option --delay-class-initialization-to-runtime=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
Detailed message:
Error: Detected a started Thread in the image heap. Threads running in the image generator are no longer running at image run time. The object was probably created by a class initializer and is reachable from a static field. By default, all class initialization is done during native image building.You can manually delay class initialization to image run time by using the option --delay-class-initialization-to-runtime=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
Trace:  field com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.threadRef

com.oracle.svm.core.util.UserError$UserException: Detected a started Thread in the image heap. Threads running in the image generator are no longer running at image run time. The object was probably created by a class initializer and is reachable from a static field. By default, all class initialization is done during native image building.You can manually delay class initialization to image run time by using the option --delay-class-initialization-to-runtime=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
Detailed message:
Error: Detected a started Thread in the image heap. Threads running in the image generator are no longer running at image run time. The object was probably created by a class initializer and is reachable from a static field. By default, all class initialization is done during native image building.You can manually delay class initialization to image run time by using the option --delay-class-initialization-to-runtime=<class-name>. Or you can write your own initialization methods and call them explicitly from your main entry point.
Trace:  field com.mysql.cj.jdbc.AbandonedConnectionCleanupThread.threadRef

        at com.oracle.svm.core.util.UserError.abort(UserError.java:67)
        at com.oracle.svm.hosted.NativeImageGenerator.runPointsToAnalysis(NativeImageGenerator.java:708)
        at com.oracle.svm.hosted.NativeImageGenerator.doRun(NativeImageGenerator.java:498)
        at com.oracle.svm.hosted.NativeImageGenerator.lambda$run$0(NativeImageGenerator.java:416)
        at java.util.concurrent.ForkJoinTask$AdaptedRunnableAction.exec(ForkJoinTask.java:1386)
        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
        at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
        at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
Error: Image building with exit status 1

7)

native-image -J-Xmx8G -H:+ReportExceptionStackTraces \
             -H:ReflectionConfigurationFiles=native/config/reflect-config.json \
             -H:ResourceConfigurationFiles=native/config/resource-config.json                                    \
             -H:DynamicProxyConfigurationFiles=native/config/proxy-config.json                                    \
             --delay-class-initialization-to-runtime="com.mysql.cj.jdbc.AbandonedConnectionCleanupThread" \
             --enable-http --enable-https --verbose --no-server -jar ./target/scala-2.12/bot.jar

Error: Class that is marked for delaying initialization to run time got initialized during image building: com.mysql.cj.jdbc.AbandonedConnectionCleanupThread
com.oracle.svm.core.util.UserError$UserException: Class that is marked for delaying initialization to run time got initialized during image building: com.mysql.cj.jdbc.AbandonedConnectionCleanupThread


8)
--debug-attach

native-image -J-Xmx8G -H:+ReportExceptionStackTraces\
             -H:ReflectionConfigurationFiles=native/config/reflect-config.json \
             -H:ResourceConfigurationFiles=native/config/resource-config.json                                    \
             -H:DynamicProxyConfigurationFiles=native/config/proxy-config.json                                    \
             --delay-class-initialization-to-runtime="com.mysql.cj.jdbc.AbandonedConnectionCleanupThread, \
             com.mysql.cj.jdbc.NonRegisteringDriver,com.mysql.cj.jdbc.ConnectionImpl,com.mysql.cj.jdbc.MysqlDataSource,
             java.sql.DriverManager" \
             --enable-http --enable-https --verbose --no-server -jar ./target/scala-2.12/bot.jar

Warning: RecomputeFieldValue.FieldOffset automatic substitution failed. The automatic substitution registration was attempted because a call to sun.misc.Unsafe.objectFieldOffset(Field) was detected in the static initializer of akka.actor.LightArrayRevolverScheduler$. Detailed failure reason(s): The field akka.actor.LightArrayRevolverScheduler$.akka$actor$LightArrayRevolverScheduler$$taskOffset, where the value produced by the field offset computation is stored, is not static.
[bot:13137]     analysis:  44,203.02 ms
Error: Error encountered while parsing akka.actor.LightArrayRevolverScheduler$TaskHolder.extractTask(java.lang.Runnable)
Parsing context:
        parsing akka.actor.LightArrayRevolverScheduler$TaskHolder.cancel(LightArrayRevolverScheduler.scala:339)
        parsing akka.actor.LightArrayRevolverScheduler.akka$actor$LightArrayRevolverScheduler$$schedule(LightArrayRevolverScheduler.scala:173)
        parsing akka.actor.LightArrayRevolverScheduler.scheduleOnce(LightArrayRevolverScheduler.scala:134)
        parsing akka.dispatch.MessageDispatcher.akka$dispatch$MessageDispatcher$$scheduleShutdownAction(AbstractDispatcher.scala:174)
        parsing akka.dispatch.MessageDispatcher$$anon$3.run(AbstractDispatcher.scala:224)
        parsing java.lang.Shutdown.runHooks(Shutdown.java:123)
        parsing java.lang.Shutdown.sequence(Shutdown.java:167)
        parsing java.lang.Shutdown.shutdown(Shutdown.java:234)
        parsing com.oracle.svm.core.jdk.RuntimeSupport.shutdown(RuntimeSupport.java:181)
        parsing com.oracle.svm.core.JavaMainWrapper.run(JavaMainWrapper.java:177)
        parsing com.oracle.svm.core.code.IsolateEnterStub.JavaMainWrapper_run_5087f5482cc9a6abc971913ece43acb471d2631b(generated:0)

com.oracle.graal.pointsto.util.AnalysisError$ParsingError: Error encountered while parsing akka.actor.LightArrayRevolverScheduler$TaskHolder.extractTask(java.lang.Runnable)
Parsing context:
        parsing akka.actor.LightArrayRevolverScheduler$TaskHolder.cancel(LightArrayRevolverScheduler.scala:339)
        parsing akka.actor.LightArrayRevolverScheduler.akka$actor$LightArrayRevolverScheduler$$schedule(LightArrayRevolverScheduler.scala:173)
        parsing akka.actor.LightArrayRevolverScheduler.scheduleOnce(LightArrayRevolverScheduler.scala:134)
        parsing akka.dispatch.MessageDispatcher.akka$dispatch$MessageDispatcher$$scheduleShutdownAction(AbstractDispatcher.scala:174)
        parsing akka.dispatch.MessageDispatcher$$anon$3.run(AbstractDispatcher.scala:224)
        parsing java.lang.Shutdown.runHooks(Shutdown.java:123)
        parsing java.lang.Shutdown.sequence(Shutdown.java:167)
        parsing java.lang.Shutdown.shutdown(Shutdown.java:234)
        parsing com.oracle.svm.core.jdk.RuntimeSupport.shutdown(RuntimeSupport.java:181)
        parsing com.oracle.svm.core.JavaMainWrapper.run(JavaMainWrapper.java:177)
        parsing com.oracle.svm.core.code.IsolateEnterStub.JavaMainWrapper_run_5087f5482cc9a6abc971913ece43acb471d2631b(generated:0)

        at com.oracle.graal.pointsto.util.AnalysisError.parsingError(AnalysisError.java:138)
        at com.oracle.graal.pointsto.flow.MethodTypeFlow.doParse(MethodTypeFlow.java:327)
        at com.oracle.graal.pointsto.flow.MethodTypeFlow.ensureParsed(MethodTypeFlow.java:300)
        at com.oracle.graal.pointsto.flow.MethodTypeFlow.addContext(MethodTypeFlow.java:107)
        at com.oracle.graal.pointsto.flow.SpecialInvokeTypeFlow.onObservedUpdate(InvokeTypeFlow.java:421)
        at com.oracle.graal.pointsto.flow.TypeFlow.notifyObservers(TypeFlow.java:352)
        at com.oracle.graal.pointsto.flow.TypeFlow.update(TypeFlow.java:394)
        at com.oracle.graal.pointsto.BigBang$2.run(BigBang.java:508)
        at com.oracle.graal.pointsto.util.CompletionExecutor.lambda$execute$0(CompletionExecutor.java:169)
        at java.util.concurrent.ForkJoinTask$RunnableExecuteAction.exec(ForkJoinTask.java:1402)
        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
        at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
        at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
Caused by: com.oracle.svm.hosted.analysis.flow.SVMMethodTypeFlowBuilder$UnsafeOffsetError: Field AnalysisField<LightArrayRevolverScheduler$.akka$actor$LightArrayRevolverScheduler$$taskOffset accessed: false reads: true written: false> is used as an offset in an unsafe operation, but no value recomputation found.
 Wrapped field: HotSpotResolvedJavaFieldImpl<akka.actor.LightArrayRevolverScheduler$.akka$actor$LightArrayRevolverScheduler$$taskOffset long:16>
 Location: at akka.actor.LightArrayRevolverScheduler$TaskHolder.extractTask(LightArrayRevolverScheduler.scala:321) [bci: 113]
        at com.oracle.svm.hosted.analysis.flow.SVMMethodTypeFlowBuilder$UnsafeOffsetError.report(SVMMethodTypeFlowBuilder.java:119)
        at com.oracle.svm.hosted.analysis.flow.SVMMethodTypeFlowBuilder.checkUnsafeOffset(SVMMethodTypeFlowBuilder.java:161)
        at com.oracle.graal.pointsto.flow.MethodTypeFlowBuilder$NodeIterator.node(MethodTypeFlowBuilder.java:1088)
        at org.graalvm.compiler.phases.graph.PostOrderNodeIterator.apply(PostOrderNodeIterator.java:106)
        at com.oracle.graal.pointsto.flow.MethodTypeFlowBuilder.apply(MethodTypeFlowBuilder.java:413)
        at com.oracle.graal.pointsto.flow.MethodTypeFlow.doParse(MethodTypeFlow.java:310)
        ... 12 more
Error: Image building with exit status 1


do substitution for LightArray

9)


native-image -J-Xmx8G -H:+ReportExceptionStackTraces\
             -H:ReflectionConfigurationFiles=native/config/reflect-config.json \
             -H:ResourceConfigurationFiles=native/config/resource-config.json                                    \
             -H:DynamicProxyConfigurationFiles=native/config/proxy-config.json                                    \
             --delay-class-initialization-to-runtime="com.mysql.cj.jdbc.AbandonedConnectionCleanupThread, \
             com.mysql.cj.jdbc.NonRegisteringDriver,com.mysql.cj.jdbc.ConnectionImpl,com.mysql.cj.jdbc.MysqlDataSource,
             java.sql.DriverManager" \
             --enable-http --enable-https --verbose --no-server -jar ./target/scala-2.12/bot.jar


Error: com.oracle.graal.pointsto.constraints.UnresolvedElementException: Discovered unresolved type during parsing: com.codahale.metrics.MetricRegistry. To diagnose the issue you can use the --allow-incomplete-classpath option. The missing type is then reported at run time when it is accessed the first time.
Trace:
        at parsing com.zaxxer.hikari.pool.HikariPool.setMetricRegistry(HikariPool.java:284)
Call path from entry point to com.zaxxer.hikari.pool.HikariPool.setMetricRegistry(Object):
        at com.zaxxer.hikari.pool.HikariPool.setMetricRegistry(HikariPool.java:283)
        at com.zaxxer.hikari.HikariDataSource.setMetricRegistry(HikariDataSource.java:245)
        at com.oracle.svm.reflect.HikariConfig_setMetricRegistry_4b823f043641c9c9ffcd28679ba7be1dab4004ea_282.invoke(Unknown Source)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at com.mysql.cj.jdbc.ha.MultiHostConnectionProxy$JdbcInterfaceProxy.invoke(MultiHostConnectionProxy.java:105)
        at com.sun.proxy.$Proxy109.hashCode(Unknown Source)
        at java.util.HashMap.hash(HashMap.java:339)
        at java.util.HashMap.get(HashMap.java:557)
        at com.oracle.svm.jni.access.JNIReflectionDictionary.getFieldNameByID(JNIReflectionDictionary.java:260)
        at com.oracle.svm.jni.functions.JNIFunctions.ToReflectedField(JNIFunctions.java:842)
        at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ToReflectedField_80d8233579d5215df0227b770e5c01228a0de9b9(generated:0)
Error: com.oracle.graal.pointsto.constraints.UnresolvedElementException: Discovered unresolved type during parsing: com.codahale.metrics.health.HealthCheckRegistry. To diagnose the issue you can use the --allow-incomplete-classpath option. The missing type is then reported at run time when it is accessed the first time.
Trace:
        at parsing com.zaxxer.hikari.HikariConfig.setHealthCheckRegistry(HikariConfig.java:709)
Call path from entry point to com.zaxxer.hikari.HikariConfig.setHealthCheckRegistry(Object):
        at com.zaxxer.hikari.HikariConfig.setHealthCheckRegistry(HikariConfig.java:704)
        at com.oracle.svm.reflect.HikariConfig_setHealthCheckRegistry_b23383acec28f867e99c3c214a071f5763c9d615_280.invoke(Unknown Source)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at com.mysql.cj.jdbc.ha.MultiHostConnectionProxy$JdbcInterfaceProxy.invoke(MultiHostConnectionProxy.java:105)
        at com.sun.proxy.$Proxy109.hashCode(Unknown Source)
        at java.util.HashMap.hash(HashMap.java:339)
        at java.util.HashMap.get(HashMap.java:557)
        at com.oracle.svm.jni.access.JNIReflectionDictionary.getFieldNameByID(JNIReflectionDictionary.java:260)
        at com.oracle.svm.jni.functions.JNIFunctions.ToReflectedField(JNIFunctions.java:842)
        at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ToReflectedField_80d8233579d5215df0227b770e5c01228a0de9b9(generated:0)
Error: com.oracle.graal.pointsto.constraints.UnresolvedElementException: Discovered unresolved type during parsing: com.codahale.metrics.health.HealthCheckRegistry. To diagnose the issue you can use the --allow-incomplete-classpath option. The missing type is then reported at run time when it is accessed the first time.
Trace:
        at parsing com.zaxxer.hikari.pool.HikariPool.setHealthCheckRegistry(HikariPool.java:318)
Call path from entry point to com.zaxxer.hikari.pool.HikariPool.setHealthCheckRegistry(Object):
        at com.zaxxer.hikari.pool.HikariPool.setHealthCheckRegistry(HikariPool.java:317)
        at com.zaxxer.hikari.HikariDataSource.setHealthCheckRegistry(HikariDataSource.java:281)
        at com.oracle.svm.reflect.HikariConfig_setHealthCheckRegistry_b23383acec28f867e99c3c214a071f5763c9d615_280.invoke(Unknown Source)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at com.mysql.cj.jdbc.ha.MultiHostConnectionProxy$JdbcInterfaceProxy.invoke(MultiHostConnectionProxy.java:105)
        at com.sun.proxy.$Proxy109.hashCode(Unknown Source)
        at java.util.HashMap.hash(HashMap.java:339)
        at java.util.HashMap.get(HashMap.java:557)
        at com.oracle.svm.jni.access.JNIReflectionDictionary.getFieldNameByID(JNIReflectionDictionary.java:260)
        at com.oracle.svm.jni.functions.JNIFunctions.ToReflectedField(JNIFunctions.java:842)
        at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ToReflectedField_80d8233579d5215df0227b770e5c01228a0de9b9(generated:0)

com.oracle.svm.core.util.UserError$UserException: unsupported features in 3 methods
Detailed message:
Error: com.oracle.graal.pointsto.constraints.UnresolvedElementException: Discovered unresolved type during parsing: com.codahale.metrics.MetricRegistry. To diagnose the issue you can use the --allow-incomplete-classpath option. The missing type is then reported at run time when it is accessed the first time.
Trace:
        at parsing com.zaxxer.hikari.pool.HikariPool.setMetricRegistry(HikariPool.java:284)
Call path from entry point to com.zaxxer.hikari.pool.HikariPool.setMetricRegistry(Object):
        at com.zaxxer.hikari.pool.HikariPool.setMetricRegistry(HikariPool.java:283)
        at com.zaxxer.hikari.HikariDataSource.setMetricRegistry(HikariDataSource.java:245)
        at com.oracle.svm.reflect.HikariConfig_setMetricRegistry_4b823f043641c9c9ffcd28679ba7be1dab4004ea_282.invoke(Unknown Source)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at com.mysql.cj.jdbc.ha.MultiHostConnectionProxy$JdbcInterfaceProxy.invoke(MultiHostConnectionProxy.java:105)
        at com.sun.proxy.$Proxy109.hashCode(Unknown Source)
        at java.util.HashMap.hash(HashMap.java:339)
        at java.util.HashMap.get(HashMap.java:557)
        at com.oracle.svm.jni.access.JNIReflectionDictionary.getFieldNameByID(JNIReflectionDictionary.java:260)
        at com.oracle.svm.jni.functions.JNIFunctions.ToReflectedField(JNIFunctions.java:842)
        at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ToReflectedField_80d8233579d5215df0227b770e5c01228a0de9b9(generated:0)
Error: com.oracle.graal.pointsto.constraints.UnresolvedElementException: Discovered unresolved type during parsing: com.codahale.metrics.health.HealthCheckRegistry. To diagnose the issue you can use the --allow-incomplete-classpath option. The missing type is then reported at run time when it is accessed the first time.
Trace:
        at parsing com.zaxxer.hikari.HikariConfig.setHealthCheckRegistry(HikariConfig.java:709)
Call path from entry point to com.zaxxer.hikari.HikariConfig.setHealthCheckRegistry(Object):
        at com.zaxxer.hikari.HikariConfig.setHealthCheckRegistry(HikariConfig.java:704)
        at com.oracle.svm.reflect.HikariConfig_setHealthCheckRegistry_b23383acec28f867e99c3c214a071f5763c9d615_280.invoke(Unknown Source)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at com.mysql.cj.jdbc.ha.MultiHostConnectionProxy$JdbcInterfaceProxy.invoke(MultiHostConnectionProxy.java:105)
        at com.sun.proxy.$Proxy109.hashCode(Unknown Source)
        at java.util.HashMap.hash(HashMap.java:339)
        at java.util.HashMap.get(HashMap.java:557)
        at com.oracle.svm.jni.access.JNIReflectionDictionary.getFieldNameByID(JNIReflectionDictionary.java:260)
        at com.oracle.svm.jni.functions.JNIFunctions.ToReflectedField(JNIFunctions.java:842)
        at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ToReflectedField_80d8233579d5215df0227b770e5c01228a0de9b9(generated:0)
Error: com.oracle.graal.pointsto.constraints.UnresolvedElementException: Discovered unresolved type during parsing: com.codahale.metrics.health.HealthCheckRegistry. To diagnose the issue you can use the --allow-incomplete-classpath option. The missing type is then reported at run time when it is accessed the first time.
Trace:
        at parsing com.zaxxer.hikari.pool.HikariPool.setHealthCheckRegistry(HikariPool.java:318)
Call path from entry point to com.zaxxer.hikari.pool.HikariPool.setHealthCheckRegistry(Object):
        at com.zaxxer.hikari.pool.HikariPool.setHealthCheckRegistry(HikariPool.java:317)
        at com.zaxxer.hikari.HikariDataSource.setHealthCheckRegistry(HikariDataSource.java:281)
        at com.oracle.svm.reflect.HikariConfig_setHealthCheckRegistry_b23383acec28f867e99c3c214a071f5763c9d615_280.invoke(Unknown Source)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at com.mysql.cj.jdbc.ha.MultiHostConnectionProxy$JdbcInterfaceProxy.invoke(MultiHostConnectionProxy.java:105)
        at com.sun.proxy.$Proxy109.hashCode(Unknown Source)
        at java.util.HashMap.hash(HashMap.java:339)
        at java.util.HashMap.get(HashMap.java:557)
        at com.oracle.svm.jni.access.JNIReflectionDictionary.getFieldNameByID(JNIReflectionDictionary.java:260)
        at com.oracle.svm.jni.functions.JNIFunctions.ToReflectedField(JNIFunctions.java:842)
        at com.oracle.svm.core.code.IsolateEnterStub.JNIFunctions_ToReflectedField_80d8233579d5215df0227b770e5c01228a0de9b9(generated:0)

        at com.oracle.svm.core.util.UserError.abort(UserError.java:67)
        at com.oracle.svm.hosted.NativeImageGenerator.runPointsToAnalysis(NativeImageGenerator.java:708)
        at com.oracle.svm.hosted.NativeImageGenerator.doRun(NativeImageGenerator.java:498)
        at com.oracle.svm.hosted.NativeImageGenerator.lambda$run$0(NativeImageGenerator.java:416)
        at java.util.concurrent.ForkJoinTask$AdaptedRunnableAction.exec(ForkJoinTask.java:1386)
        at java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:289)
        at java.util.concurrent.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1056)
        at java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1692)
        at java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:157)
Error: Image building with exit status 1

do hikari substitutions

10)

Exception in thread "main" com.typesafe.config.ConfigException$Missing: No configuration setting found for key 'telegram.token'
        at com.typesafe.config.impl.SimpleConfig.findKeyOrNull(SimpleConfig.java:156)


native-image -J-Xmx8G -H:+ReportExceptionStackTraces\
             -H:ReflectionConfigurationFiles=native/config/reflect-config.json \
             -H:ResourceConfigurationFiles=native/config/resource-config.json                                    \
             -H:DynamicProxyConfigurationFiles=native/config/proxy-config.json                                    \
             --delay-class-initialization-to-runtime="com.mysql.cj.jdbc.AbandonedConnectionCleanupThread, \
             com.mysql.cj.jdbc.NonRegisteringDriver,com.mysql.cj.jdbc.ConnectionImpl,com.mysql.cj.jdbc.MysqlDataSource,
             java.sql.DriverManager" \
             --rerun-class-initialization-at-runtime="com.typesafe.config.impl.ConfigImpl$EnvVariablesHolder,  \
                                 com.typesafe.config.impl.ConfigImpl$SystemPropertiesHolder"        \
             --enable-http --enable-https --verbose --no-server -jar ./target/scala-2.12/bot.jar

rerun-class-initialization

11)

./bot -Djava.library.path=/usr/lib/jvm/default-java/lib


2019-03-24 21:25:34 DEBUG com.zaxxer.hikari.pool.HikariPool - db - Cannot acquire connection from data source
java.sql.SQLNonTransientConnectionException: Cannot connect to MySQL server on localhost:3,306.


native-image -J-Xmx8G -H:+ReportExceptionStackTraces\
             -H:ReflectionConfigurationFiles=native/config/reflect-config.json \
             -H:ResourceConfigurationFiles=native/config/resource-config.json                                    \
             -H:DynamicProxyConfigurationFiles=native/config/proxy-config.json                                    \
             --delay-class-initialization-to-runtime="com.mysql.cj.jdbc.AbandonedConnectionCleanupThread, \
             com.mysql.cj.jdbc.NonRegisteringDriver,com.mysql.cj.jdbc.ConnectionImpl,com.mysql.cj.jdbc.MysqlDataSource, \
             java.sql.DriverManager,com.zaxxer.hikari.pool.HikariPool,com.mysql.cj.jdbc.ConnectionImpl" \
             --rerun-class-initialization-at-runtime="com.typesafe.config.impl.ConfigImpl$EnvVariablesHolder,  \
                                 com.typesafe.config.impl.ConfigImpl$SystemPropertiesHolder,com.typesafe.config.impl.ConfigImpl,slick.jdbc.JdbcBackend$"        \
             --enable-http --enable-https --verbose --no-server -jar ./target/scala-2.12/bot.jar

==========


export TELEGRAM_TOKEN=851233569:AAF36vGZi_-Eg3RLm0cjjT58WvOqevnii60

export HERE_APP_ID=I2sTQZR30zF6sSKV92kd

export HERE_APP_CODE=7kZNaxxBrvp0_c84sbAoSA



12)
invalidate caches!!!

sbt clean assembly

java -agentpath:${HOME}/.local/share/graalvm-ce-1.0.0-rc16/jre/lib/amd64/libnative-image-agent.so -jar target/scala-2.12/bot.jar


native-image -J-Xmx8G -H:+ReportExceptionStackTraces --no-fallback\
            -H:ConfigurationFileDirectories="native-config"   \
            --delay-class-initialization-to-runtime="org.postgresql.sspi.SSPIClient" \
            --enable-http --enable-https --verbose --no-server -jar ./target/scala-2.12/bot.jar

./bot -Djava.library.path=/usr/lib/jvm/default-java/lib


```
2019-05-05 00:28:52 DEBUG com.zaxxer.hikari.pool.HikariPool - db - Cannot acquire connection from data source
java.lang.IllegalArgumentException: Class java.sql.Statement[] is instantiated reflectively but was never registered. Register the class by using org.graalvm.nativeimage.hosted.RuntimeReflection
        at com.oracle.svm.core.genscavenge.graal.AllocationSnippets.checkArrayHub(AllocationSnippets.java:182)
        at com.zaxxer.hikari.pool.PoolEntry.<init>(PoolEntry.java:69)
        at com.zaxxer.hikari.pool.PoolBase.newPoolEntry(PoolBase.java:198)
        at com.zaxxer.hikari.pool.HikariPool.createPoolEntry(HikariPool.java:467)
        at com.zaxxer.hikari.pool.HikariPool.access$100(HikariPool.java:71)
        at com.zaxxer.hikari.pool.HikariPool$PoolEntryCreator.call(HikariPool.java:706)
        at com.zaxxer.hikari.pool.HikariPool$PoolEntryCreator.call(HikariPool.java:692)
        at java.util.concurrent.FutureTask.run(FutureTask.java:266)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        at java.lang.Thread.run(Thread.java:748)
        at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:473)
        at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:193)
```


13)
java.lang.NullPointerException: null
        at com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:184)
        at com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:155)
        at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:100)
        at slick.jdbc.hikaricp.HikariCPJdbcDataSource.createConnection(HikariCPJdbcDataSource.scala:14)
        at slick.jdbc.JdbcBackend$BaseSession.<init>(JdbcBackend.scala:494)
        at slick.jdbc.JdbcBackend$DatabaseDef.createSession(JdbcBackend.scala:46)
        at slick.jdbc.JdbcBackend$DatabaseDef.createSession(JdbcBackend.scala:37)
        at slick.basic.BasicBackend$DatabaseDef.acquireSession(BasicBackend.scala:249)
        at slick.basic.BasicBackend$DatabaseDef.acquireSession$(BasicBackend.scala:248)
        at slick.jdbc.JdbcBackend$DatabaseDef.acquireSession(JdbcBackend.scala:37)
        at slick.basic.BasicBackend$DatabaseDef$$anon$3.run(BasicBackend.scala:274)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
        at java.lang.Thread.run(Thread.java:748)
        at com.oracle.svm.core.thread.JavaThreads.threadStartRoutine(JavaThreads.java:473)
        at com.oracle.svm.core.posix.thread.PosixJavaThreads.pthreadStartRoutine(PosixJavaThreads.java:193)

"io.micrometer" % "micrometer-core" % "1.1.4"



14)

native-image -J-Xmx8G -H:+ReportExceptionStackTraces --no-fallback\
            -H:ConfigurationFileDirectories="native-config"   \
            --delay-class-initialization-to-runtime="org.postgresql.sspi.SSPIClient" \
            --allow-incomplete-classpath \
            --enable-http --enable-https --verbose --no-server -jar ./target/scala-2.12/bot.jar

