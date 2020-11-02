package com.cg.employeepayroll;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
	public void givenEmployeePayrollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(4, employeePayrollData.size());
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdatedUsingPreparedStatement_ShouldSyncWithDB()
			throws PayrollSystemException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("terisa", 3000000.0);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("terisa");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(4, employeePayrollData.size());
	}

	@Test
	public void findSumAverageMinMaxCount_ofEmployees_ShouldMatchEmployeeCount() throws PayrollSystemException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> genderToAverageSalaryMap = employeePayrollService.getAvgSalary(IOService.DB_IO);
		Double avgSalaryMale = 3000000.0;
		Assert.assertEquals(avgSalaryMale, genderToAverageSalaryMap.get("M"));
		Double avgSalaryFemale = 3000000.0;
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
		Assert.assertEquals(3, employeePayrollData.size());
	}

	@Test
	public void given6Employee_WhenAddedToDB_ShouldMatchEmployeeEnteries() {
		EmployeePayrollData[] arrayOfEmps = {
				new EmployeePayrollData(0, "Patric Jane", 3000000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(0, "Grace Vanpelt", 2000000.0, LocalDate.now(), 'F'),
				new EmployeePayrollData(0, "Kimball Cho", 2000000.0, LocalDate.now(), 'M'),
				new EmployeePayrollData(0, "Wayne Rigsby", 2000000.0, LocalDate.now(), 'M') };
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		log.info("Duration without thread: " + Duration.between(start, end));
		Assert.assertEquals(8, employeePayrollService.countEntries(IOService.DB_IO));
	}
}
