package mod.chloeprime.tacinteractkey.util;

import java.lang.invoke.MethodHandles;

public class InheritanceChecker<T> {

    public InheritanceChecker(Class<T> baseType, String methodName, Class<?>... paramTypes) {
        this.givenBaseType = baseType;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.realBaseType = getRealBaseType(baseType, methodName, paramTypes);
    }

    public Class<T> getBaseType() {
        return givenBaseType;
    }

    public boolean isInherited(Class<? extends T> typeToCheck) {
        return CACHE.get(typeToCheck);
    }

    private final Class<T> givenBaseType;
    private final String methodName;
    private final Class<?>[] paramTypes;
    private final Class<? super T> realBaseType;
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private final ClassValue<Boolean> CACHE = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            try {
                var handle = LOOKUP.unreflect(type.getMethod(methodName, paramTypes));
                return !realBaseType.equals(LOOKUP.revealDirect(handle).getDeclaringClass());
            } catch (NoSuchMethodException | IllegalAccessException e) {
                return false;
            }
        }
    };

    @SuppressWarnings("unchecked")
    private static <T> Class<? super T> getRealBaseType(Class<T> type, String method, Class<?>... paramTypes) {
        try {
            var handle = LOOKUP.unreflect(type.getMethod(method, paramTypes));
            return (Class<? super T>) LOOKUP.revealDirect(handle).getDeclaringClass();
        } catch (IllegalAccessException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}
