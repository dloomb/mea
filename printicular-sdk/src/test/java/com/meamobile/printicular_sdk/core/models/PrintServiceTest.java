package com.meamobile.printicular_sdk.core.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrintServiceTest extends ModelTest
{

    @Test
    public void it_has_the_correct_type() {
        PrintService s = new PrintService();
        assertEquals("print_services", s.getType());
    }

    @Test
    public void it_is_auto_confirmable() {
        PrintService s = getNewPrintServiceInstance();
        assertTrue(s.isAutoConfirmable());
    }

}