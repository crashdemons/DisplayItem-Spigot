/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.crashdemons.displayitem_spigot.plugins;

/**
 *
 * @author crashdemons (crashenator at gmail.com)
 */
abstract public class CompatiblePluginState {
    protected boolean pluginPresent=false;
    protected boolean supportAvailable=false;
    protected boolean supportEnabled=false;

    protected abstract void determinePresence();
    protected abstract void determineSupport();
    
    /**
     * indicate that support should be activated (at plugin enable time).
     * This method should only be called ONCE in the lifetime of the object.
     * @param state whether to enable or disable the plugin
     * @return whether plugin support is active (enabled, supported and present).
     */
    public boolean activate(boolean state){
        onActivate(state);
        setSupportEnabled(state);
        return isActive();
    }
    
    /**
     * Sets whether support is configured to be enabled or disabled.
     * @param supportEnabled 
     */
    public void setSupportEnabled(boolean supportEnabled) {
        if(supportEnabled != this.supportEnabled){
            if(supportEnabled) onEnable();
            else onDisable();
            this.supportEnabled = supportEnabled;
        }
    }
   
    /**
     * Indicate that support is configured to be disabled for the plugin.
     */
    public void disable(){ setSupportEnabled(false); }
   
    /**
     * indicate that support is configured to be enabled for the plugin.
     * @return whether plugin support is active (enabled, supported and present).
     */
    public boolean enable(){ setSupportEnabled(true); return isActive(); }
    
    /**
     * method that is called when the support class has been called to activate (either in enabled or disabled state)
     * @param state the enabled/disabled state with which support was activated
     */
    protected void onActivate(boolean state){
        determinePresence();
        determineSupport();
    }
    
    /**
     * method that is called when the support state has been indicated to change to Enabled.
     */
    protected void onEnable(){}
    /**
     * method that is called when the support state has been indicated to change to Disabled.
     */
    protected void onDisable(){}

    /**
     * Whether the plugin has been marked as present
     * @return the state described
     */
    public boolean isPluginPresent() { return pluginPresent;}
    
    /**
     * Whether plugin support has indicated that it is 'ready' (able to be used).
     * @return the state described
     */
    public boolean isSupportAvailable() { return supportAvailable;}
    /**
     * Whether plugin support has been 'enabled' officially (generally configured to be on).
     * @return the state described
     */
    public boolean isSupportEnabled() {return supportEnabled;}
    
    /**
     * Whether the plugin support is active for use (enabled, supported, and present)
     * @return the state described
     */
    public boolean isActive(){ return isSupportEnabled() && isSupportAvailable() && isPluginPresent(); }
    
}
