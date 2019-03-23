package graal.substitution;

import akka.actor.LightArrayRevolverScheduler$;
import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;

import static com.oracle.svm.core.annotate.RecomputeFieldValue.Kind;

@TargetClass(LightArrayRevolverScheduler$.class)
public final class LightArrayRevolverSchedulerSubstitution {

    @Alias
    @RecomputeFieldValue(kind = Kind.FieldOffset, declClassName = "akka.actor.LightArrayRevolverScheduler$TaskHolder", name = "task")
    public long akka$actor$LightArrayRevolverScheduler$$taskOffset;
    
}
