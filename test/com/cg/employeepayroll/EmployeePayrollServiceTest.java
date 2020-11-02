package com.cg.employeepayroll;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import com.cg.employeepayroll.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {
	Logger log = Logger.getLogger(EmployeePayrollServiceTest.class.getName());

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEnteries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "Diksha kalra", 100000.0),
				new EmployeePayrollData(2, "Bill Gates", 200000.0),
				new EmployeePayrollData(3, "mark Zuckerberg", 300000.0) };
		EmployeePayrollService employeePayrollService;
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(IOService.FILE_IO);
		long entries = employeePayrollService.countEntries(IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB()
			throws PayrollSystemException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("charlie", 3000000.0);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("charlie");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2019, 05, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(6, employeePayrollData.size());
	}

	@Test
	public void findSumAverageMinMaxCount_ofEmployees_ShouldMatchEmployeeCount() throws PayrollSystemException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> genderToAverageSalaryMap = employeePayrollService.getAvgSalary(IOService.DB_IO);
		Double avgSalaryFemale = 1000000.0;
		Assert.assertEquals(avgSalaryFemale, genderToAverageSalaryMap.get("F"));
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws PayrollSystemException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("mark", 5000000.0, LocalDate.now(), 'M');
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("mark");
		Assert.assertTrue(result);
	}

	@Test
	public void givenEmployeeWhenRemoved_ShouldRemainInDatabase() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		int countOfEmployeeRemoved = employeePayrollService.removeEmployeeFromPayroll("mark", IOService.DB_IO);
		Assert.assertEquals(1, countOfEmployeeRemoved);
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readActiveEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(5, employeePayrollData.size());
	}

	@Test
	public void given4Employee_WhenAddedToDB_ShouldMatchEmployeeEnteries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(0, "Patric Jane", 3000000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(0, "George Vanpelt", 2000000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(0, "Kimball Cho", 2000000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(0, "Wayne Rigsby", 2000000.0, LocalDate.now(), 'M') };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeesToPayrollUsingThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		log.info("Duration with thread: " + Duration.between(threadStart, threadEnd));
	}

	@Test
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(6, employeePayrollData.size());
	}

	@Test
	public void given4Employees_WhenUpdated_shouldSyncWithDB() throws PayrollSystemException {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "diksha", 1000000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(0, "deepali", 1000000.0, LocalDate.now(), 'F'),
				new EmployeePayrollData(0, "charlie", 5000000.0, LocalDate.now(), 'M') };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.updateEmployeeToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		log.info("Duration with thread: " + Duration.between(start, end));
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("charlie");
		Assert.assertTrue(result);
	}
}
