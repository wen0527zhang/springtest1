package com.neworange.gb28181.bean;

import javax.sip.Dialog;
import java.util.EventObject;

/**
 * @author winter
 * @version 1.0.0
 * @date 2024/4/10 18:45
 * @description
 */
public class DeviceNotFoundEvent extends EventObject {
    private String callId;

    /**
     * Constructs a prototypical Event.
     *
     * @param dialog
     * @throws IllegalArgumentException if source is null.
     */
    public DeviceNotFoundEvent(Dialog dialog) {
        super(dialog);
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }
}
