package me.vrekt.arc.listener;

import me.vrekt.arc.Arc;
import me.vrekt.arc.check.management.CheckManager;

public interface ACheckListener {
    CheckManager CHECK_MANAGER = Arc.getCheckManager();
}
