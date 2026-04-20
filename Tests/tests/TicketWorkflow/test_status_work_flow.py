from page.workflow.customer_work_flow import Customer_WorkFlow
from page.workflow.employee_work_flow import Employee_WorkFlow
from page.Customer.Customer_Create_Ticket import Customer_Create_Ticket
from page.Employee.employee_claim_ticket import employee_claim_ticket



'''
    ==========================================
    status assigned --> quoted
    employee flow 
    ==========================================
'''

'''
    should status change to quote fail with not with cancel save
'''
def test_status_assigned_to_quoted_workflow(employee_page, prepare_full_workflow_ticket):
    ticket_id = prepare_full_workflow_ticket
    employee_workflow = Employee_WorkFlow(employee_page)
    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.quote_input("123.12")
    employee_workflow.cancel_button.click()
    #     assert
    employee_workflow.assert_should_not_change_to_quoted()
    employee_workflow.page.reload()


    # should status not change status with invalid input
    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.quote_input("-123")
    employee_workflow.assert_invalid_amount_input_should_fail_quoted()
    employee_workflow.page.reload()



    # should status change to quote  successfully
    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.quote_input("123.12")
    employee_workflow.save_change_button.click()
    employee_workflow.assert_status_from_assigned_to_quoted()




'''
    ==========================================
    status quoted --> awaiting device
    customer flow 
    ==========================================
'''


def test_status_quoted_to_awaiting_device_workflow(customer_page,prepare_quote_ticket):
    customer_workflow = Customer_WorkFlow(customer_page)
    ticket_id = prepare_quote_ticket

    #  should not success with cancel confirmation
    customer_workflow.go_to_my_tickets_page()
    customer_workflow.view_ticket_details(ticket_id)
    customer_workflow.update_button.click()
    customer_workflow.cancel_quote()
    customer_workflow.assert_cancel_confirmation_status_not_changed()

    customer_workflow.page.reload()

    # should status change to awaiting device successfully
    customer_workflow.go_to_my_tickets_page()
    customer_workflow.view_ticket_details(ticket_id)
    customer_workflow.update_button.click()
    customer_workflow.confirm_quote()
    customer_workflow.assert_status_change_to_awaiting_device_after_confirm()


'''
    ==========================================
    status awaiting device --> device received  
    employee flow 
    ==========================================
'''

'''
    should status not change to device received with cancel save
'''
def test_status_awaiting_device_to_device_received(employee_page,prepare_awaiting_device_ticket):
    ticket_id = prepare_awaiting_device_ticket
    employee_workflow = Employee_WorkFlow(employee_page)

    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("DEVICE_RECEIVED")
    employee_workflow.cancel_button.click()
    # assert
    employee_workflow.assert_status_not_change_to_device_received_with_cancel_update()

    employee_workflow.page.reload()
    # should status not change if not select device received


    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("SHIPPED")
    employee_workflow.page.once("dialog", lambda dialog: dialog.accept())
    employee_workflow.save_change_button.click()
    employee_workflow.assert_status_not_change_to_device_received_with_cancel_update()
    employee_workflow.page.reload()

    # should status change to device received


    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("DEVICE_RECEIVED")
    employee_workflow.save_change_button.click()

    # assert
    employee_workflow.assert_status_change_to_device_received()


'''
    ==========================================
    device received --> in progress   
    employee flow 
    ==========================================
'''
def test_status_device_received_to_in_progress_workflow(employee_page,prepare_device_received_ticket):
    employee_workflow = Employee_WorkFlow(employee_page)
    ticket_id = prepare_device_received_ticket
    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("IN_PROGRESS")
    employee_workflow.cancel_button.click()

    employee_workflow.page.reload()
    employee_workflow.assert_status_not_change_to_in_progress()


    # should status not change if not select in progress
    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("DELIVERED")
    employee_workflow.assert_window_alert()
    employee_workflow.page.reload()
    employee_workflow.assert_status_not_change_to_in_progress()



    # should status change to in progress successfully

    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("IN_PROGRESS")
    employee_workflow.save_change_button.click()
    employee_workflow.assert_status_change_to_in_progress_successfully()


'''
    ==========================================
    in progress -->  ready for confirmation
    employee flow 
    ==========================================
'''

def test_status_in_progress_to_ready_for_confirmation_workflow(employee_page,prepare_in_progress_ticket):
    employee_workflow = Employee_WorkFlow(employee_page)
    ticket_id = prepare_in_progress_ticket

    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("READY_FOR_CONFIRMATION")
    employee_workflow.cancel_button.click()
    employee_workflow.page.reload()
    employee_workflow.assert_status_should_not_change_to_ready_for_confirmation()


    # should status not change if not select ready for confirmation


    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("SHIPPED")
    employee_workflow.cancel_button.click()
    employee_workflow.page.reload()
    employee_workflow.assert_status_should_not_change_to_ready_for_confirmation()




    # should status change to in ready for confirmation  successfully


    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("READY_FOR_CONFIRMATION")
    employee_workflow.save_change_button.click()
    employee_workflow.page.reload()
    employee_workflow.assert_status_should_change_to_ready_for_confirmation()


'''
    ==========================================
    ready for confirmation --> paid
    customer flow 
    ==========================================
'''

def test_status_ready_for_conformation_to_paid(customer_page,prepare_ready_for_confirmation_ticket):
    customer_workflow = Customer_WorkFlow(customer_page)
    ticket_id = prepare_ready_for_confirmation_ticket

    customer_workflow.go_to_my_tickets_page()
    customer_workflow.view_ticket_details(ticket_id)
    customer_workflow.update_button.click()
    customer_workflow.cancel_payment()
    customer_workflow.page.reload()
    customer_workflow.assert_status_should_not_change_to_paid()


    # should status change to paid successfully
    customer_workflow.go_to_my_tickets_page()
    customer_workflow.view_ticket_details(ticket_id)
    customer_workflow.update_button.click()
    customer_workflow.confirm_payment()
    # customer_workflow.page.reload()
    customer_workflow.assert_status_should_change_to_paid()


'''
    ==========================================
    paid --> shipped
    employee flow 
    ==========================================
'''

def test_status_paid_to_shipped_workflow(employee_page,prepare_paid_ticket):
    employee_workflow = Employee_WorkFlow(employee_page)
    ticket_id = prepare_paid_ticket

    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("SHIPPED")
    employee_workflow.cancel_button.click()
    employee_workflow.page.reload()
    employee_workflow.assert_status_should_not_change_to_shipped()


    # should status not change to shipped with wrong next status
    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("READY_FOR_CONFIRMATION")
    employee_workflow.assert_window_alert()
    employee_workflow.page.reload()
    employee_workflow.assert_status_should_not_change_to_shipped()



    # should status change to shipped successfully
    employee_workflow.go_to_my_tickets_page()
    employee_workflow.view_ticket_details(ticket_id)
    employee_workflow.update_button.click()
    employee_workflow.update_ticket_status("SHIPPED")
    employee_workflow.save_change_button.click()
    employee_workflow.asser_status_should_change_to_shipped()



'''
    ==========================================
    shipped --> delivered
    customer flow 
    ==========================================
'''

def test_status_shipped_to_delivered(customer_page,prepare_shipped_ticket):
    customer_workflow = Customer_WorkFlow(customer_page)
    ticket_id = prepare_shipped_ticket

    # should not success with cancel conformation
    customer_workflow.go_to_my_tickets_page()
    customer_workflow.view_ticket_details(ticket_id)
    customer_workflow.update_button.click()
    customer_workflow.cancel_delivered()
    customer_workflow.page.reload()
    customer_workflow.assert_status_should_not_change_to_delivered()



    # should satus change to delivered successfully
    customer_workflow.go_to_my_tickets_page()
    customer_workflow.view_ticket_details(ticket_id)
    customer_workflow.update_button.click()
    customer_workflow.confirm_delivered()
    customer_workflow.assert_status_should_change_to_delivered()