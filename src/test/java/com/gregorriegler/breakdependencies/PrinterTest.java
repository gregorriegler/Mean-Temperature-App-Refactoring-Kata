package com.gregorriegler.breakdependencies;

import org.approvaltests.ApprovalUtilities;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.time.YearMonth;

public class PrinterTest {

    @Test
    void should_print() {
        ByteArrayOutputStream output = new ApprovalUtilities().writeSystemOutToStringBuffer();
        MeanTemperatureApplication printer = new MeanTemperatureApplication();
        printer.print(YearMonth.of(2020, 1), YearMonth.of(2020, 2), 1d);

        Approvals.verify(output);
    }
}
