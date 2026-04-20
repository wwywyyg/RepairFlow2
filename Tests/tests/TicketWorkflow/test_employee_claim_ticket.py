from page.Employee.employee_claim_ticket import employee_claim_ticket
from page.Customer.Customer_Create_Ticket import Customer_Create_Ticket
'''
    should all list available tickets
'''
def test_list_all_available_tickets(employee_page):
    employee_page = employee_claim_ticket(employee_page)
    employee_page.available_ticket_link.click()
    employee_page.assert_list_tickets_page_loaded()

'''
    should claim ticket successfully
'''
def test_should_claim_ticket_successfully(employee_page,prepare_ticket):
    ticket_id = prepare_ticket
    employee_page = employee_claim_ticket(employee_page)
    employee_page.available_ticket_link.click()


    employee_page.claim_ticket_confirm(ticket_id)
    employee_page.my_ticket_link.click()
    employee_page.assert_ticket_claim_successfully(ticket_id)

'''
    should not claim ticket successfully without confirmation
'''
def test_should_not_claim_ticket_successfully_without_confirmation(employee_page,prepare_ticket):
    ticket_id = prepare_ticket
    employee_page = employee_claim_ticket(employee_page)
    employee_page.available_ticket_link.click()
    employee_page.claim_ticket_cancel(ticket_id)
    employee_page.assert_ticket_claim_failed(ticket_id)

