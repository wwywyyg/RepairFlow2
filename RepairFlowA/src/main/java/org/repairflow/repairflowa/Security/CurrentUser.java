package org.repairflow.repairflowa.Security;

import java.lang.annotation.*;

/**
 * @author guangyang
 * @date 11/25/25 PM9:12
 * @description TODO: Description
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
