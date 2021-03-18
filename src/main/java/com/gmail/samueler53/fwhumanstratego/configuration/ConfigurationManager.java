//package com.gmail.samueler53.fwhumanstratego.configuration;
//
//import com.gmail.samueler53.fwhumanstratego.FwHumanStratego;
//
//public class ConfigurationManager {
//
//    private static ConfigurationManager configInstance;
//    private final FwHumanStratego plugin;
//
//    private ConfigurationManager(FwHumanStratego plugin) {
//        if (configInstance != null) {
//            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
//        }
//        this.plugin = plugin;
//    }
//
//    public static ConfigurationManager getInstance() {
//        if (configInstance == null) {
//            configInstance = new ConfigurationManager(FwHumanStratego.getPlugin());
//        }
//        return configInstance;
//    }
//
//    public void reloadDefaultConfig() {
//        plugin.reloadDefaultConfig();
//    }
//
//}