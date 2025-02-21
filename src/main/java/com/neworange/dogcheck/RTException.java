package com.neworange.dogcheck;

/**
 * @author winter
 * @version 1.0.0
 * @ date 2024/8/8 19:13
 * @ description
 */
public class RTException  extends RuntimeException {
    private int retval;

    public RTException() {
        this.retval = 0;
    }

    public RTException(int var1) {
        this.retval = var1;
    }

    public RTException(int var1, String var2) {
        super(var2);
        this.retval = var1;
    }

    public RTException(String var1) {
        super(var1);
        this.retval = 0;
    }

    public int HResult() {
        if (this.retval == 0) {
            int var1 = this.getMessage().lastIndexOf("0x");
            if (var1 != -1) {
                String var2 = this.getMessage().substring(var1 + 6);
                this.retval = Integer.parseInt(var2, 16);
            }
        }

        return this.retval;
    }
}
