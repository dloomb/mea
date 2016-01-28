package com.meamobile.printicular_sdk.core.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrintServiceTest
{

    @Test
    public void it_has_the_correct_type() {
        PrintService s = new PrintService();
        assertEquals("print_services", s.getType());
    }

}