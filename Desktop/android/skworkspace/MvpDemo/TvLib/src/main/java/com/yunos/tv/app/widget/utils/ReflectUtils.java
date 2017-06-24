//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.yunos.tv.app.widget.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectUtils {
    protected static final boolean DEBUG = false;

    public ReflectUtils() {
    }

    public static boolean getInternalBoolean(String inerClass, String fieldName) {
        boolean bool = false;

        try {
            Class e = Class.forName(inerClass);
            Object obj = e.newInstance();
            Field field = e.getField(fieldName);
            makeAccessible(field);
            bool = field.getBoolean(obj);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return bool;
    }

    public static int getInternalInt(String inerClass, String fieldName) {
        int id = 0;

        try {
            Class e = Class.forName(inerClass);
            Object obj = e.newInstance();
            Field field = e.getField(fieldName);
            makeAccessible(field);
            id = field.getInt(obj);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return id;
    }

    public static int[] getInternalIntArray(String inerClass, String fieldName) {
        int[] id = new int[0];

        try {
            Class e = Class.forName(inerClass);
            Object obj = e.newInstance();
            Field field = e.getField(fieldName);
            makeAccessible(field);
            id = (int[])field.get(obj);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return id;
    }

    public static Object invokeMethod(Object object, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        Method method = getDeclaredMethod(object, methodName, parameterTypes);
        if(method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
        } else {
            method.setAccessible(true);

            try {
                return method.invoke(object, parameters);
            } catch (Exception var6) {
                throw convertReflectionExceptionToUnchecked(var6);
            }
        }
    }

    public static Object invokeMethod(Object object, String methodName, Object[] parameters) {
        Class[] parameterTypes = new Class[parameters.length];
        int method = 0;

        for(int e = parameters.length; method < e; ++method) {
            parameterTypes[method] = parameters[method].getClass();
        }

        Method var7 = getDeclaredMethod(object, methodName, parameterTypes);
        if(var7 == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + object + "]");
        } else {
            var7.setAccessible(true);

            try {
                return var7.invoke(object, parameters);
            } catch (Exception var6) {
                throw convertReflectionExceptionToUnchecked(var6);
            }
        }
    }

    public static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
        return getDeclaredMethod(object.getClass(), methodName, parameterTypes);
    }

    private static Method getDeclaredMethod(Class<?> object, String methodName, Class<?>[] parameterTypes) {
        Class superClass = object;

        while(superClass != Object.class) {
            try {
                return superClass.getDeclaredMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException var5) {
                superClass = superClass.getSuperclass();
            }
        }

        return null;
    }

    public static Object invokeSuperMethod(Object owner, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Exception {
        Method method = getDeclaredMethod(owner.getClass().getSuperclass(), methodName, parameterTypes);
        if(method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + owner + "]");
        } else {
            method.setAccessible(true);

            try {
                return method.invoke(owner, parameters);
            } catch (Exception var6) {
                throw convertReflectionExceptionToUnchecked(var6);
            }
        }
    }

    public static Object getProperty(Object object, String fieldName) throws Exception {
        Field field = getDeclaredField(object, fieldName);
        if(field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        } else {
            makeAccessible(field);
            Object result = null;

            try {
                result = field.get(object);
            } catch (IllegalAccessException var5) {
                var5.printStackTrace();
            }

            return result;
        }
    }

    private static void makeAccessible(Field field) {
        if(!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }

    }

    private static Field getDeclaredField(Object object, String fieldName) {
        Class superClass = object.getClass();

        while(superClass != Object.class) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException var4) {
                superClass = superClass.getSuperclass();
            }
        }

        return null;
    }

    private static Field getDeclaredField(Class<?> object, String fieldName) {
        Class superClass = object;

        while(superClass != Object.class) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException var4) {
                superClass = superClass.getSuperclass();
            }
        }

        return null;
    }

    public static void setProperty(Object object, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException {
        Field field = getDeclaredField(object, fieldName);
        if(field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");
        } else {
            makeAccessible(field);

            try {
                field.set(object, value);
            } catch (IllegalAccessException var5) {
                var5.printStackTrace();
            }

        }
    }

    public static Object getStaticProperty(String className, String fieldName) {
        try {
            Class e = Class.forName(className);
            Field field = getDeclaredField(e, fieldName);
            if(field == null) {
                throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + className + "]");
            } else {
                makeAccessible(field);
                Object result = null;

                try {
                    result = field.get(e);
                } catch (IllegalAccessException var6) {
                    var6.printStackTrace();
                }

                return result;
            }
        } catch (Exception var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public static Object invokeStaticMethod(String className, String methodName, Object[] args) throws Exception {
        Class[] argsClass = new Class[args.length];
        int i = 0;

        for(int j = args.length; i < j; ++i) {
            argsClass[i] = args[i].getClass();
        }

        return invokeStaticMethod(className, methodName, argsClass, args);
    }

    public static Object invokeStaticMethod(String className, String methodName, Class<?>[] parameterTypes, Object[] parameters) {
        try {
            Class e = Class.forName(className);
            Method method = getDeclaredMethod(e, methodName, parameterTypes);
            if(method == null) {
                throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + className + "]");
            } else {
                method.setAccessible(true);
                return method.invoke((Object)null, parameters);
            }
        } catch (Exception var6) {
            throw convertReflectionExceptionToUnchecked(var6);
        }
    }

    public static Object newInstance(String className, Object[] args) throws Exception {
        Class newoneClass = Class.forName(className);
        Class[] argsClass = new Class[args.length];
        int cons = 0;

        for(int j = args.length; cons < j; ++cons) {
            argsClass[cons] = args[cons].getClass();
        }

        Constructor var6 = newoneClass.getConstructor(argsClass);
        var6.setAccessible(true);
        return var6.newInstance(args);
    }

    public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        return convertReflectionExceptionToUnchecked((String)null, e);
    }

    public static RuntimeException convertReflectionExceptionToUnchecked(String desc, Exception e) {
        desc = desc == null?"Unexpected Checked Exception.":desc;
        return (RuntimeException)(!(e instanceof IllegalAccessException) && !(e instanceof IllegalArgumentException) && !(e instanceof NoSuchMethodException)?(e instanceof InvocationTargetException?new RuntimeException(desc, ((InvocationTargetException)e).getTargetException()):(e instanceof RuntimeException?(RuntimeException)e:new RuntimeException(desc, e))):new IllegalArgumentException(desc, e));
    }
}
