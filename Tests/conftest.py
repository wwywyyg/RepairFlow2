import os

import pytest
from playwright.sync_api import sync_playwright
from pathlib import Path
from page.Customer.Customer_Create_Ticket import Customer_Create_Ticket
from page.Employee.employee_claim_ticket import employee_claim_ticket
from page.workflow.customer_work_flow import Customer_WorkFlow
from page.workflow.employee_work_flow import Employee_WorkFlow

# BASE_URL = "http://localhost:5173"
BASE_URL = os.getenv("BASE_URL", "http://localhost:3000")
# CUSTOMER_STORAGE_STATE = "testCode/pages/Setup/customer_auth.json"
BASE_DIR = Path(__file__).resolve().parent   # testCode
CUSTOMER_STORAGE_STATE = BASE_DIR / "page" / "Setup" / "customer_auth.json"
EMPLOYEE_STORAGE_STATE = BASE_DIR / "page" / "Setup" / "employee_auth.json"

@pytest.fixture(scope="session")
def playwright_instance():
    with sync_playwright() as p:
        yield p


@pytest.fixture(scope="session")
def browser(playwright_instance):
    browser = playwright_instance.chromium.launch(headless=False)
    yield browser
    browser.close()




@pytest.fixture(scope="session", autouse=True)
def setup_auth_states(browser):
    """
    Run once before all tests.
    Make sure customer_auth.json and employee_auth.json exist.
    """

    CUSTOMER_STORAGE_STATE.parent.mkdir(parents=True, exist_ok=True)

    # customer auth
    customer_context = browser.new_context()
    customer_page = customer_context.new_page()

    customer_page.goto(f"{BASE_URL}/login")
    customer_page.fill("input[name='email']", "customer@test.com")
    customer_page.fill("input[name='password']", "11223344")
    customer_page.click("button:has-text('Login')")
    customer_page.wait_for_url("http://localhost:5173/dashboard")

    customer_context.storage_state(path=CUSTOMER_STORAGE_STATE)
    customer_page.close()
    customer_context.close()

    # employee auth
    employee_context = browser.new_context()
    employee_page = employee_context.new_page()

    employee_page.goto(f"{BASE_URL}/login")
    employee_page.fill("input[name='email']", "employee@test.com")
    employee_page.fill("input[name='password']", "11223344")
    employee_page.click("button:has-text('Login')")
    employee_page.wait_for_url("http://localhost:5173/dashboard")


    employee_context.storage_state(path=EMPLOYEE_STORAGE_STATE)
    employee_page.close()
    employee_context.close()



'''
    guest
'''
@pytest.fixture(scope="function")
def guest_page(browser):
    context = browser.new_context()
    page = context.new_page()
    yield page
    page.close()
    context.close()

'''
    Customer 
'''


@pytest.fixture(scope="function")
def customer_context(browser):
    context = browser.new_context(storage_state=CUSTOMER_STORAGE_STATE)
    yield context
    context.close()


@pytest.fixture(scope="function")
def customer_page(customer_context):
    page = customer_context.new_page()
    page.goto(f"{BASE_URL}/dashboard")
    yield page
    page.close()


@pytest.fixture(scope="function")
def prepare_ticket(customer_page):
    create_ticket = Customer_Create_Ticket(customer_page)
    create_ticket.create_ticket("title","description","1","2")
    ticket_id = create_ticket.get_created_ticket_id_from_url()
    yield ticket_id




'''
    Employee 
'''

@pytest.fixture(scope="function")
def employee_context(browser):
    context  = browser.new_context(storage_state=EMPLOYEE_STORAGE_STATE)
    yield context
    context.close()

@pytest.fixture(scope="function")
def employee_page(employee_context):
    page = employee_context.new_page()
    page.goto(f"{BASE_URL}/dashboard")
    yield page
    page.close()


'''
    work flow ticket  
'''
#  ticket Status : ASSIGNED
@pytest.fixture(scope="function")
def prepare_full_workflow_ticket(prepare_ticket, employee_page):
    ticket_id = prepare_ticket
    employee_page = employee_claim_ticket(employee_page)
    employee_page.available_ticket_link.click()
    employee_page.claim_ticket_confirm(ticket_id)
    employee_page.my_ticket_link.click()
    employee_page.assert_ticket_claim_successfully(ticket_id)

    yield ticket_id


# status : ASSIGNED  ->  QUOTED  | Employee
@pytest.fixture(scope="function")
def prepare_quote_ticket(prepare_full_workflow_ticket,employee_page):
    ticket_id = prepare_full_workflow_ticket
    employee_page = Employee_WorkFlow(employee_page)
    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.quote_input("234.45")
    employee_page.save_change_button.click()
    employee_page.assert_status_from_assigned_to_quoted()

    yield ticket_id



#  status QUOTED -> AWAITING_DEVICE
@pytest.fixture(scope="function")
def prepare_awaiting_device_ticket(prepare_quote_ticket, customer_page):
    ticket_id = prepare_quote_ticket
    customer_page = Customer_WorkFlow(customer_page)
    customer_page.go_to_my_tickets_page()
    customer_page.view_ticket_details(ticket_id)
    customer_page.update_button.click()
    customer_page.confirm_quote()
    customer_page.assert_status_change_to_awaiting_device_after_confirm()

    yield ticket_id


# status AWAITING_DEVICE -> DEVICE_RECEIVED
@pytest.fixture(scope="function")
def prepare_device_received_ticket(prepare_awaiting_device_ticket, employee_page):
    ticket_id = prepare_awaiting_device_ticket
    employee_page = Employee_WorkFlow(employee_page)
    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.update_ticket_status("DEVICE_RECEIVED")
    employee_page.save_change_button.click()
    employee_page.assert_status_change_to_device_received()

    yield ticket_id

# status DEVICE_RECEIVED -> IN_PROGRESS
@pytest.fixture(scope="function")
def prepare_in_progress_ticket(prepare_device_received_ticket, employee_page):
    ticket_id = prepare_device_received_ticket
    employee_page = Employee_WorkFlow(employee_page)
    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.update_ticket_status("IN_PROGRESS")
    employee_page.save_change_button.click()
    employee_page.assert_status_change_to_in_progress_successfully()

    yield ticket_id

# status IN_PROGRESS -> READY_FOR_CONFIRMATION
@pytest.fixture(scope="function")
def prepare_ready_for_confirmation_ticket(prepare_in_progress_ticket, employee_page):
    ticket_id = prepare_in_progress_ticket
    employee_page = Employee_WorkFlow(employee_page)
    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.update_ticket_status("READY_FOR_CONFIRMATION")
    employee_page.save_change_button.click()
    employee_page.assert_status_should_change_to_ready_for_confirmation()

    yield ticket_id

# status READY_FOR_CONFIRMATION -> PAID
@pytest.fixture(scope="function")
def prepare_paid_ticket(prepare_ready_for_confirmation_ticket, customer_page):
    ticket_id = prepare_ready_for_confirmation_ticket
    customer_page = Customer_WorkFlow(customer_page)
    customer_page.go_to_my_tickets_page()
    customer_page.view_ticket_details(ticket_id)
    customer_page.update_button.click()
    customer_page.confirm_payment()
    customer_page.assert_status_should_change_to_paid()

    yield ticket_id

#  status PAID -> SHIPPED
@pytest.fixture(scope="function")
def prepare_shipped_ticket(prepare_paid_ticket, employee_page):
    ticket_id = prepare_paid_ticket
    employee_page = Employee_WorkFlow(employee_page)
    employee_page.go_to_my_tickets_page()
    employee_page.view_ticket_details(ticket_id)
    employee_page.update_button.click()
    employee_page.update_ticket_status("SHIPPED")
    employee_page.save_change_button.click()
    employee_page.asser_status_should_change_to_shipped()

    yield ticket_id

#  status SHIPPED -> DELIVERED
@pytest.fixture(scope="function")
def prepare_delivered_ticket(prepare_shipped_ticket, customer_page):
    ticket_id = prepare_shipped_ticket
    customer_page = Customer_WorkFlow(customer_page)
    customer_page.go_to_my_tickets_page()
    customer_page.view_ticket_details(ticket_id)
    customer_page.update_button.click()
    customer_page.confirm_delivered()
    customer_page.assert_status_should_change_to_delivered()

    yield ticket_id


