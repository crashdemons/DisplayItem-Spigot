/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  License, v. 2.0. If a copy of the MPL was not distributed with this
 *  file, You can obtain one at http://mozilla.org/MPL/2.0/ .
 */
package com.github.crashdemons.displayitem_spigot.plugins;

import static org.bukkit.Bukkit.getServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * Defines an abstract third-party-plugin compatibility class which can detect
 * and retrieves the plugin instance.
 *
 * @author crashdemons (crashenator at gmail.com)
 */
public abstract class CompatiblePlugin extends CompatiblePluginState implements Listener {
    protected boolean metaSupport=false;//indicates a meta-support class, not reliant on a *specific* plugin.
    private String pluginName = "";

    /**
     * The parent plugin requesting third-party plugin support.
     */
    protected Plugin parentPlugin = null;

    /**
     * Construct the plugin-compatibility object.
     * 
     * Overriding methods should not perform any actions that wouldn't work before plugins are loaded,
     * this is so that this can be called at parent plugin construction time.
     *
     * @param parentPlugin the current plugin requiring the compatibility (used
     * by child classes for events and logging)
     * @param pluginName the name of the third-party plugin to support
     */
    public CompatiblePlugin(Plugin parentPlugin, String pluginName) {
        this.parentPlugin = parentPlugin;
        this.pluginName = pluginName;
    }
    
    /**
     * Check and update whether the plugin is present at all.
     */
    @Override
    protected void determinePresence(){
        pluginPresent = metaSupport || (this.getPlugin() != null); //if the plugin name lookup returns null, the plugin is not present.
    }
    
    /**
     * Check and update whether support for this plugin is available (eg: may be overridden with version logic).
     * By default this updates support to whether the plugin is present.
     */
    @Override
    protected void determineSupport(){
        supportAvailable = pluginPresent;
    }
    
    @Override
    protected void onActivate(boolean enable){
        super.onActivate(enable);
        registerEvents();//we do this here because we cannot easily unregister all events later
        if(!isPluginPresent()){ parentPlugin.getLogger().info(pluginName + " not detected."); return; }
        if(!isSupportAvailable()){ parentPlugin.getLogger().info(pluginName + " support not available."); return; }
    }
    @Override
    protected void onEnable(){
        if(isPluginPresent() && isSupportAvailable()){
            parentPlugin.getLogger().info(pluginName + " support enabled by configuration."); 
        }
    }
    @Override
    protected void onDisable(){
        parentPlugin.getLogger().info(pluginName + " support disabled by configuration.");
    }


    /**
     * Get the plugin instance for the third-party plugin being supported.
     * This method has Bukkit look-up the plugin name every time it is called,
     * unless the plugin name is blank - you should use get() instead.
     *
     * @return the plugin instance, or null of the plugin name is blank
     */
    protected final Plugin getPlugin() {
        //System.out.println("get pluginName: "+pluginName);
        if (pluginName.isEmpty()) {
            return null;
        }
        return getServer().getPluginManager().getPlugin(pluginName);
    }


    /**
     * Gets the proper name of the third-party plugin this class attempts to
     * support.
     *
     * @return the plugin name
     */
    public String getName() {
        return pluginName;
    }

    /**
     * Get the plugin instance for the third-party plugin being supported.
     * This method only looks up the plugin name with Bukkit if it was initially
     * detected to be present.
     *
     * @return the plugin instance, or null if the plugin was not present at
     * startup.
     */
    public Plugin get() {
        if (isPluginPresent()) { return getPlugin(); }//save bukkit map lookup, marginal time save in benchmarks.
        return null;
    }
    
    /**
     * Register all Bukkit events for the plugin support class.
     * Note: This only registers events in the child class!
     * In child classes, you should call this in the overrided activate() method if you need event support.
     */
    private void registerEvents(){
        getServer().getPluginManager().registerEvents(this, parentPlugin);
    }
}
