package com.cg.employeepayroll;

import java.util.Arrays;
import org.junit.Test;
import com.cg.employeepayroll.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {
	
	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEnteries() {
		EmployeePayrollData[] arrayOfEmps= {
				new EmployeePayrollData(1,"Diksha kalra", 100000.0),
				new EmployeePayrollData(1,"Bill Gates", 200000.0),
				new EmployeePayrollData(1,"mark Zuckerberg", 300000.0)
		};
		EmployeePayrollService employeePayrollService;
		employeePayrollService=new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		employeePayrollService.printData(IOService.FILE_IO);
	}
}
