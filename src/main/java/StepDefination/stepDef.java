package StepDefination;

import org.testng.Reporter;

import Library.reportUtility;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;



public class stepDef extends reportUtility{

    @Given("^Prod and UAT Reports are available to compare$")
    public void _prod_and_uat_reports_are_available_to_compare() throws Throwable {
    	System.out.println("First given");
    	
      
    }

    @When("^User runs the Excel Utility$")
    public void _user_runs_the_excel_utility() throws Throwable {
        System.out.println("when");
    }

    @Then("Comparison Report is genrated with ProdData in First sheet , results in second Tab and Summary Report report in Third tab$")
    public void _comparison_report_is_genrated_with_proddata_in_first_sheet_results_in_second_tab_and_summary_report_report_in_third_tab() throws Throwable {
    	 System.out.println("Then");
    }

}