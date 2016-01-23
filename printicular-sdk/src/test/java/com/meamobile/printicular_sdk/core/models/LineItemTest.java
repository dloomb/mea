package com.meamobile.printicular_sdk.core.models;

import org.junit.Test;

import static org.junit.Assert.*;

public class LineItemTest extends ModelTest
{
    @Test
    public void it_has_the_correct_type() {
        LineItem line = new LineItem();
        assertEquals("line_items", line.getType());
    }
}