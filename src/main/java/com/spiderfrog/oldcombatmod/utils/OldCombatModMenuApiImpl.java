package com.spiderfrog.oldcombatmod.utils;

import com.spiderfrog.oldcombatmod.client.ConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import java.io.ObjectInputFilter;

public class OldCombatModMenuApiImpl implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return new ConfigScreen(parent);
        };
    }
}
