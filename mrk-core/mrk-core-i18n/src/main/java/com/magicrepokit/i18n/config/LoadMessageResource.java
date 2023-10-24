package com.magicrepokit.i18n.config;

import org.springframework.core.io.Resource;

import java.util.Map;

public interface LoadMessageResource {
    Map<String, Map<String, String>> load();
}
