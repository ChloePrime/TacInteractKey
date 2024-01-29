package mod.chloeprime.tacinteractkey.util;

import java.lang.reflect.Method;

public class InheritanceChecker<T> {

    public InheritanceChecker(Class<T> baseType, String methodName, Class<?>... paramTypes) {
        this.givenBaseType = baseType;
        this.methodName = methodName;
        this.paramTypes = paramTypes;
        this.realBaseType = getDeclareClass(baseType, methodName, paramTypes);
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
    private final ClassValue<Boolean> CACHE = new ClassValue<>() {
        @Override
        protected Boolean computeValue(Class<?> type) {
            return !realBaseType.equals(getDeclareClass(type, methodName, paramTypes));
        }
    };

    @SuppressWarnings("unchecked")
    private static <T> Class<? super T> getDeclareClass(Class<T> type, String methodName, Class<?>... paramTypes) {
        try {
            return (Class<? super T>) getDeclaredMethod(type, methodName, paramTypes).getDeclaringClass();
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Method getDeclaredMethod(Class<?> type, String methodName, Class<?>... paramTypes) throws NoSuchMethodException {
        Method method;
        try {
            method = type.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException ignored) {
            method = type.getDeclaredMethod(methodName, paramTypes);
            if (!method.trySetAccessible()) {
                throw new IllegalArgumentException("Inaccessible Method");
            }
        }
        return method;
    }
}
