/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.libraries.sainttx;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author sainttx
 */
public class ReflectionUtil {

    /*
     * The server version string to location NMS & OBC classes
     */
    private static String versionString;
    
    /**
     * Cache of FQN classes that we've searched for
     */
    private static Map<String,Class<?>> loadedClasses = new HashMap<>();

    /*
     * Cache of methods that we've found in particular classes
     */
    private static Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<Class<?>, Map<String, Method>>();

    /*
     * Cache of fields that we've found in particular classes
     */
    private static Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<Class<?>, Map<String, Field>>();

    /**
     * Gets the version string for NMS & OBC class paths
     *
     * @return The version string of OBC and NMS packages with a trailing period
     */
    public static String getVersion() {
        if (versionString == null) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
        }

        return versionString;
    }
    
    private static Class<?> getClassUncached(String class_fqn){
        if(class_fqn==null) throw new IllegalArgumentException("passed classname was null");
        Class<?> clazz = null;
        
        //System.out.println("DI-DEBUG: Retrieving class FQN "+class_fqn);

        try {
            clazz = Class.forName(class_fqn);
            //System.out.println("   DI-DEBUG: found "+clazz);
        } catch (Throwable t) {
            //t.printStackTrace();
            return clazz;
        }
        
        return clazz;
    }
    public static Class<?> getClass(String class_fqn){
        if(class_fqn==null) throw new IllegalArgumentException("passed classname was null");
        if (loadedClasses.containsKey(class_fqn)) {
            return loadedClasses.get(class_fqn);
        }
        Class<?> clazz = getClassUncached(class_fqn);
        //System.out.println("DI-DEBUG: RetrievedC "+clazz);
        loadedClasses.put(class_fqn, clazz);
        return clazz;
    }
    public static String getVersionedClassName(String package_fqn,String classname){
        return package_fqn+ "." + getVersion() + classname;
    }
    public static Class<?> getVersionedClass(String package_fqn,String classname){
        String class_fqn = getVersionedClassName(package_fqn,classname);
        Class<?> clazz = getClass(class_fqn);
        //System.out.println("DI-DEBUG: RetrievedV "+clazz);
        return clazz;
    }
    public static Class<?> getNmClass(String subpackage,String classname){
        return getClass("net.minecraft."+subpackage+"."+classname);
    }
    public static Class<?> getNmVersionedClass(String subpackage,String classname){
        return getVersionedClass("net.minecraft."+subpackage,classname);
    }
    public static Class<?> getNmsVersionedClass(String classname){
        return getNmVersionedClass("server",classname);
    }
    public static Class<?> getObcVersionedClass(String classname){
        Class<?> clazz = getVersionedClass("org.bukkit.craftbukkit",classname);
        //System.out.println("DI-DEBUG: RetrievedOBC "+clazz);
        return clazz;
    }
    
    

    /**
     * Get a Bukkit {@link Player} players NMS playerConnection object
     *
     * @param player The player
     * @return The players connection
     */
    public static Object getConnection(Player player) {
        if(player==null) throw new IllegalArgumentException("passed player was null");
        Method getHandleMethod = getMethod(player.getClass(), "getHandle");

        if (getHandleMethod != null) {
            try {
                Object nmsPlayer = getHandleMethod.invoke(player);
                Field playerConField = getField(nmsPlayer.getClass(), "playerConnection");
                return playerConField.get(nmsPlayer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Get a classes constructor
     *
     * @param clazz  The constructor class
     * @param params The parameters in the constructor
     * @return The constructor object
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... params) {
        if(clazz==null) throw new IllegalArgumentException("passed class was null");
        try {
            return clazz.getConstructor(params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Get a method from a class that has the specific paramaters
     *
     * @param clazz      The class we are searching
     * @param methodName The name of the method
     * @param params     Any parameters that the method has
     * @return The method with appropriate paramaters
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        if(clazz==null) throw new IllegalArgumentException("passed class was null");
        if (!loadedMethods.containsKey(clazz)) {
            loadedMethods.put(clazz, new HashMap<String, Method>());
        }

        Map<String, Method> methods = loadedMethods.get(clazz);

        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }

        try {
            Method method = clazz.getMethod(methodName, params);
            methods.put(methodName, method);
            loadedMethods.put(clazz, methods);
            return method;
        } catch (Exception e) {
            //e.printStackTrace();
            methods.put(methodName, null);
            loadedMethods.put(clazz, methods);
            return null;
        }
    }

    /**
     * Get a field with a particular name from a class
     *
     * @param clazz     The class
     * @param fieldName The name of the field
     * @return The field object
     */
    public static Field getField(Class<?> clazz, String fieldName) {
        if(clazz==null) throw new IllegalArgumentException("passed class was null");
        if (!loadedFields.containsKey(clazz)) {
            loadedFields.put(clazz, new HashMap<String, Field>());
        }

        Map<String, Field> fields = loadedFields.get(clazz);

        if (fields.containsKey(fieldName)) {
            return fields.get(fieldName);
        }

        try {
            Field field = clazz.getField(fieldName);
            fields.put(fieldName, field);
            loadedFields.put(clazz, fields);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            fields.put(fieldName, null);
            loadedFields.put(clazz, fields);
            return null;
        }
    }
}
